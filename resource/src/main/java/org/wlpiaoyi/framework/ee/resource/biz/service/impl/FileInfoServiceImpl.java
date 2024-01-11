package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.FileInfoMapper;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IVideoInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.impl.file.FileImageHandle;
import org.wlpiaoyi.framework.ee.resource.biz.service.impl.file.FileVideoHandle;
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

    @Autowired
    private IImageInfoService imageInfoService;

    @Autowired
    private IVideoInfoService videoInfoService;

    public FileInfoVo detail(Long id){
        FileInfo fileInfo = this.getById(id);
        if(fileInfo == null){
            return null;
        }
        FileInfoVo fileInfoVo = ModelWrapper.parseOne(fileInfo, FileInfoVo.class);

        if(FileImageHandle.isSupportSuffix(fileInfoVo.getSuffix())){
            fileInfoVo.setExpandInfo(this.imageInfoService.detailByFileId(id));
        }else if(FileVideoHandle.isSupportSuffix(fileInfoVo.getSuffix())){
            fileInfoVo.setExpandInfo(this.videoInfoService.detailByFileId(id));
        }
        return fileInfoVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public String save(Object fileIo, FileInfo entity, Map funcMap, FileInfoSaveInterceptor interceptor){
        if(funcMap == null) funcMap = new HashMap<>();
        List<InputStream> inputStreams = new ArrayList<>();
        List<String> removePaths = MapUtils.get(funcMap, "removePaths", new ArrayList<>());
        funcMap.put("removePaths", removePaths);
        Map<String, String> unMoveMap = MapUtils.getMap(funcMap, "unMoveMap");
        boolean isRootDone = false;
        if(unMoveMap == null){
            isRootDone = true;
            unMoveMap = new HashMap<>();
            funcMap.put("unMoveMap", unMoveMap);
        }
        try{
            String tempFilePath;
            if(fileIo instanceof MultipartFile){
                MultipartFile multipartFile = (MultipartFile) fileIo;
                tempFilePath = FileUtils.moveFileToTempPath(multipartFile, this.fileConfig.getTempPath());
            }else if (fileIo instanceof InputStream){
                InputStream io = (InputStream) fileIo;
                tempFilePath = FileUtils.writeFileToTempPath(io, this.fileConfig.getTempPath());
            }else{
                throw new BusinessException("不支持的文件输入");
            }
            String fingerprintHex = FileUtils.getFingerprintHex(new File(tempFilePath));
            if(ValueUtils.isBlank(entity.getId())){
                entity.setId(IdUtils.nextId());
            }
            if(ValueUtils.isBlank(entity.getName())){
                throw new BusinessException("文件名称不能为空");
            }
            if(ValueUtils.isBlank(entity.getSuffix())){
                if(entity.getName().contains(".")){
                    entity.setSuffix(entity.getName().substring(entity.getName().lastIndexOf(".") + 1));
                }
            }
            entity.setFingerprint(this.fileConfig.parseFingerprintHexTo(fingerprintHex));
            entity.setSize(DataUtils.getSize(tempFilePath));
            String fileSign = null;
            if(entity.getIsVerifySign() == 1){
                fileSign = this.fileConfig.signFile(entity.getId(), entity.getFingerprint());
            }
            unMoveMap.put(fingerprintHex, tempFilePath);
            removePaths.add(tempFilePath);
            this.save(entity, funcMap, interceptor);
            if (isRootDone){
                if(ValueUtils.isBlank(unMoveMap)){
                    throw new BusinessException("没有移动的文件");
                }
                for (Map.Entry<String, String> entry : unMoveMap.entrySet()){
                    boolean fileExists = FileUtils.mergeByFingerprintHex(new File(entry.getValue()), entry.getKey(), this.fileConfig.getDataPath());
                    log.info("file dataPath fileExists:{}, fingerprintHex: {}", fileExists, fingerprintHex);
                }
            }
            return fileSign;
        }finally {
            if(isRootDone){
                removePaths.forEach(this::deleteFile);
            }
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
        filePath = filePath.replaceAll("\\\\", "/");
        String basePath;
        if(filePath.startsWith(this.fileConfig.getDataPath().replaceAll("\\\\", "/"))){
            basePath = this.fileConfig.getDataPath();
        }else if(filePath.startsWith(this.fileConfig.getTempPath().replaceAll("\\\\", "/"))){
            basePath = this.fileConfig.getTempPath();
        }else{
            throw new BusinessException("不支持删除文件资源以外的数据");
        }
        File removeFile = new File(filePath);
        if(removeFile.delete()){
            log.info("file.clean delete success for path [{}]", removeFile.getAbsolutePath());
        }else{
            log.warn("file.clean delete failed for path [{}]", removeFile.getAbsolutePath());
        }
        String relativePath = filePath.substring(basePath.length());
        while (relativePath.length() > 0 && relativePath.contains("/")){
            relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String absolutePath = FileUtils.concatAbsolutePath(basePath, relativePath);
            removeFile = new File(absolutePath);
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
