package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.ImageInfoMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.FileInfoMapper;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.impl.file.FileImageHandle;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 服务类实现
 * {@code @date:} 			2023-12-08 16:48:27
 * {@code @version:}: 		1.0
 */
@Slf4j
@Primary
@Service
public class FileInfoServiceImpl extends BaseServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {


    @Autowired
    private FileConfig fileConfig;


    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public String save(InputStream fileIo, FileInfo fileInfo, Map funcMap, FileInfoSaveInterceptor interceptor){
        if(funcMap == null) funcMap = new HashMap<>();
        List<InputStream> inputStreams = new ArrayList<>();
        inputStreams.add(fileIo);
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
            this.save(fileInfo, funcMap, interceptor);
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

    @Transactional(rollbackFor = Exception.class)
    private boolean save(FileInfo entity, Map funcMap, FileInfoSaveInterceptor interceptor) {
        if(ValueUtils.isBlank(entity.getId())){
            entity.setId(IdUtils.nextId());
        }
        boolean saveRes = super.save(entity);
        if(interceptor != null){
            interceptor.afterSave(saveRes, funcMap, entity);
        }
        return saveRes;
    }

    public void deleteFile(String filePath){
        String basePath;
        if(filePath.startsWith(this.fileConfig.getDataPath())){
            basePath = this.fileConfig.getDataPath();
        }else if(filePath.startsWith(this.fileConfig.getTempPath())){
            basePath = this.fileConfig.getTempPath();
        }else{
            throw new BusinessException("不支持删除文件资源以外的数据");
        }
        String relativePath = filePath.substring(basePath.length());
        while (relativePath.length() > 0 && relativePath.contains("/")){
            relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String absolutePath = basePath + "/" + relativePath;
            File removeFile = new File(absolutePath);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> cleanFile() {
        List<Long> fileIds = this.baseMapper.selectDeletedIds();
        if(ValueUtils.isBlank(fileIds)){
            log.info("file deleted fileIds empty");
            return null;
        }
        log.info("file deleted fileIds size:{} values:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
        List<String> fingerprints = this.baseMapper.selectCanDeletedFingerprintsByIds(fileIds);
        if(ValueUtils.isBlank(fingerprints)){
            log.info("file select fingerprints empty");
            return null;
        }
        log.info("file select fingerprints size:{} values:{}", fingerprints.size(), ValueUtils.toStrings(fingerprints));
        int delRes = this.baseMapper.deleteByIds(fileIds);
        log.info("file deleted ids size:{} values:{}", delRes, fileIds);

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
            this.deleteFile(FileUtils.concatAbsolutePath(this.fileConfig.getDataPath(), fingerprintPath));
        }
        return fingerprints;
    }
}
