package org.wlpiaoyi.framework.ee.resource.biz.service.impl.file;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.http.HttpHeaders;
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
import org.wlpiaoyi.framework.ee.utils.response.FileResponse;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.SystemException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

/**
 * <p><b>{@code @description:}</b>  文件下载</p>
 * <p><b>{@code @date:}</b>         2024/3/13 13:27</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
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
    public String save(Object fileIo, FileInfo entity, Map funcMap){
        return this.fileInfoService.save(fileIo, entity, funcMap, this);
    }

    /**
     * <p><b>{@code @description:}</b> 
     * 根据token下载文件
     * </p>
     * 
     * <p><b>@param</b> <b>token</b>
     * {@link String}
     * </p>
     * 
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * </p>
     * 
     * <p><b>@param</b> <b>request</b>
     * {@link HttpServletRequest}
     * </p>
     * 
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/13 13:23</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    @SneakyThrows
    @Override
    public void download(String token, Map funcMap, HttpServletRequest request, HttpServletResponse response){
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

    private final FileResponse fileResponse = FileResponse.getInstance(GsonBuilder.gsonDefault().fromJson(
            DataUtils.readFile(DataUtils.USER_DIR + "/config/content-type.json"), Map.class
    ));

    /**
     * <p><b>{@code @description:}</b> 
     * 根据文件信息对象下载文件
     * </p>
     * 
     * <p><b>@param</b> <b>entity</b>
     * {@link FileInfo}
     * </p>
     * 
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * </p>
     * 
     * <p><b>@param</b> <b>request</b>
     * {@link HttpServletRequest}
     * </p>
     * 
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/13 13:22</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    @Override
    public void download(FileInfo entity, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        String dataType = MapUtils.getString(funcMap, "dataType", "general");
        if(this.fileImageHandle.canDownloadByThumbnail(entity.getSuffix(), dataType)){
            entity = this.fileImageHandle.getThumbnailFileInfo(this, entity);
        }else if(this.fileVideoHandle.canDownloadByScreenshot(entity.getSuffix(), dataType)){
            entity = this.fileVideoHandle.getScreenshotFileInfo(this, entity);
        }
        if(entity.getIsVerifySign() == 1){
            String fileSign = MapUtils.getString(funcMap, "fileSign");
            if(ValueUtils.isBlank(fileSign)){
                throw new BusinessException("无权访问文件");
            }
            try{
                if(!this.fileConfig.verifyFile(entity.getId(), entity.getFingerprint(), fileSign)){
                    throw new BusinessException("无权访问文件");
                }
            }catch (Exception e){
                throw new BusinessException("无权访问文件", e);
            }
        }
        String ft = entity.getSuffix();
        if(ValueUtils.isNotBlank(ft)){
            ft = ft.toLowerCase(Locale.ROOT);
        }
        String contentType = this.fileResponse.getContentTypeMap().get(ft);
        if(ValueUtils.isBlank(contentType)){
            contentType = this.fileResponse.getContentTypeMap().get("default");
        }
        funcMap.put("contentType", contentType);
        String fileName = entity.getName();
        if(!fileName.contains(".")){
            fileName += "." + entity.getSuffix();
        }
        funcMap.put("fileName", fileName);
        String ogPath = this.fileConfig.getFilePathByFingerprint(entity.getFingerprint());
        try {
            this.fileResponse.download(new File(ogPath), funcMap, request, response);
        } catch (SystemException e) {
            log.error("download file error", e);
            throw new BusinessException(e.getMessage());
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
