package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
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

    static {
        FFmpegLogCallback.set();
    }

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

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public boolean afterSaveHandle(FileServiceImpl fileService, FileInfo entity, Map funcMap){
        if(!FileVideoHandle.isSupportSuffix(entity.getSuffix())){
            return false;
        }
        VideoInfo videoInfo = fileService.videoInfoService.saveByFileInfo(entity, funcMap);
        if(videoInfo == null){
            throw new BusinessException("视频信息保存失败");
        }
        Map<String, String> unMoveMap = MapUtils.getMap(funcMap, "unMoveMap");
        if(unMoveMap == null){
            throw new BusinessException("没有移动的文件容器");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String videoFile = unMoveMap.get(fileService.fileConfig.parseFingerprintToHex(entity.getFingerprint()));
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(IdUtils.nextId());
        fileInfo.setSuffix("jpg");
        fileInfo.setName("screenshot.jpg");
        float screenshotFloat = MapUtils.getFloat(funcMap, "screenshotFloat", -1.0f);
        if(screenshotFloat >= 0){
            FileVideoHandle.getVideoImage(new File(videoFile), screenshotFloat, fileInfo.getSuffix(), outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            outputStream.close();
            funcMap.remove("thumbnailSize");
            fileService.fileInfoService.save(inputStream, fileInfo, funcMap, fileService);
            inputStream.close();
            ImageInfo imageInfo = fileService.imageInfoService.getImageByFileId(fileInfo.getId());
            if(imageInfo == null){
                throw new BusinessException("没有找到截图信息");
            }
            videoInfo.setScreenshotId(imageInfo.getId());
            return fileService.videoInfoService.updateById(videoInfo);
        }
        return true;
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
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
        try {
            grabber.start();
            videoInfo.setWidth(grabber.getImageWidth());
            videoInfo.setHeight(grabber.getImageHeight());
            videoInfo.setDuration(grabber.getLengthInTime() / 1000); //ms
        } catch (Exception e) {
            log.error("获取视频时长异常", e);
            throw new BusinessException("获取视频时长异常", e);
        } finally {
            try {;
                grabber.flush();
                grabber.stop();
                grabber.close();
                grabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean getVideoImage(File fileVideo, float screenshotFloat, String suffix, OutputStream outputStream){
        final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(fileVideo);
        try {
            grabber.start();
            int i = 0;
            int length = grabber.getLengthInFrames();
            if(screenshotFloat < 0. || screenshotFloat > 1.){
                throw new BusinessException("screenshotFloat取值范围[0.0~1.0]");
            }
            int frameNum = (int) ((double)length * (double)screenshotFloat);
            Frame frame = null;
            while (i < length) {
                i++;
                if(frameNum > i){
                    continue;
                }
                Frame curframe = grabber.grabFrame();
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
            return true;
        } catch (Exception e) {
            log.error("视频截屏异常", e);
            return false;
        } finally {
            try {
                grabber.flush();
                grabber.stop();
                grabber.close();
                grabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//        File file = new File("D:\\wlpia\\Documents\\Temp\\c809cb3e5e02c5de10fc850f77a43556.mp4");
//        int i = 1000;
//        while (--i > 0){
//            VideoInfo vi = new VideoInfo();
//            setVideoInfo(file, vi);
//            getVideoImage(file, -1, "jpg", new ByteArrayOutputStream());
//        }
//        System.out.println("======================>");
//
//    }
}
