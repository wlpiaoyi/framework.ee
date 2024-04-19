package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.FileInfoMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.ImageInfoMapper;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 服务类实现
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
@Slf4j
@Primary
@Service
public class ImageInfoServiceImpl extends BaseServiceImpl<ImageInfoMapper, ImageInfo> implements IImageInfoService {

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    public ImageInfoVo detail(Long id) {
        ImageInfo entity = this.getById(id);
        if(entity == null){
            return null;
        }
        ImageInfoVo entityVo = ModelWrapper.parseOne(entity, ImageInfoVo.class);
        if(ValueUtils.isNotBlank(entityVo.getFileId())){
            entityVo.setFileInfo(this.fileInfoMapper.selectById(entityVo.getFileId()));
        }
        return entityVo;
    }

    @Override
    public ImageInfoVo detailByFileId(Long fileId) {
        ImageInfo entity = this.baseMapper.selectOne(Wrappers.<ImageInfo>lambdaQuery()
                .eq(ImageInfo::getFileId, fileId)
        );
        if(entity == null){
            return null;
        }
        ImageInfoVo entityVo = ModelWrapper.parseOne(entity, ImageInfoVo.class);
        if(ValueUtils.isNotBlank(entityVo.getFileId())){
            entityVo.setFileInfo(this.fileInfoMapper.selectById(entityVo.getFileId()));
        }
        if(ValueUtils.isNotBlank(entityVo.getThumbnailId())){
            entityVo.setThumbnailInfo(this.detail(entityVo.getThumbnailId()));
        }
        return entityVo;
    }

    public boolean hasThumbnail(Long fileId){
        return this.baseMapper.exists(
                Wrappers.<ImageInfo>lambdaQuery().eq(ImageInfo::getFileId, fileId)
        );
    }

    public ImageInfo getImageByFileId(Long fileId){
        List<ImageInfo> imageInfos = this.baseMapper.selectList(Wrappers.<ImageInfo>lambdaQuery().eq(
                ImageInfo::getFileId, fileId
        ));
        if(ValueUtils.isBlank(imageInfos)){
            return null;
        }
        return imageInfos.get(0);
    }

    @Override
    public ImageInfo getThumbnailByFileId(Long fileId) {
        ImageInfo imageInfo = this.getImageByFileId(fileId);
        if(ValueUtils.isBlank(imageInfo.getThumbnailId())){
            return null;
        }
        return this.baseMapper.selectById(imageInfo.getThumbnailId());
    }

    @SneakyThrows
    @Override
    public ImageInfo saveByFileInfo(FileInfo fileInfo, Map funcMap) {
        Map<String, String> unMoveMap = MapUtils.getMap(funcMap, "unMoveMap");
        if(unMoveMap == null){
            throw new BusinessException("没有移动的文件容器");
        }
        File imageFile = new File(unMoveMap.get(this.fileConfig.parseFingerprintToHex(fileInfo.getFingerprint())));
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setId(IdUtils.nextId());
        imageInfo.setFileId(fileInfo.getId());
        imageInfo.setSuffix(fileInfo.getSuffix());
        Image orgImage = ImageIO.read(imageFile);
        int width = orgImage.getWidth(null);
        int height = orgImage.getHeight(null);
        imageInfo.setWidth(width);
        imageInfo.setHeight(height);
        if(!super.save(imageInfo)){
            throw new BusinessException("图片信息保存失败");
        }
        return imageInfo;
    }

    @Override
    public boolean save(ImageInfo entity) {
        throw new BusinessException("not support is method");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> cleanImage() {
        List<Long> deletedIds = this.baseMapper.selectIdsFromDeletedFile();
        if(ValueUtils.isBlank(deletedIds)){
            log.info("image select deletedIds empty");
            return null;
        }
        log.info("image select deletedIds size:{} ids:{}", deletedIds.size(), ValueUtils.toStrings(deletedIds));
        boolean delRes = this.deleteLogic(deletedIds);
        log.info("image deleted deletedIds result:{}", delRes);
        List<Long> thumbnailIds = this.baseMapper.selectThumbnailIdByIds(deletedIds);
        if(ValueUtils.isNotBlank(thumbnailIds)){
            log.info("image select thumbnailIds size:{} ids:{}", thumbnailIds.size(), ValueUtils.toStrings(thumbnailIds));
            this.deleteLogic(thumbnailIds);
            deletedIds.removeAll(thumbnailIds);
            deletedIds.addAll(thumbnailIds);
            log.info("image deleted thumbnailIds result:{}", thumbnailIds);
        }else{
            log.info("image select thumbnailIds empty");
        }
        List<Long> fileIds = this.baseMapper.selectFileIdByIds(deletedIds);
        log.info("image delete by fileIds size:{}, ids:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
        int delAll = this.baseMapper.deletedByIds(deletedIds);
        log.info("image deleted allIds size:{}, ids:{}", delAll, ValueUtils.toStrings(deletedIds));
        return fileIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLogic(List<Long> ids) {
        List<Long> temps = this.baseMapper.selectThumbnailIdByIds(ids);
        if(ValueUtils.isNotBlank(temps)){
            super.deleteLogic(temps);
        }
        return super.deleteLogic(ids);
    }


}
