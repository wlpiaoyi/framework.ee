package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.ImageInfoVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper.ImageInfoMapper;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.impl.file.FileImageHandle;
import org.wlpiaoyi.framework.ee.file.manager.config.FileConfig;
import org.wlpiaoyi.framework.ee.file.manager.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.file.manager.utils.FileUtils;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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



    public boolean hasThumbnail(Long fileId){
        return this.baseMapper.exists(
                Wrappers.<ImageInfo>lambdaQuery().eq(ImageInfo::getFileId, fileId)
        );
    }

    @Override
    public ImageInfo getThumbnailImageByFileId(Long fileId) {
        List<ImageInfo> imageInfos = this.baseMapper.selectList(Wrappers.<ImageInfo>lambdaQuery().eq(
                ImageInfo::getFileId, fileId
        ));
        if(ValueUtils.isBlank(imageInfos)){
            return null;
        }
        ImageInfo imageInfo = imageInfos.get(0);
        if(ValueUtils.isBlank(imageInfo.getThumbnailId())){
            return null;
        }
        return this.baseMapper.selectById(imageInfo.getThumbnailId());
    }

    @SneakyThrows
    @Override
    public ImageInfo saveByFileInfo(FileInfo fileInfo) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setId(IdUtils.nextId());
        imageInfo.setFileId(fileInfo.getId());
        imageInfo.setSuffix(fileInfo.getSuffix());
        File orgImageFile = new File(this.fileConfig.getFilePathByFingerprint(fileInfo.getFingerprint()));
        Image orgImage = ImageIO.read(orgImageFile);
        int width = orgImage.getWidth(null);
        int height = orgImage.getHeight(null);
        imageInfo.setWidth(width);
        imageInfo.setHeight(height);
        super.save(imageInfo);
        return imageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> cleanImage() {
        List<Long> deleteIds = this.baseMapper.selectIdsFromDeletedFile();;
        if(ValueUtils.isBlank(deleteIds)){
            log.info("image select deletedIds empty");
            return null;
        }
        log.info("image select deletedIds size:{} ids:{}", deleteIds.size(), ValueUtils.toStrings(deleteIds));
        boolean delRes = this.deleteLogic(deleteIds);
        log.info("image deleted deletedIds result:{}", delRes);
        List<Long> thumbnailIds = this.baseMapper.selectThumbnailIdByIds(deleteIds);
        if(ValueUtils.isNotBlank(thumbnailIds)){
            log.info("image select thumbnailIds size:{} ids:{}", thumbnailIds.size(), ValueUtils.toStrings(thumbnailIds));
            this.deleteLogic(thumbnailIds);
            deleteIds.removeAll(thumbnailIds);
            deleteIds.addAll(thumbnailIds);
            log.info("image deleted thumbnailIds result:{}", thumbnailIds);
        }else{
            log.info("image select thumbnailIds empty");
        }
        List<Long> fileIds = this.baseMapper.selectFileIdByIds(deleteIds);
        log.info("image delete by fileIds size:{}, ids:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
        int delAll = this.baseMapper.deletedByIds(deleteIds);
        log.info("image deleted allIds size:{}, ids:{}", delAll, ValueUtils.toStrings(deleteIds));
        return fileIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLogic(List<Long> ids) {
        List<Long> temps = this.baseMapper.selectThumbnailIdByIds(ids);
        if(ValueUtils.isBlank(temps)){
            super.deleteLogic(temps);
        }
        return super.deleteLogic(ids);
    }
}
