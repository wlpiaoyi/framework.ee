package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.*;
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

    /**
     * 压缩图片
     * @param orgImagePath
     * @param suffix
     * @param smallSize
     * @param outputStream
     * @return: void
     * @author: wlpia
     * @date: 2024/1/11 18:17
     */
    @SneakyThrows
    public static void generateSmall(String orgImagePath, String suffix, double smallSize, OutputStream outputStream) {
        Thumbnails.Builder<File> builder = Thumbnails.of(orgImagePath);
        builder.scale(smallSize)
                .outputFormat(suffix)   //保存为文件的格式设置
                .outputQuality(1)    //输出的图片质量  0~1 之间,否则报错
                .toOutputStream(outputStream);   //输出到指定的输出流中
    }

    /**
     * 图片水印
     * @param orgImagePath  图片路径
     * @param suffix        图片格式
     * @param image         水印图片
     * @param scale         放大缩小水印
     * @param angle         旋转水印
     * @param opacity       透明水印
     * @param outputStream
     */
    @SneakyThrows
    public static void watermark(String orgImagePath, String suffix, BufferedImage image, double scale, double angle, float opacity, OutputStream outputStream){

        Thumbnails.Builder<File> builder = Thumbnails.of(orgImagePath);
        builder.scale(1.f)
                .outputFormat(suffix)   //保存为文件的格式设置
                .outputQuality(1)   //输出的图片质量  0~1 之间,否则报错
                .watermark(Positions.CENTER,
                        Thumbnails.of(image).scale(scale).rotate(angle).asBufferedImage(),
                        opacity)
                .toOutputStream(outputStream);   //输出到指定的输出流中
    }

    /**
     * 文字转字图片
     * @param text      文本字符串
     * @param font      设置字体
     * @param fontColor 字体颜色
     * @param width     图片宽度
     * @param height    图片高度
     * @param alpha     文字透明度，值从0.0f-1.0f，依次变得不透明
     * @return: java.awt.image.BufferedImage
     * @author: wlpia
     * @date: 2024/1/11 18:50
     */
    public static BufferedImage parseTextToImage(String text, Font font, Color fontColor, int width, int height, float alpha) {
       return parseTextToImage(text, font, fontColor, Color.ORANGE, 4, 4, width, height, alpha);
    }
    public static BufferedImage parseTextToImage(String text, Font font, Color fontColor, Color shadowColor, int shadowOffsetX, int shadowOffsetY, int width, int height, float alpha) {
        BufferedImage textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = textImage.createGraphics();
        //设置背景透明
        textImage = g2.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = textImage.createGraphics();

        //开启文字抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //设置字体
        g2.setFont(font);
//        // 创建循环渐变的GraphientPaint对象
//        GradientPaint paint = new GradientPaint(20, 20, Color.BLUE, 100,120, Color.RED, true);
//        g2.setPaint(paint);// 设置渐变
        //设置透明度:1.0f为透明度 ，值从0-1.0，依次变得不透明
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        //计算字体位置：上下左右居中
        FontRenderContext context = g2.getFontRenderContext();
        LineMetrics lineMetrics = font.getLineMetrics(text, context);
        FontMetrics fontMetrics = FontDesignMetrics.getMetrics(font);
        float offset = (width - fontMetrics.stringWidth(text)) / 2;
        float y = (height + lineMetrics.getAscent() - lineMetrics.getDescent() - lineMetrics.getLeading()) / 2;
        //绘图
        g2.setColor(shadowColor);
        g2.drawString(text, (int) offset + shadowOffsetX, (int) y + shadowOffsetY);
        //设置字体颜色
        g2.setColor(fontColor);
        g2.drawString(text, (int) offset, (int) y);
        //释放资源
        g2.dispose();
        return textImage;
    }

    @SneakyThrows
    public static void main(String[] args) {
//        generateSmall("D:\\wlpia\\Documents\\Temp\\微信图片_20231227174448.jpg", "jpg", 0.3f,
//                new FileOutputStream(new File("D:\\wlpia\\Documents\\Temp\\1.jpg")));
        BufferedImage bufferedImage = parseTextToImage("请在这里输入文字",
                        new Font("微软雅黑", Font.BOLD, 100),
                        Color.BLACK, 1000, 200, 1.0f);
        watermark("D:\\wlpia\\Documents\\Temp\\微信图片_20231227174448.jpg",
                "jpg", bufferedImage, 4., 45., 0.5f,
                new FileOutputStream(new File("D:\\wlpia\\Documents\\Temp\\1.jpg")));
        ImageIO.write(bufferedImage, "png",
                        new FileOutputStream(new File("D:\\wlpia\\Documents\\Temp\\1.png")));


    }

}
