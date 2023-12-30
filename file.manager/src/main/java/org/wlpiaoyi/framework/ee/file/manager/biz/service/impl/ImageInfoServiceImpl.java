package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper.ImageInfoMapper;
import org.wlpiaoyi.framework.ee.file.manager.config.FileConfig;
import org.wlpiaoyi.framework.ee.file.manager.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.file.manager.utils.FileUtils;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
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
@Primary
@Service
public class ImageInfoServiceImpl extends BaseServiceImpl<ImageInfoMapper, ImageInfo> implements IImageInfoService {

    private Set<String> imageSuffixes = new HashSet(){{
       add("jpg");
       add("jpeg");
       add("png");
    }};

    @Autowired
    private FileConfig fileConfig;

    public boolean isSupport(String suffix){
        return this.imageSuffixes.contains(suffix);
    }

    public boolean hasThumbnail(Long fileId){
        return this.baseMapper.exists(
                Wrappers.<ImageInfo>lambdaQuery().eq(ImageInfo::getFileId, fileId)
        );
    }

    @Override
    public ImageInfo getThumbnailByFileId(Long fileId) {
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
    public void generateSmall(String fingerprintHex, String suffix, double smallSize, OutputStream outputStream) {
        String orgImagePath = this.fileConfig.getFilePathByFingerprintHex(fingerprintHex);
        File orgImageFile = new File(orgImagePath);
        Image orgImage = ImageIO.read(orgImageFile);
        int width = orgImage.getWidth(null);
        int height = orgImage.getHeight(null);
        int widthSmall = (int) (width * smallSize);
        int heightSmall = (int) (height * smallSize);
        int imageType;
        if(suffix.equals("png")){
            imageType = BufferedImage.TYPE_INT_ARGB;
        }else{
            imageType = BufferedImage.TYPE_INT_RGB;
        }
        BufferedImage bi = new BufferedImage(widthSmall, heightSmall, imageType);
        Graphics g = bi.getGraphics();
        g.drawImage(orgImage, 0, 0, widthSmall, heightSmall, null);
        g.dispose();
        ImageIO.write(bi, suffix, outputStream);
    }

    @SneakyThrows
    @Override
    public String generateSmall(String fingerprintHex, String suffix, double smallSize) {
        String fileName = StringUtils.getUUID32();
        FileUtils.makeDir(this.fileConfig.getTempPath());
        File tempFile = new File(this.fileConfig.getTempPath() + "/" + fileName);
        this.generateSmall(fingerprintHex, suffix, smallSize, new FileOutputStream(tempFile));
        return FileUtils.mergeToFingerprintHex(tempFile, this.fileConfig.getDataPath());
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

    @Autowired
    private IFileInfoService fileInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> cleanImage() {
        List<Long> ids = this.baseMapper.selectDeletedIds();
        if(ValueUtils.isBlank(ids)){
            return null;
        }
        List<Long> thumbnailIds = this.baseMapper.selectThumbnailIdByIds(ids);
        if(ValueUtils.isNotBlank(thumbnailIds)){
            this.deleteLogic(thumbnailIds);
            ids.removeAll(thumbnailIds);
            ids.addAll(thumbnailIds);
        }
        List<Long> fileIds = this.baseMapper.selectDeletedFileIds();
        if(ValueUtils.isNotBlank(fileIds)){
            this.fileInfoService.deleteLogic(fileIds);
        }
        this.baseMapper.deletedImages();
        return ids;
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
