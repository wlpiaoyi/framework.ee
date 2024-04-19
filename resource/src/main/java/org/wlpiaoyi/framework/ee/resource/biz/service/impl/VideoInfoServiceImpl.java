package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
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
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.ee.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	视频信息 服务类实现
 * {@code @date:} 			2024-01-08 14:07:23
 * {@code @version:}: 		1.0
 */
@Slf4j
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
    public VideoInfo saveByFileInfo(FileInfo fileInfo, Map funcMap) {
        Map<String, String> unMoveMap = MapUtils.getMap(funcMap, "unMoveMap");
        if(unMoveMap == null){
            throw new BusinessException("没有移动的文件容器");
        }
        File videoFile = new File(unMoveMap.get(this.fileConfig.parseFingerprintToHex(fileInfo.getFingerprint())));
        VideoInfo entity = new VideoInfo();
        entity.setId(IdUtils.nextId());
        entity.setFileId(fileInfo.getId());
        entity.setSuffix(fileInfo.getSuffix());
        FileVideoHandle.setVideoInfo(videoFile, entity);
        if(!super.save(entity)){
            return null;
        }
        return entity;
    }

    @Override
    public boolean save(VideoInfo entity) {
        throw new BusinessException("not support is method");
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> cleanVideo(){
        List<Long> deletedIds = this.baseMapper.selectIdsFromDeletedFile();
        if(ValueUtils.isBlank(deletedIds)){
            log.info("video select deletedIds empty");
            return null;
        }
        log.info("video select deletedIds size:{} ids:{}", deletedIds.size(), ValueUtils.toStrings(deletedIds));
        boolean delRes = this.deleteLogic(deletedIds);
        log.info("video deleted deletedIds result:{}", delRes);
        List<Long> screenshotImageIds = this.baseMapper.selectScreenshotImageIdByIds(deletedIds);
        if(ValueUtils.isNotBlank(screenshotImageIds)){
            log.info("video select screenshotImageIds size:{} ids:{}", screenshotImageIds.size(), ValueUtils.toStrings(screenshotImageIds));
            this.imageInfoService.deleteLogic(screenshotImageIds);
            log.info("video deleted screenshotImageIds result:{}", screenshotImageIds);
        }else{
            log.info("video select screenshotImageIds empty");
        }
        List<Long> screenshotFileIds = this.baseMapper.selectScreenshotFileIdByIds(deletedIds);
        log.info("video delete by screenshotFileIds size:{}, ids:{}", screenshotFileIds.size(), ValueUtils.toStrings(screenshotFileIds));
        int delAll = this.baseMapper.deletedByIds(deletedIds);
        log.info("video deleted allIds size:{}, ids:{}", delAll, ValueUtils.toStrings(deletedIds));
        return screenshotFileIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLogic(List<Long> ids) {
        List<Long> imageIds = this.baseMapper.selectScreenshotImageIdByIds(ids);
        if(ValueUtils.isNotBlank(imageIds)){
            this.imageInfoService.deleteLogic(imageIds);
        }
        return super.deleteLogic(ids);
    }
}
