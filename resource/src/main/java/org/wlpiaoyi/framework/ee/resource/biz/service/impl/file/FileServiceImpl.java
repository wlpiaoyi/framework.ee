package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.bytedeco.javacv.FrameFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IImageInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IVideoInfoService;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.SystemException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/8 16:51
 * {@code @version:}:       1.0
 */
@Slf4j
@Primary
@Service
public class FileServiceImpl implements IFileService, IFileInfoService.FileInfoSaveInterceptor, IFileInfoService.FileInfoUpdateInterceptor {

    @Autowired
    FileConfig fileConfig;

    @Autowired
    IFileInfoService fileInfoService;
    @Autowired
    IImageInfoService imageInfoService;

    @Autowired
    IVideoInfoService videoInfoService;

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    @Override
    public String save(Object fileIo, FileInfo entity, Map<?, ?> funcMap){
        return this.fileInfoService.save(fileIo, entity, funcMap, this);
    }

    private static final Map<String, String> contentTypeMap = new HashMap<String, String>(){{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("gif", "image/gif");

        put("pdf", "application/pdf");

        put("docx", "application/msword");
        put("doc", "application/msword");
        put("xlsx", "application/vnd.ms-excel");
        put("xls", "application/vnd.ms-excel");

        put("mp4", "video/mp4");
        put("wmv", "video/wmv");
        put("mp3", "audio/mp3");

        put("default", "application/octet-stream");
    }};


    @SneakyThrows
    @Override
    public void download(String token, Map<String, ?> funcMap, HttpServletRequest request, HttpServletResponse response){
        Object[] eToken = this.fileConfig.decodeToken(token);
        Long id = (Long) eToken[0];
        String fingerprint = (String) eToken[1];
        FileInfo fileInfo = this.fileInfoService.getById(id);
        if(fileInfo == null){
            throw new SystemException("没有找到文件");
        }
        String fmFingerprint = ValueUtils.bytesToHex(this.fileConfig.dataDecode(fileInfo.getFingerprint()));
        String ogFingerprint = ValueUtils.bytesToHex(this.fileConfig.dataDecode(fingerprint));
        if(!fmFingerprint.equals(ogFingerprint)){
            throw new SystemException("文件验证失败");
        }
        this.download(fileInfo, funcMap,request, response);
    }


    @Autowired
    private FileImageHandle fileImageHandle;
    @Autowired
    private FileVideoHandle fileVideoHandle;

    @Override
    public void download(FileInfo entity, Map<String, ?> funcMap, HttpServletRequest request, HttpServletResponse response){
        String dataType = MapUtils.getString(funcMap, "dataType", "general");
        if(this.fileImageHandle.canDownloadByThumbnail(entity.getSuffix(), dataType)){
            entity = this.fileImageHandle.getThumbnailFileInfo(this, entity);
        }else if(this.fileVideoHandle.canDownloadByScreenshot(entity.getSuffix(), dataType)){
            entity = this.fileVideoHandle.getScreenshotFileInfo(this, entity);
        }
        if(entity.getIsVerifySign() == 1){
            String fileSign = MapUtils.getString(funcMap, "fileSign");
            if(ValueUtils.isBlank(fileSign)){
                throw new SystemException("无权访问文件");
            }
            try{
                if(!this.fileConfig.verifyFile(entity.getId(), entity.getFingerprint(), fileSign)){
                    throw new SystemException("无权访问文件");
                }
            }catch (Exception e){
                throw new SystemException("无权访问文件", e);
            }
        }
        String ft = entity.getSuffix();
        if(ValueUtils.isNotBlank(ft)){
            ft = ft.toLowerCase(Locale.ROOT);
        }
        String contentType = contentTypeMap.get(ft);
        if(ValueUtils.isBlank(contentType)){
            contentType = contentTypeMap.get("default");
        }
        ((Map) funcMap).put("contentType", contentType);
        String fileName = entity.getName();
        if(!fileName.contains(".")){
            fileName += "." + entity.getSuffix();
        }
        ((Map) funcMap).put("fileName", fileName);
        String ogPath = this.fileConfig.getFilePathByFingerprint(entity.getFingerprint());
        this.download(new File(ogPath), funcMap, request, response);
    }


    private void download(File file, Map<String, ?> funcMap, HttpServletRequest request, HttpServletResponse response){
        List<Closeable> closeables = new ArrayList<>();
        try{
            String contentType = MapUtils.getString(funcMap, "contentType");
            String fileName = MapUtils.getString(funcMap, "fileName");
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }
            response.setContentType(contentType);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String readType = MapUtils.getString(funcMap, "readType", "inline");

            OutputStream outputStream = response.getOutputStream();
            response.reset();
            closeables.add(outputStream);
            RandomAccessFile randomAccessFile  = new RandomAccessFile(file, "r");
            closeables.add(randomAccessFile);
            long fileLength = randomAccessFile.length();
            long requestSize = (int) fileLength;
            //获取请求头中Range的值
            String rangeString = request.getHeader(HttpHeaders.RANGE);
            //从Range中提取需要获取数据的开始和结束位置
            long requestStart = 0, requestEnd = 0;

            if (ValueUtils.isNotBlank(rangeString) && !"null".equals(rangeString)) {
                //断点传输下载返回206
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-disposition", "inline" + ";filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
                String[] ranges = rangeString.split("=");
                if (ranges.length > 1) {
                    String[] rangeDatas = ranges[1].split("-");
                    requestStart = Integer.parseInt(rangeDatas[0]);
                    if (rangeDatas.length > 1) {
                        requestEnd = Integer.parseInt(rangeDatas[1]);
                    }
                }
                if (requestEnd != 0 && requestEnd > requestStart) {
                    requestSize = requestEnd - requestStart + 1;
                } //断点传输下载视频返回206
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                //设置targetFile，从自定义位置开始读取数据
                randomAccessFile.seek(requestStart);
                //根据协议设置请求头
                response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            }else{
                response.setHeader("Content-disposition", readType + ";filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
                // response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getName(), Charsets.UTF_8.name()));

            }
            //设置文件长度
//            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(requestSize));

            //从磁盘读取数据流返回
            byte[] cache = new byte[1024];
            while (requestSize > 0) {
                int len = randomAccessFile.read(cache);
                if (requestSize < cache.length) {
                    outputStream.write(cache, 0, (int) requestSize);
                    outputStream.flush();
                } else {
                    outputStream.write(cache, 0, len);
                    outputStream.flush();
                    if (len < cache.length) {
                        break;
                    }
                }
                Thread.sleep(10);
                requestSize -= cache.length;
            }

            outputStream.flush();
            outputStream.close();
            randomAccessFile.close();
            closeables.remove(outputStream);
            closeables.remove(randomAccessFile);
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw (BusinessException)e;
            }
            if(e instanceof SystemException){
                throw (SystemException)e;
            }
            throw new SystemException("文件读取异常", e);
        }finally {
            if(ValueUtils.isNotBlank(closeables)){
                for (Closeable closeable : closeables){
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        log.error("FileServiceImpl.download close obj failed", e);
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> cleanFile() {
        File tempPath = new File(this.fileConfig.getTempPath());
        if(!tempPath.exists()){
            if(!tempPath.mkdirs()){
                log.warn("FileServiceImpl.cleanFile mkdirs failed for tempPath:{}", tempPath);
            }
        }
        File dataPath = new File(this.fileConfig.getDataPath());
        if(!dataPath.exists()){
            if(!dataPath.mkdirs()){
                log.warn("FileServiceImpl.cleanFile mkdirs failed for dataPath:{}", dataPath);
            }
        }
        List<Long> fileIds = this.videoInfoService.cleanVideo();
        if(ValueUtils.isNotBlank(fileIds)){
            log.info("file cleanVideo fileIds size:{} values:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
            boolean delRes = this.fileInfoService.deleteLogic(fileIds);
            log.info("file deleteLogic fileIds delRes:{}", delRes);
        }else{
            log.info("file cleanVideo fileIds empty");
        }
        fileIds = this.imageInfoService.cleanImage();
        if(ValueUtils.isNotBlank(fileIds)){
            log.info("file cleanImage fileIds size:{} values:{}", fileIds.size(), ValueUtils.toStrings(fileIds));
            boolean delRes = this.fileInfoService.deleteLogic(fileIds);
            log.info("file deleteLogic fileIds delRes:{}", delRes);
        }else{
            log.info("file cleanImage fileIds empty");
        }
        return this.fileInfoService.cleanFile();
    }


    @Override
    public void beforeSave(Map funcMap, FileInfo entity) {
        if(ValueUtils.isBlank(entity.getSuffix())){
            return;
        }

        if(this.fileImageHandle.beforeSaveHandle(this, entity, funcMap)){
            log.info("file before handle image");
            return;
        }
        if(this.fileVideoHandle.beforeSaveHandle(this, entity, funcMap)){
            log.info("file before handle image");
            return;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    @Override
    public void afterSave(Map funcMap, FileInfo entity) {
        if(ValueUtils.isBlank(entity.getSuffix())){
            return;
        }
        if(this.fileImageHandle.afterSaveHandle(this, entity, funcMap)){
           log.info("file after handle image");
           return;
        }
        if(this.fileVideoHandle.afterSaveHandle(this, entity, funcMap)){
            log.info("file after handle video");
            return;
        }
    }

    @Override
    public void beforeUpdate(Map funcMap, FileInfo entity) {

    }

    @Override
    public void afterUpdate(Map funcMap, FileInfo entity) {
    }
}
