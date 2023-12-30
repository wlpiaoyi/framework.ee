package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper.FileInfoMapper;
import org.wlpiaoyi.framework.ee.file.manager.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.List;


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
        List<String> fingerprints = this.baseMapper.selectDeletedForFingerprint();
        if(ValueUtils.isBlank(fingerprints)){
            return null;
        }
        this.baseMapper.deleteByFingerprints(fingerprints);
        return fingerprints;
    }

    @Override
    public boolean save(FileInfo entity, Object userInfo, FileInfoSaveInterceptor interceptor) {
        if(ValueUtils.isBlank(entity.getId())){
            entity.setId(IdUtils.nextId());
        }
        boolean saveRes = super.save(entity);
        if(interceptor != null){
            interceptor.afterSave(saveRes, userInfo, entity);
        }
        return saveRes;
    }

    @Override
    public boolean updateById(FileInfo entity, Object userInfo, FileInfoUpdateInterceptor interceptor) {
        boolean updateRes = super.updateById(entity);
        if(interceptor != null){
            interceptor.afterUpdate(updateRes, userInfo, entity);
        }
        return updateRes;
    }
}
