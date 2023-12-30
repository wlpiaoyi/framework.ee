package org.wlpiaoyi.framework.ee.file.manager.biz.service;

import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.service.IBaseService;

import java.io.OutputStream;
import java.util.List;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 服务类接口
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
public interface IImageInfoService extends IBaseService<ImageInfo> {

    boolean isSupport(String suffix);

    /**
     * 压缩图片
     * @param fingerprintHex 文件指纹
     * @param suffix 文件格式
     * @param smallSize 文件压缩倍数
     * @param outputStream 输出流
     * @return: void
     * @author: wlpia
     * @date: 2023/12/30 15:36
     */
    void generateSmall(String fingerprintHex, String suffix, double smallSize, OutputStream outputStream);
    /**
     * 压缩图片
     * @param fingerprintHex 文件指纹
     * @param suffix 文件格式
     * @param smallSize 文件压缩倍数
     * @return: java.lang.String 压缩后的文件指纹
     * @author: wlpia
     * @date: 2023/12/30 11:43
     */
    String generateSmall(String fingerprintHex, String suffix, double smallSize);

    /**
     * 是否有缩略图
     * @param fileId
     * @return: boolean
     * @author: wlpia
     * @date: 2023/12/30 12:13
     */
    boolean hasThumbnail(Long fileId);

    /**
     * 获取缩略图
     * @param fileId
     * @return: org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo
     * @author: wlpia
     * @date: 2023/12/30 17:07
     */
    ImageInfo getThumbnailByFileId(Long fileId);

    /**
     * 根据文件信息保存图片
     * @param fileInfo
     * @return: org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo
     * @author: wlpia
     * @date: 2023/12/30 16:30
     */
    ImageInfo saveByFileInfo(FileInfo fileInfo);

    /**
     * 清理图片
     * @return: java.util.List<java.lang.Long> 对应的图片Id
     * @author: wlpia
     * @date: 2023/12/30 18:36
     */
    List<Long> cleanImage();


}
