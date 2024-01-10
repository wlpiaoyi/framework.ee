package org.wlpiaoyi.framework.ee.resource.biz.service;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo;
import org.wlpiaoyi.framework.ee.resource.service.IBaseService;

import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 服务类接口
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
public interface IImageInfoService extends IBaseService<ImageInfo> {

//    boolean isSupport(String suffix);
//    /**
//     * 压缩图片
//     * @param fingerprintHex 文件指纹
//     * @param suffix 文件格式
//     * @param smallSize 文件压缩倍数
//     * @return: java.lang.String 压缩后的文件指纹
//     * @author: wlpia
//     * @date: 2023/12/30 11:43
//     */
//    String generateSmall(String fingerprintHex, String suffix, double smallSize);

    /**
     * 图片详情
     * @param id
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo
     * @author: wlpia
     * @date: 2024/1/9 12:42
     */
    ImageInfoVo detail(Long id);
    /**
     * 图片详情
     * @param fileId 文件Id
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo
     * @author: wlpia
     * @date: 2024/1/8 14:17
     */
    ImageInfoVo detailByFileId(Long fileId);

    /**
     * 是否有缩略图
     * @param fileId
     * @return: boolean
     * @author: wlpia
     * @date: 2023/12/30 12:13
     */
    boolean hasThumbnail(Long fileId);


    /**
     * 获取图片
     * @param fileId
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo
     * @author: wlpia
     * @date: 2024/1/9 12:38
     */
    ImageInfo getImageByFileId(Long fileId);

    /**
     * 获取缩略图
     * @param fileId
     * @return: entity.domain.biz.org.wlpiaoyi.framework.ee.resource.ImageInfo
     * @author: wlpia
     * @date: 2023/12/30 17:07
     */
    ImageInfo getThumbnailByFileId(Long fileId);

    /**
     * 根据文件信息保存图片
     * @param fileInfo
     * @param funcMap
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo
     * @author: wlpia
     * @date: 2024/1/9 17:56
     */
    ImageInfo saveByFileInfo(FileInfo fileInfo, Map funcMap);

    /**
     * 清理图片
     * @return: java.util.List<java.lang.Long> 缩略图对应的FileInfo.id
     * @author: wlpia
     * @date: 2023/12/30 18:36
     */
    List<Long> cleanImage();


}
