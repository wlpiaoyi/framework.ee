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
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IVideoInfoService;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
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
    @Autowired
    IImageInfoService imageInfoService;

    @Autowired
    IVideoInfoService videoInfoService;

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    @Override
    public String save(InputStream fileIo, FileInfo fileInfo, Map funcMap){
        return this.fileInfoService.save(fileIo, fileInfo, funcMap, this);
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
    @Autowired
    private FileVideoHandle fileVideoHandle;

    @Override
    public void download(FileInfo fileInfo, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        List<OutputStream> outputStreams = new ArrayList<>();
        List<InputStream> inputStreams = new ArrayList<>();
        try{
            String dataType = MapUtils.getString(funcMap, "dataType", "org");
            if(this.fileImageHandle.canDownloadByThumbnail(fileInfo.getSuffix(), dataType)){
                fileInfo = this.fileImageHandle.getThumbnailFileInfo(this, fileInfo);
            }else if(this.fileVideoHandle.canDownloadByScreenshot(fileInfo.getSuffix(), dataType)){
                fileInfo = this.fileVideoHandle.getScreenshotFileInfo(this, fileInfo);
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
        List<Long> fileIds = this.videoInfoService.cleanVideo();
        if(ValueUtils.isNotBlank(fileIds)){
            log.info("file cleanVideo fileIds size:{} values:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
            boolean delRes = this.fileInfoService.deleteLogic(fileIds);
            log.info("file deleteLogic fileIds delRes:{}", delRes);
        }else{
            log.info("file cleanVideo fileIds empty");
        }
        fileIds = this.imageInfoService.cleanImage();
        if(ValueUtils.isNotBlank(fileIds)){
            log.info("file cleanImage fileIds size:{} values:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
            boolean delRes = this.fileInfoService.deleteLogic(fileIds);
            log.info("file deleteLogic fileIds delRes:{}", delRes);
        }else{
            log.info("file cleanImage fileIds empty");
        }
        List<String> fingerprints = this.fileInfoService.cleanFile();
        return fingerprints;
    }


    @Transactional(rollbackFor = Exception.class)
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
           return;
        }
        if(this.fileVideoHandle.afterSaveHandle(this, entity, funcMap)){
            log.info("file handle video");
            return;
        }
    }

    @Override
    public void afterUpdate(boolean updateRes, Map funcMap, FileInfo entity) {
        if(!updateRes){
            throw new BusinessException("更新-失败");
        }
    }
}
