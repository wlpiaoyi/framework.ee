package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@code @author:}         wlpia
 * {@code @description:}
 * {@code @date:}           2024-01-08 14:34:54
 * {@code @version:}:       1.0
 */
@Slf4j
@Service
public class FileVideoHandle {

    @Autowired
    private FileConfig fileConfig;

    /**
     * 是否执行截图下载
     * @param suffix
     * @param dataType
     * @return: boolean
     * @author: wlpia
     * @date: 2024/1/9 10:48
     */
    boolean canDownloadByScreenshot(String suffix, String dataType){
        if(dataType.equals("screenshot") && FileVideoHandle.isSupportSuffix(suffix)){
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
    FileInfo getScreenshotFileInfo(FileServiceImpl fileService, FileInfo entity){
        VideoInfo videoInfo = fileService.videoInfoService.getVideoByFileId(entity.getId());
        if(videoInfo == null){
            return null;
        }
        ImageInfo imageInfo = fileService.imageInfoService.getById(videoInfo.getScreenshotId());
        if(imageInfo == null){
            return null;
        }
        FileInfo fileInfo = fileService.fileInfoService.getById(imageInfo.getFileId());
        if(fileInfo == null){
            return null;
        }
        return fileInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean afterSaveHandle(FileServiceImpl fileService, FileInfo entity, Map funcMap){
        if(!FileVideoHandle.isSupportSuffix(entity.getSuffix())){
            return false;
        }
        VideoInfo videoInfo = fileService.videoInfoService.saveByFileInfo(entity);
        if(videoInfo == null){
            throw new BusinessException("视频信息保存失败");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String videoFile = fileConfig.getFilePathByFingerprint(entity.getFingerprint());
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(IdUtils.nextId());
        fileInfo.setSuffix("jpg");
        fileInfo.setName("screenshot.jpg");
        FileVideoHandle.getVideoImage(new File(videoFile), -1, fileInfo.getSuffix(), outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        funcMap.remove("thumbnailSize");
        fileService.fileInfoService.save(inputStream, fileInfo, funcMap, fileService);
        ImageInfo imageInfo = fileService.imageInfoService.getImageByFileId(fileInfo.getId());
        if(imageInfo == null){
            throw new BusinessException("没有找到截图信息");
        }
        videoInfo.setScreenshotId(imageInfo.getId());
        return fileService.videoInfoService.updateById(videoInfo);
    }

    private static final Set<String> videoSuffixSet= new HashSet(){{
        add("mp4");
    }};

    public static boolean isSupportSuffix(String suffix){
        return videoSuffixSet.contains(suffix);
    }

    /**
     * 获取视频信息
     * @param videoFile 源视频文件
     * @param videoInfo 视频实体
     * @return: void
     * @author: wlpia 
     * @date: 2024/1/8 14:45
     */
    public static void setVideoInfo(File videoFile, VideoInfo videoInfo){

        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(videoFile);
        try {
            ff.start();
            videoInfo.setWidth(ff.getImageWidth());
            videoInfo.setHeight(ff.getImageHeight());
            videoInfo.setDuration(ff.getLengthInTime() / 1000); //ms
            ff.stop();
        } catch (Exception e) {
            log.error("获取视频时长异常", e);
            throw new BusinessException("获取视频时长异常", e);
        }
    }

    public static boolean getVideoImage(File fileVideo, int frameNum, String suffix, OutputStream outputStream){
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(fileVideo);
        try {
            ff.start();

            int i = 0;
            int length = ff.getLengthInFrames();
            if(frameNum < 0){
                frameNum = length / 2;
            }
            Frame frame = null;
            while (i < length) {
                i++;
                if(frameNum > i){
                    continue;
                }
                Frame curframe = ff.grabFrame();
                if(curframe.image == null){
                    continue;
                }
                frame = curframe;
                break;
            }
            // 截取的帧图片
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage srcImage = converter.getBufferedImage(frame);
            ImageIO.write(srcImage, suffix, outputStream);
            ff.stop();
            return true;
        } catch (Exception e) {
            log.error("视频截屏异常", e);
            return false;
        }
    }
}
