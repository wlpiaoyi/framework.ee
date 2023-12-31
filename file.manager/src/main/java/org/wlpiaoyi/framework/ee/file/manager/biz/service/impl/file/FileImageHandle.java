package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.file.manager.utils.SpringUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    图片数据处理服务
 * {@code @date:}           2023-12-31 21:58:25
 * {@code @version:}:       1.0
 */
@Slf4j
public class FileImageHandle {


    static boolean canDownloadFileInfoHandle(FileInfo entity, String dataType){
        IImageInfoService imageInfoService = SpringUtils.getBean(IImageInfoService.class);
        if(imageInfoService.isSupport(entity.getSuffix()) && dataType.equals("thumbnail")){
            return true;
        }
        return false;
    }

    static FileInfo downloadFileInfoHandle(FileServiceImpl fileService, FileInfo entity){
        FileInfo fileInfo = fileService.fileInfoService.getThumbnailFileByFileInfo(entity);
        if(fileInfo != null){
            return fileInfo;
        }
        return entity;
    }

    static boolean afterSaveHandle(FileServiceImpl fileService, FileInfo entity, Map funcMap){
        IImageInfoService imageInfoService = SpringUtils.getBean(IImageInfoService.class);
        if(!imageInfoService.isSupport(entity.getSuffix())){
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
        imageInfoService.generateSmall(fileService.fileConfig.parseFingerprintToHex(entity.getFingerprint()),
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

}
