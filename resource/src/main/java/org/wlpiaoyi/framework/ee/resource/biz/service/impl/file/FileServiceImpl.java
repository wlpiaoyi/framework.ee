package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.SystemException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/8 16:51
 * {@code @version:}:       1.0
 */
@Slf4j
@Primary
@Service
public class FileServiceImpl implements IFileService, IFileInfoService.FileInfoSaveInterceptor, IFileInfoService.FileInfoUpdateInterceptor {

    @Autowired
    FileConfig fileConfig;

    @Autowired
    IFileInfoService fileInfoService;

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    @Override
    public String save(InputStream fileIo, FileInfo fileInfo, Map funcMap){
        return this.save(fileIo, fileInfo, funcMap, true);
    }
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    String save(InputStream fileIo, FileInfo fileInfo, Map funcMap, boolean hasCallback){
        if(funcMap == null) funcMap = new HashMap<>();
        List<InputStream> inputStreams = new ArrayList<>();
        List<String> removePaths = MapUtils.get(funcMap, "removePaths", new ArrayList<>());
        boolean fileExists = false;
        try{
            String tempFilePath = FileUtils.writeFileToTargetPath(fileIo, this.fileConfig.getTempPath());
            String fingerprintHex = FileUtils.getFingerprintHex(new File(tempFilePath));
            if(ValueUtils.isBlank(fileInfo.getId())){
                fileInfo.setId(IdUtils.nextId());
            }
            if(ValueUtils.isBlank(fileInfo.getName())){
                throw new BusinessException("文件名称不能为空");
            }
            if(ValueUtils.isBlank(fileInfo.getSuffix())){
                if(fileInfo.getName().contains(".")){
                    fileInfo.setSuffix(fileInfo.getName().substring(fileInfo.getName().lastIndexOf(".") + 1));
                }
            }
            fileInfo.setFingerprint(this.fileConfig.parseFingerprintHexTo(fingerprintHex));
            fileInfo.setSize(DataUtils.getSize(tempFilePath));
            fileInfo.setToken(this.fileConfig.dataEncode(this.fileConfig.getAesCipher().encrypt(fileInfo.getId().toString().getBytes())));
            String fileSign = null;
            if(fileInfo.getIsVerifySign() == 1){
                FileInputStream orgFileIo = new FileInputStream(this.fileConfig.getFilePathByFingerprintHex(fingerprintHex));
                inputStreams.add(orgFileIo);
                InputStream tokenByteInput = new ByteArrayInputStream(fileInfo.getToken().getBytes());
                final String dataSign = this.fileConfig.dataEncode(this.fileConfig.getSignVerify().sign(orgFileIo));
                final String tokenSign = this.fileConfig.dataEncode(this.fileConfig.getSignVerify().sign(tokenByteInput));
                tokenByteInput.close();
                fileSign = tokenSign + "," + dataSign;
                try {
                    orgFileIo.close();
                    inputStreams.remove(orgFileIo);
                } catch (Exception e) {
                    throw e;
                }
            }
            String fileLocalPath = this.fileConfig.getFilePathByFingerprintHex(fingerprintHex);
            fileExists = FileUtils.mergeByFingerprintHex(new File(tempFilePath), fingerprintHex, this.fileConfig.getDataPath());
            funcMap.put("tempFilePath", tempFilePath);
            funcMap.put("fileLocalPath", fileLocalPath);
            removePaths.add(tempFilePath);
            if(!fileExists){
                removePaths.add(fileLocalPath);
            }
            funcMap.put("removePaths", removePaths);
            if(hasCallback){
                this.fileInfoService.save(fileInfo, funcMap, this);
            }else{
                this.fileInfoService.save(fileInfo, funcMap, null);
            }
            log.info("file upload localPath: {}", fileLocalPath);
            return fileSign;
        } catch (Exception e){
            for(String filePath : removePaths){
                this.deleteFile(filePath);
            }
            throw e;
        }finally {
            try{
                if(ValueUtils.isNotBlank(inputStreams)){
                    for (InputStream inputStream : inputStreams){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static Map<String, String> contentTypeMap = new HashMap(){{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("gif", "image/gif");

        put("pdf", "application/pdf");

        put("docx", "application/msword");
        put("doc", "application/msword");
        put("xlsx", "application/vnd.ms-excel");
        put("xls", "application/vnd.ms-excel");

        put("mp4", "video/mp4");
        put("wmv", "video/wmv");
        put("mp3", "audio/mp3");

        put("default", "application/octet-stream");
    }};


    @SneakyThrows
    @Override
    public void download(String token, String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        Long id = new Long(new String(this.fileConfig.getAesCipher().decrypt(this.fileConfig.dataDecode(token))));
        FileInfo fileInfo = this.fileInfoService.getById(id);
        if(fileInfo == null){
            throw new SystemException("没有找到文件");
        }
        String fmFingerprint = ValueUtils.bytesToHex(this.fileConfig.dataDecode(fileInfo.getFingerprint()));
        String ogFingerprint = ValueUtils.bytesToHex(this.fileConfig.dataDecode(fingerprint));
        if(!fmFingerprint.equals(ogFingerprint)){
            throw new SystemException("文件验证失败");
        }
        this.download(fileInfo, funcMap,request, response);
    }


    @Autowired
    private FileImageHandle fileImageHandle;
    @Override
    public void download(FileInfo fileInfo, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        List<OutputStream> outputStreams = new ArrayList<>();
        List<InputStream> inputStreams = new ArrayList<>();
        try{
            String dataType = MapUtils.getString(funcMap, "dataType", "org");
            if(this.fileImageHandle.canDownloadFileInfoHandle(fileInfo, dataType)){
                fileInfo = this.fileImageHandle.downloadFileInfoHandle(this, fileInfo);
            }
            String ogPath = this.fileConfig.getFilePathByFingerprint(fileInfo.getFingerprint());
            if(fileInfo.getIsVerifySign() == 1){
                String fileSign = request.getHeader("file-sign");
                if(ValueUtils.isBlank(fileSign)){
                    throw new SystemException("无权访问文件");
                }
               try{
                   String[] args = fileSign.split(",");
                   String tokenSign = args[0];
                   String dataSign = args[1];
                   ByteArrayInputStream bis = new ByteArrayInputStream(fileInfo.getToken().getBytes());
                   inputStreams.add(bis);
                   if(!this.fileConfig.getSignVerify().verify(bis, this.fileConfig.dataDecode(tokenSign))){
                       throw new SystemException("无权访问文件");
                   }
                   bis.close();
                   inputStreams.remove(bis);
                   FileInputStream fis = new FileInputStream(ogPath);
                   inputStreams.add(fis);
                   if(!this.fileConfig.getSignVerify().verify(fis, this.fileConfig.dataDecode(dataSign))){
                       throw new SystemException("无权访问文件");
                   }
                   fis.close();
                   inputStreams.remove(fis);
               }catch (Exception e){
                   throw new SystemException("无权访问文件", e);
               }
            }
            String ft = fileInfo.getSuffix();
            if(ValueUtils.isNotBlank(ft)){
                ft = ft.toLowerCase(Locale.ROOT);
            }
            String contentType = contentTypeMap.get(ft);
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }
            response.setContentType(contentType);
            response.setCharacterEncoding(Charsets.UTF_8.name());
            String readType = MapUtils.getString(funcMap, "readType", "inline");
            String filename = fileInfo.getName();
            if(!filename.contains(".")){
                filename += "." + fileInfo.getSuffix();
            }
            response.setHeader("Content-disposition", readType + ";filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8.name()));
//            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getName(), Charsets.UTF_8.name()));

            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            outputStreams.add(sos);
            FileInputStream fis = new FileInputStream(ogPath);
            inputStreams.add(fis);
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = fis.read(data, 0, data.length)) != -1) {
                sos.write(data, 0, nRead);
                sos.flush();
            }
            sos.close();
            outputStreams.remove(sos);
            fis.close();
            inputStreams.remove(fis);
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw (BusinessException)e;
            }
            if(e instanceof SystemException){
                throw (SystemException)e;
            }
            throw new SystemException("文件读取异常", e);
        }finally {

            if(ValueUtils.isNotBlank(outputStreams)){
                for (OutputStream outputStream : outputStreams){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(ValueUtils.isNotBlank(inputStreams)){
                for (InputStream inputStream : inputStreams){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> cleanFile() {
        File tempPath = new File(this.fileConfig.getTempPath());
        if(!tempPath.exists()){
            tempPath.mkdirs();
        }
        File dataPath = new File(this.fileConfig.getDataPath());
        if(!dataPath.exists()){
            dataPath.mkdirs();
        }
        List<String> fingerprints = this.fileInfoService.cleanFile();
        if(ValueUtils.isBlank(fingerprints)){
            return null;
        }
        for (String fingerprint : fingerprints){
            String fingerprintHex = ValueUtils.bytesToHex(this.fileConfig.dataDecode(fingerprint));
            String filePath = this.fileConfig.getFilePathByFingerprintHex(fingerprintHex);
            File file = new File(filePath);
            if(file.exists()){
                if(file.delete()){
                    log.info("file.clean delete success for file [{}]", file.getAbsolutePath());
                }else{
                    log.warn("file.clean delete failed for file [{}]", file.getAbsolutePath());
                }
            }
            String fingerprintPath = FileUtils.getMd5PathByFingerprintHex(fingerprintHex);
            this.deleteFile(fingerprintPath);
        }
        return fingerprints;
    }

    private void deleteFile(String fingerprintPath){
        while (fingerprintPath.length() > 0 && fingerprintPath.contains("/")){
            fingerprintPath = fingerprintPath.substring(0, fingerprintPath.lastIndexOf("/"));
            String absPath = this.fileConfig.getDataPath() + "/" + fingerprintPath;
            File removeFile = new File(absPath);
            if(ValueUtils.isNotBlank(removeFile.list())){
                break;
            }
            if(removeFile.delete()){
                log.info("file.clean delete success for path [{}]", removeFile.getAbsolutePath());
            }else{
                log.warn("file.clean delete failed for path [{}]", removeFile.getAbsolutePath());
            }
        }
    }

    @SneakyThrows
    @Override
    public void afterSave(boolean saveRes, Map funcMap, FileInfo entity) {
        if(!saveRes){
            throw new BusinessException("保存失败");
        }
        if(ValueUtils.isBlank(entity.getSuffix())){
            return;
        }
        if(this.fileImageHandle.afterSaveHandle(this, entity, funcMap)){
           log.info("file handle image");
        }
    }

    @Override
    public void afterUpdate(boolean updateRes, Map funcMap, FileInfo entity) {
        if(!updateRes){
            throw new BusinessException("更新-失败");
        }
    }
}
