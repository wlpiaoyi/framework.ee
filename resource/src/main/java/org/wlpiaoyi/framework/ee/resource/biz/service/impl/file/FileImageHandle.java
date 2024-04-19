package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import sun.font.FontDesignMetrics;

import jakarta.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @Autowired
    private FileConfig fileConfig;

    @Getter
    @Value("${resource.fontName}")
    private String fontName;

    @SneakyThrows
    boolean beforeSaveHandle(FileServiceImpl fileService, FileInfo entity, Map funcMap) {
        if(!FileImageHandle.isSupportSuffix(entity.getSuffix())){
            return false;
        }
        String tempFilePath = MapUtils.getString(funcMap, "tempFilePath");
        if(ValueUtils.isBlank(tempFilePath)){
            return false;
        }
        if(ValueUtils.isNotBlank(MapUtils.getString(funcMap, "waterText"))){
            String waterText = MapUtils.getString(funcMap, "waterText").replaceAll("\\\\n", "\n");
            Integer waterFontSize = MapUtils.getInteger(funcMap, "waterFontSize", 15);

            ParseTextToImageModel imageModel = ParseTextToImageModel.builder()
                    .textfont(new Font(this.fontName, Font.BOLD, waterFontSize))
                    .textPaint(new GradientPaint(20, 20, Color.WHITE, 100,120, Color.LIGHT_GRAY, true))
                    .textShadowPaint(new GradientPaint(20, 20, Color.BLACK, 100,120, Color.GRAY, true))
                    .textShadowOffsetX(Math.max(1, waterFontSize / 25))
                    .textShadowOffsetY(Math.max(1, waterFontSize / 25))
                    .textAlpha(1.f)
                    .imageWidth(1400)
                    .imageHeight(500)
                    .build();
            BufferedImage textImage = parseTextToImage(waterText, imageModel);
            BufferedImage inputImage = ImageIO.read(new File(tempFilePath));
            BufferedImage waterImage = watermark(inputImage,entity.getSuffix(), textImage,
                    ImageWriteModel.builder().build(),
                    ImageWriteModel.builder().angle(45.f).opacity(0.5f).build());
            tempFilePath = FileUtils.createTempFilePath(this.fileConfig.getTempPath());
            OutputStream outputStream = Files.newOutputStream(Paths.get(tempFilePath));
            write(waterImage, entity.getSuffix(), 1, 0, 1, outputStream);
            outputStream.flush();
            outputStream.close();
            funcMap.put("tempFilePath", tempFilePath);
            return true;
        }
        return false;

    }

    @Transactional(rollbackFor = Exception.class)
    boolean afterSaveHandle(FileServiceImpl fileService, FileInfo entity, Map<?, ?> funcMap){
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

    private static final Map<String, Integer> imageSuffixeMap = new HashMap<String, Integer>(){{
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

    @Builder
    @Getter
    public static class ParseTextToImageModel{
        private Font textfont;                  //文本字体
        private Color textColor;                //文本颜色
        private float textAlpha;                //文本透明度
        private GradientPaint textPaint;        //文本渐变
        private Color textShadowColor;          //文本偏移颜色
        private GradientPaint textShadowPaint;  //文本偏移渐变
        private int textShadowOffsetX;          //文本偏移x
        private int textShadowOffsetY;          //文本偏移y
        private int imageWidth;                 //图片宽度
        private int imageHeight;                //图片高度
    }
    /**
     * 文字转图片
     * @param text
     * @param modelParams
     * @return: java.awt.image.BufferedImage
     * @author: wlpia
     * @date: 2024/1/12 15:04
     */
    public static BufferedImage parseTextToImage(String text, ParseTextToImageModel modelParams) {
        BufferedImage textImage = new BufferedImage(modelParams.imageWidth, modelParams.imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = textImage.createGraphics();
        //设置背景透明
        textImage = g2.getDeviceConfiguration().createCompatibleImage(modelParams.imageWidth, modelParams.imageHeight, Transparency.TRANSLUCENT);
        g2.dispose();
        g2 = textImage.createGraphics();

        //开启文字抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //设置字体
        g2.setFont(modelParams.textfont);
        //计算字体位置：上下左右居中
        FontRenderContext context = g2.getFontRenderContext();
        LineMetrics lineMetrics = modelParams.textfont.getLineMetrics(text, context);
        FontMetrics fontMetrics = FontDesignMetrics.getMetrics(modelParams.textfont);

        String[] texts = text.split("\n");
        float offsetY =  (lineMetrics.getAscent() + lineMetrics.getDescent() + lineMetrics.getLeading()) * (texts.length - 1) / 2;
        int tLine = 0;
        for(String txt : texts){
            tLine ++;
            float offset = (float) (modelParams.imageWidth - fontMetrics.stringWidth(txt)) / 2;
            float y = (modelParams.imageHeight + lineMetrics.getAscent() - lineMetrics.getDescent() - lineMetrics.getLeading()) / 2 - offsetY
                    + (lineMetrics.getAscent() + lineMetrics.getDescent() + lineMetrics.getLeading()) * (tLine - 1);

            if(modelParams.textColor != null){
                //绘图
                if(modelParams.textShadowColor != null){
                    g2.setColor(modelParams.textShadowColor);
                    g2.drawString(txt, (int) offset + modelParams.textShadowOffsetX, (int) y + modelParams.textShadowOffsetY);
                }
                //设置字体颜色
                g2.setColor(modelParams.textColor);
                g2.drawString(txt, (int) offset, (int) y);
            }else if(modelParams.textPaint != null){
                //绘图
                if(modelParams.textShadowPaint != null){
                    g2.setPaint(modelParams.textShadowPaint);
                    g2.drawString(txt, (int) offset + modelParams.textShadowOffsetX, (int) y + modelParams.textShadowOffsetY);
                }
                // 创建循环渐变的GraphientPaint对象
//            GradientPaint paint = new GradientPaint(20, 20, Color.BLUE, 100,120, Color.RED, true);
                g2.setPaint(modelParams.textPaint);// 设置渐变
                g2.drawString(txt, (int) offset, (int) y);
            }
        }

        //设置透明度:1.0f为透明度 ，值从0-1.0，依次变得不透明
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, modelParams.textAlpha));
        //释放资源
        g2.dispose();
        return textImage;
    }
    @Builder
    @Getter
    public static class ImageWriteModel{
        @Builder.Default
        private double scale = 1;   //放大缩小
        @Builder.Default
        private double angle = 0;   //旋转
        @Builder.Default
        private float quality = 0;  //质量
        @Builder.Default
        private float opacity = 1;  //透明
    }

    /**
     * 图片水印
     * @param inputImage    输入图片
     * @param suffix        图片格式
     * @param waterImage    水印图片
     * @param inputModel
     * @param waterModel
     * @return: java.awt.image.BufferedImage
     * @author: wlpia
     * @date: 2024/1/11 22:22
     */
    @SneakyThrows
    public static BufferedImage watermark(BufferedImage inputImage, String suffix, BufferedImage waterImage, ImageWriteModel inputModel, ImageWriteModel waterModel){
        Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(inputImage);
        return builder.scale(inputModel.scale)
                .outputFormat(suffix)                   //保存为文件的格式设置
                .outputQuality(inputModel.quality)      //输出的图片质量  0~1 之间,否则报错
                .rotate(inputModel.angle)
                .watermark(Positions.CENTER,
                        Thumbnails.of(waterImage).scale(waterModel.scale).rotate(waterModel.angle).asBufferedImage(),
                        waterModel.opacity)
                .asBufferedImage();
    }
    /**
     * 保存图片
     * @param inputImage    输入图片
     * @param suffix        图片格式
     * @param angle         图片旋转幅度
     * @return: java.awt.image.BufferedImage
     * @author: wlpia
     * @date: 2024/1/11 22:22
     */
    @SneakyThrows
    public static void write(BufferedImage inputImage, String suffix, double scale, double angle, double quality, OutputStream outputStream){
        Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(inputImage);
        builder.scale(scale)
                .rotate(angle)
                .outputFormat(suffix)           //保存为文件的格式设置
                .outputQuality(quality)         //输出的图片质量  0~1 之间,否则报错
                .toOutputStream(outputStream);  //输出到指定的输出流中
    }


//    @SneakyThrows
//    public static void main(String[] args) {
//        String basePath = "C:\\Users\\wlpia\\Desktop\\Temp\\test_file\\";
////        String basePath = "D:\\wlpia\\Documents\\Temp\\";
////        generateSmall("D:\\wlpia\\Documents\\Temp\\微信图片_20231227174448.jpg", "jpg", 0.3f,
////                new FileOutputStream(new File("D:\\wlpia\\Documents\\Temp\\1.jpg")));
//        String text = "仅用于某某平台认证,\n复印打印无效\n如果用于其他场景本人概不负责";
//        ParseTextToImageModel imageModel = ParseTextToImageModel.builder()
//                .textfont(new Font("微软雅黑", Font.BOLD, 100))
//                .textPaint(new GradientPaint(20, 20, Color.WHITE, 100,120, Color.LIGHT_GRAY, true))
//                .textShadowPaint(new GradientPaint(20, 20, Color.BLACK, 100,120, Color.GRAY, true))
//                .textShadowOffsetX(4)
//                .textShadowOffsetY(4)
//                .textAlpha(1.f)
//                .imageWidth(1400)
//                .imageHeight(500)
//                .build();
//        BufferedImage textImage = parseTextToImage(text, imageModel);
//        BufferedImage inputImage = ImageIO.read(new File(basePath + "E6CF736F-D906-4399-B39B-8FA5D5DD4587.png"));
//        BufferedImage waterImage = watermark(inputImage,"png", textImage,
//                ImageWriteModel.builder().build(),
//                ImageWriteModel.builder().angle(45.f).opacity(0.5f).build());
//        write(textImage, "jpg", 1, 0, 1, new FileOutputStream(new File(basePath + "textImage.jpg")));
//        write(waterImage, "jpg", 1, 0, 1, new FileOutputStream(basePath + "waterImage.jpg"));
//
//    }

}
