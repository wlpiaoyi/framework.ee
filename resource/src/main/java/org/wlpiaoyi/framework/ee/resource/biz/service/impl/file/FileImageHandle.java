package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
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

    /**
     * 是否执行缩略图下载
     * @param suffix
     * @param dataType
     * @return: boolean
     * @author: wlpia
     * @date: 2024/1/9 10:48
     */
    boolean canDownloadByThumbnail(String suffix, String dataType){
        if(dataType.equals("thumbnail") && FileImageHandle.isSupportSuffix(suffix)){
            return true;
        }
        return false;
    }

    /**
     * 获取缩略图文件实体如果没有就返回当前文件实体
     * @param fileService
     * @param entity
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo
     * @author: wlpia
     * @date: 2024/1/9 10:53
     */
    FileInfo getThumbnailFileInfo(FileServiceImpl fileService, FileInfo entity){
        ImageInfo imageInfo = fileService.imageInfoService.getThumbnailByFileId(entity.getId());
        if(imageInfo == null){
            return entity;
        }
        FileInfo fileInfo = fileService.fileInfoService.getById(imageInfo.getFileId());
        if(fileInfo == null){
            return entity;
        }
        return fileInfo;
    }

    @Autowired
    private IImageInfoService imageInfoService;

    @Transactional(rollbackFor = Exception.class)
    boolean afterSaveHandle(FileServiceImpl fileService, FileInfo entity, Map funcMap){
        if(!FileImageHandle.isSupportSuffix(entity.getSuffix())){
            return false;
        }
        if(this.imageInfoService.hasThumbnail(entity.getId())){
            return true;
        }
        Map<String, String> unMoveMap = MapUtils.getMap(funcMap, "unMoveMap");
        if(unMoveMap == null){
            throw new BusinessException("没有移动的文件容器");
        }
        ImageInfo imageInfo =  this.imageInfoService.saveByFileInfo(entity, funcMap);
        log.info("image save success, id:{}", imageInfo.getId());
        if(!funcMap.containsKey("thumbnailSize")){
            return true;
        }
        funcMap.remove("screenshotFloat");
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

        String orgImagePath = unMoveMap.get(fileService.fileConfig.parseFingerprintToHex(entity.getFingerprint()));
        if(ValueUtils.isBlank(orgImagePath)){
            throw new BusinessException("没有找到原图");
        }
        FileImageHandle.generateSmall(orgImagePath,
                entity.getSuffix(), thumbnailSize, byteOutputStream);
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
        FileInfo smallFileInfo = new FileInfo();
        smallFileInfo.setName(entity.getName());
        if(smallFileInfo.getName().contains(".")){
            smallFileInfo.setName(entity.getName().substring(0, entity.getName().lastIndexOf(".") + 1));
        }
        smallFileInfo.setName("thumbnail." + entity.getSuffix());
        fileService.fileInfoService.save(byteInputStream, smallFileInfo,  funcMap, null);
        ImageInfo smallImageInfo = this.imageInfoService.saveByFileInfo(smallFileInfo, funcMap);
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
