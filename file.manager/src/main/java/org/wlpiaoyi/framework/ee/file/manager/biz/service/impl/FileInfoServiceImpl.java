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
        List<Long> thumbnailFileIds = this.imageInfoService.cleanImage();
        if(ValueUtils.isNotBlank(thumbnailFileIds)){
            log.info("file cleanImage thumbnailFileIds size:{} values:{}", thumbnailFileIds.size(), ValueUtils.toStrings(thumbnailFileIds));
            boolean delRes = this.deleteLogic(thumbnailFileIds);
            log.info("file deleteLogic thumbnailFileIds delRes:{}", delRes);
        }else{
            log.info("file cleanImage thumbnailFileIds empty");
        }
        List<String> fingerprints = this.baseMapper.selectDeletedForFingerprint();
        if(ValueUtils.isBlank(fingerprints)){
            log.info("file select fingerprints empty");
            return null;
        }
        log.info("file select fingerprints size:{} values:{}", fingerprints.size(), ValueUtils.toStrings(fingerprints));
        this.baseMapper.deleteByFingerprints(fingerprints);
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
        boolean updateRes = super.updateById(entity);
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
