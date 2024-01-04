package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.file.manager.utils.SpringUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    图片数据处理服务
 * {@code @date:}           2023-12-31 21:58:25
 * {@code @version:}:       1.0
 */
@Slf4j
@Service
public class FileImageHandle {


    public boolean canDownloadFileInfoHandle(FileInfo entity, String dataType){
        if(FileImageHandle.isSupportSuffix(entity.getSuffix()) && dataType.equals("thumbnail")){
            return true;
        }
        return false;
    }

    public FileInfo downloadFileInfoHandle(FileServiceImpl fileService, FileInfo entity){
        FileInfo fileInfo = fileService.fileInfoService.getThumbnailFileByFileInfo(entity);
        if(fileInfo != null){
            return fileInfo;
        }
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean afterSaveHandle(FileServiceImpl fileService, FileInfo entity, Map funcMap){
        IImageInfoService imageInfoService = SpringUtils.getBean(IImageInfoService.class);
        if(!FileImageHandle.isSupportSuffix(entity.getSuffix())){
            return false;
        }
        if(imageInfoService.hasThumbnail(entity.getId())){
            return true;
        }
        ImageInfo imageInfo =  imageInfoService.saveByFileInfo(entity);
        log.info("image save success, id:{}", imageInfo.getId());
        if(!funcMap.containsKey("thumbnailSize")){
            return true;
        }
        double thumbnailSize = MapUtils.getDouble(funcMap, "thumbnailSize");
        if(thumbnailSize < 0){
            return true;
        }
        if(thumbnailSize > 0.99){
            throw new BusinessException("缩略图比例过大");
        }
        if(thumbnailSize < 0.01){
            throw new BusinessException("缩略图比例过小");
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        String orgImagePath = fileService.fileConfig.getFilePathByFingerprint(entity.getFingerprint());
        FileImageHandle.generateSmall(orgImagePath,
                entity.getSuffix(), thumbnailSize, byteOutputStream);
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
        FileInfo smallFileInfo = new FileInfo();
        smallFileInfo.setName(entity.getName());
        if(smallFileInfo.getName().contains(".")){
            smallFileInfo.setName(entity.getName().substring(0, entity.getName().lastIndexOf(".") + 1));
        }
        smallFileInfo.setName("thumbnail." + entity.getSuffix());
        fileService.save(byteInputStream, smallFileInfo,  funcMap, false);
        ImageInfo smallImageInfo = imageInfoService.saveByFileInfo(smallFileInfo);
        imageInfo.setThumbnailId(smallImageInfo.getId());
        imageInfoService.updateById(imageInfo);
        return true;
    }

    private static final Map<String, Integer> imageSuffixeMap = new HashMap(){{
        put("jpg", BufferedImage.TYPE_INT_RGB);
        put("jpeg", BufferedImage.TYPE_INT_RGB);
        put("png", BufferedImage.TYPE_INT_ARGB);
    }};

    public static boolean isSupportSuffix(String suffix){
        return imageSuffixeMap.containsKey(suffix);
    }
    @SneakyThrows
    static void generateSmall(String orgImagePath, String suffix, double smallSize, OutputStream outputStream) {
        File orgImageFile = new File(orgImagePath);
        Image orgImage = ImageIO.read(orgImageFile);
        int width = orgImage.getWidth(null);
        int height = orgImage.getHeight(null);
        int widthSmall = (int) (width * smallSize);
        int heightSmall = (int) (height * smallSize);
        int imageType = imageSuffixeMap.get(suffix);
        BufferedImage bi = new BufferedImage(widthSmall, heightSmall, imageType);
        Graphics g = bi.getGraphics();
        g.drawImage(orgImage, 0, 0, widthSmall, heightSmall, null);
        g.dispose();
        ImageIO.write(bi, suffix, outputStream);
    }

}
