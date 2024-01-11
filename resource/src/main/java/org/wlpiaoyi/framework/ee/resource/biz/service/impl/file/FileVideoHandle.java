package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.utils.IdUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.Progress;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

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

    public static boolean getVideoImage(File inputFile, float screenshotFloat, String suffix, OutputStream outputStream){
        final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        try {
            grabber.start();
            int length = grabber.getLengthInFrames();
            if(screenshotFloat < 0. || screenshotFloat > 1.){
                throw new BusinessException("screenshotFloat取值范围[0.0~1.0]");
            }
            int frameNum = (int) ((double)length * (double)screenshotFloat);
            Frame frame;
            while ((frame = grabber.grabImage()) != null) {
                frameNum --;
                if(frameNum > 0){
                    continue;
                }
                if(frame.image == null){
                    continue;
                }
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

    @SneakyThrows
    public static void watermark(File inputFile, String suffix, BufferedImage waterImage, double scale, double angle, float opacity, File outFile){
        // 设置源视频、加字幕后的视频文件路径
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(inputFile);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outFile, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        try{
            // 视频相关配置，取原视频配置
            recorder.setFormat(grabber.getFormat());
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.setVideoCodecName(grabber.getVideoCodecName());
            recorder.setTimestamp(grabber.getTimestamp());
//            recorder.setFrameNumber(grabber.getFrameNumber());
//            recorder.setPixelFormat(grabber.getPixelFormat());
            // 音频相关配置，取原音频配置
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioCodec(grabber.getAudioCodec());
            recorder.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame;
            int length = grabber.getLengthInVideoFrames();
            int cur = 0;
            Progress progress = new Progress();
            new Thread(() -> progress.begin("转化进度")).start();

            while ((frame = grabber.grab()) != null) {
                // 从视频帧中获取图片
                if (frame.image != null) {
                    progress.setRate((int) (((float) (++ cur) * 100.0f) / ((float) length)));
                    BufferedImage bufferedImage = converter.getBufferedImage(frame);

                    // 对图片进行文本合入
                    bufferedImage = FileImageHandle.watermark(bufferedImage, suffix,  waterImage, scale, angle, opacity);

                    // 视频帧赋值，写入输出流
                    frame.image = converter.getFrame(bufferedImage).image;
                    recorder.record(frame);
                }

                // 音频帧写入输出流
                if(frame.samples != null) {
                    recorder.record(frame);
                }
            }
            progress.end();
            System.out.println();

        }catch (Exception e){
            e.printStackTrace();
        }finally {

            grabber.flush();
            grabber.stop();
            grabber.close();
            grabber.release();

            recorder.flush();
            recorder.stop();
            recorder.close();
            recorder.release();
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        BufferedImage bufferedImage = FileImageHandle.parseTextToImage("哈哈，有🐕吗",
                new Font("微软雅黑", Font.BOLD, 100),
                Color.GREEN, 1000, 1000, 1.0f);
        FileVideoHandle.watermark(new File("C:\\Users\\wlpia\\Desktop\\Temp\\test_file\\a2b518a3625fbc19f927c06a2236384e.mp4"),
                "jpg", bufferedImage,1, 45., 0.3f, new File("C:\\Users\\wlpia\\Desktop\\Temp\\test_file\\1.mp4"));

    }
}
