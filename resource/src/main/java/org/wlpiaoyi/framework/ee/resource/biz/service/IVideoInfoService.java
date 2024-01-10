package org.wlpiaoyi.framework.ee.resource.biz.service;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo;
import org.wlpiaoyi.framework.ee.resource.service.IBaseService;

import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	视频信息 服务类接口
 * {@code @date:} 			2024-01-08 14:07:23
 * {@code @version:}: 		1.0
 */
public interface IVideoInfoService extends IBaseService<VideoInfo> {


    /**
     *
     * @param fileId
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo
     * @author: wlpia
     * @date: 2024/1/9 12:26
     */
    VideoInfoVo detailByFileId(Long fileId);

    /**
     *
     * @param fileId
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo
     * @author: wlpia
     * @date: 2024/1/9 13:10
     */
    VideoInfo getVideoByFileId(Long fileId);

    /**
     *
     * @param fileInfo
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo
     * @author: wlpia
     * @date: 2024/1/8 15:07
     */
    VideoInfo saveByFileInfo(FileInfo fileInfo, Map funcMap);

    /**
     * @param
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2024/1/9 14:41
     */
    List<Long> cleanVideo();

}
