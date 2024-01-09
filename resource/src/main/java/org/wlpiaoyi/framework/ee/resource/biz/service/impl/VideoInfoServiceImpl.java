package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.FileInfoMapper;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IVideoInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.VideoInfoMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.service.impl.file.FileVideoHandle;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	视频信息 服务类实现
 * {@code @date:} 			2024-01-08 14:07:23
 * {@code @version:}: 		1.0
 */
@Primary
@Service
public class VideoInfoServiceImpl extends BaseServiceImpl<VideoInfoMapper, VideoInfo> implements IVideoInfoService {

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private IImageInfoService imageInfoService;

    public VideoInfoVo detailByFileId(Long fileId){
        VideoInfo videoInfo = this.baseMapper.selectOne(Wrappers.<VideoInfo>lambdaQuery().eq(
                VideoInfo::getFileId, fileId
        ));
        if(videoInfo == null){
            return null;
        }
        VideoInfoVo videoInfoVo = ModelWrapper.parseOne(videoInfo, VideoInfoVo.class);
        if(ValueUtils.isNotBlank(videoInfoVo.getFileId())){
            videoInfoVo.setFileInfo(this.fileInfoMapper.selectById(videoInfoVo.getFileId()));
        }
        if(ValueUtils.isNotBlank(videoInfoVo.getScreenshotId())){
            videoInfoVo.setScreenshot(this.imageInfoService.detail(videoInfoVo.getScreenshotId()));
        }
        return videoInfoVo;
    }

    public VideoInfo getVideoByFileId(Long fileId){
        return this.baseMapper.selectOne(Wrappers.<VideoInfo>lambdaQuery().eq(
                VideoInfo::getFileId, fileId
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public VideoInfo saveByFileInfo(FileInfo fileInfo) {
        VideoInfo entity = new VideoInfo();
        entity.setId(IdUtils.nextId());
        entity.setFileId(fileInfo.getId());
        entity.setSuffix(fileInfo.getSuffix());
        File videoFile = new File(this.fileConfig.getFilePathByFingerprint(fileInfo.getFingerprint()));
        FileVideoHandle.setVideoInfo(videoFile, entity);
        if(!this.save(entity)){
            return null;
        }
        return entity;
    }
}
