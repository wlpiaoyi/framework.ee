package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper.FileInfoMapper;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.file.manager.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> cleanFile() {
        List<Long> fileIds = this.imageInfoService.cleanImage();
        if(ValueUtils.isNotBlank(fileIds)){
            log.info("file cleanImage fileIds size:{} values:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
            boolean delRes = this.deleteLogic(fileIds);
            log.info("file deleteLogic fileIds delRes:{}", delRes);
        }else{
            log.info("file cleanImage fileIds empty");
        }
        fileIds = this.baseMapper.selectDeletedIds();
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
        return fingerprints;
    }

    @Override
    public boolean save(FileInfo entity, Map funcMap, FileInfoSaveInterceptor interceptor) {
        if(ValueUtils.isBlank(entity.getId())){
            entity.setId(IdUtils.nextId());
        }
        boolean saveRes = super.save(entity);
        if(interceptor != null){
            interceptor.afterSave(saveRes, funcMap, entity);
        }
        return saveRes;
    }

    @Override
    public boolean updateById(FileInfo entity, Map funcMap, FileInfoUpdateInterceptor interceptor) {
        FileInfo db = this.getById(entity.getId());
        if(db == null){
            throw new BusinessException("没有找到对应的数据");
        }
        FileInfo updateEntity = new FileInfo();
        updateEntity.setName(entity.getName());
        updateEntity.setSuffix(entity.getSuffix());
        updateEntity.setId(entity.getId());
        boolean updateRes = super.updateById(updateEntity);
        if(interceptor != null){
            interceptor.afterUpdate(updateRes, funcMap, entity);
        }
        return updateRes;
    }

    @Autowired
    private IImageInfoService imageInfoService;

    @Override
    public FileInfo getThumbnailFileByFileInfo(FileInfo fileInfo) {
        ImageInfo thumbnailImageInfo = this.imageInfoService.getThumbnailImageByFileId(fileInfo.getId());
        if(thumbnailImageInfo != null){
            FileInfo thumbnailFileInfo = this.getById(thumbnailImageInfo.getFileId());
            if(thumbnailFileInfo != null){
                return thumbnailFileInfo;
            }
        }
        return null;
    }
}
