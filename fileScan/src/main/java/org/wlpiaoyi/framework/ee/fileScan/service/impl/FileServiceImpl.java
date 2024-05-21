package org.wlpiaoyi.framework.ee.fileScan.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.fileScan.config.FileConfig;
import org.wlpiaoyi.framework.ee.fileScan.domain.model.FileInfo;
import org.wlpiaoyi.framework.ee.fileScan.handler.HandlerInterceptor;
import org.wlpiaoyi.framework.ee.fileScan.service.IFileService;
import org.wlpiaoyi.framework.ee.utils.response.FileResponse;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.SystemException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>    </p>
 * <p><b>{@code @date:}</b>           2024/3/10 21:52</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */
@Slf4j
@Primary
@Service
public class FileServiceImpl implements IFileService {


    @Autowired
    private FileConfig fileConfig;

    private static final Map<String, String> contentTypeMap = new HashMap(){{
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
        put("mp3", "audio/mp3");

        put("default", "application/octet-stream");
    }};


    private File[] filterFiles(File parentFile, String fileName, int fileOrder){
        File[] fa = parentFile.listFiles();
        if(ValueUtils.isBlank(fa)){
            return null;
        }
        List<File> files = new ArrayList(){{ addAll(Arrays.asList(fa)); }};
        if (ValueUtils.isBlank(files)){
            return null;
        }
        List<File> removes = new ArrayList<>();
        if(ValueUtils.isNotBlank(fileName)){
            for (File file : files){
                if(file.getName().contains(fileName)){
                    continue;
                }
                removes.add(file);
            }
        }
        if(ValueUtils.isNotBlank(removes)){
            files.removeAll(removes);
        }

        if(fileOrder > 0) {
            files.sort((f1, f2) -> {
                BasicFileAttributes batt1 = null;
                try {
                    batt1 = Files.readAttributes(f1.toPath(),
                            BasicFileAttributes.class);
                } catch (IOException e) {
                    log.warn("Get file attributes failed:{}", e.getMessage());
                    return 0;
                }
                BasicFileAttributes batt2 = null;
                try {
                    batt2 = Files.readAttributes(f2.toPath(),
                            BasicFileAttributes.class);
                } catch (IOException e) {
                    log.warn("Get file attributes failed:{}", e.getMessage());
                    return 0;
                }
                long sv = 0;
                if (fileOrder == 1) {
                    sv = (batt1.creationTime().toMillis() - batt2.creationTime().toMillis());
                }else if (fileOrder == 2) {
                    sv = (batt2.creationTime().toMillis() - batt1.creationTime().toMillis());
                }else if (fileOrder == 3) {
                    sv = (batt1.lastAccessTime().toMillis() - batt2.lastAccessTime().toMillis());
                }else if (fileOrder == 4) {
                    sv = (batt2.lastAccessTime().toMillis() - batt1.lastAccessTime().toMillis());
                }
                if(sv < 0) {
                    return -1;
                }else if(sv > 0){
                    return 1;
                }
                return 0;
            });
        }else{
            Comparator<Object> compare = Collator.getInstance(java.util.Locale.CHINA);
            files.sort((f1, f2) -> {
                int d1 = f1.isDirectory() ? 0 : 1;
                int d2 = f2.isDirectory() ? 0 : 1;
                int v = d1 - d2;
                if(v != 0){
                    return v;
                }
                return compare.compare(f1.getName(),f2.getName());
            });

        }
        return files.toArray(new File[0]);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>childFile</b>
     * {@link File}
     * </p>
     *
     * <p><b>@param</b> <b>parent</b>
     * {@link FileInfo}
     * </p>
     *
     * <p><b>@param</b> <b>deepCount</b>
     * {@link int}
     * </p>
     *
     * <p><b>@param</b> <b>fileName</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>fileOrder</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/4/19 11:43</p>
     * <p><b>{@code @return:}</b>{@link FileInfo}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    @SneakyThrows
    private FileInfo scan(File childFile, FileInfo parent, int deepCount, String fileName, int fileOrder){
        if(parent == null){
            throw new BusinessException("上级目录不能为空");
        }
        if(ValueUtils.isBlank(parent.getName())){
            throw new BusinessException("上级目录名称不能为空");
        }
        if(deepCount == 0){
            return null;
        }
        if(deepCount < -100){
            throw new BusinessException("目录深度过大");
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setParent(new WeakReference<>(parent));
        parent.getChildren().add(fileInfo);
        fileInfo.setName(childFile.getName());
        if(parent.isRoot()){
            fileInfo.setPath(childFile.getName());
        }else {
            fileInfo.setPath(parent.getPath() + "/" + childFile.getName());
        }
        fileInfo.setDeep(parent.getDeep() + 1);
        fileInfo.setPathBuffer(this.fileConfig.synPathInMap(fileInfo.toString()));
        if(childFile.isFile()){
            fileInfo.setSuffix(fileInfo.getName().substring(childFile.getName().lastIndexOf(".") + 1));
            fileInfo.setLeaf(true);
            fileInfo.setDict(false);
            return fileInfo;
        }
        File[] files = filterFiles(childFile, fileName, fileOrder);
        if (ValueUtils.isBlank(files)){
            fileInfo.setLeaf(true);
            fileInfo.setDict(true);
            return fileInfo;
        }
        fileInfo.setLeaf(false);
        fileInfo.setDict(true);
        fileInfo.setChildren(new ArrayList<>());
        for (File file : files){
            scan(file, fileInfo, deepCount - 1, fileName, fileOrder);
        }
        return fileInfo;
    }
    /**
     * <p><b>{@code @description:}</b>
     * 文件扫描
     * </p>
     *
     * <p><b>@param</b> <b>baseFile</b>
     * {@link File}
     * </p>
     *
     * <p><b>@param</b> <b>deepCount</b>
     * {@link int}
     * </p>
     *
     * <p><b>@param</b> <b>fileName</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>fileOrder</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/11 23:00</p>
     * <p><b>{@code @return:}</b>{@link FileInfo}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    @SneakyThrows
    public FileInfo scanFileInfo(File baseFile, int deepCount, String fileName, int fileOrder){
        FileInfo fileInfo = new FileInfo();
        if(baseFile == null){
            baseFile = new File(this.fileConfig.getFileMenu());
            fileInfo.setRoot(true);
        }else {
            fileInfo.setPath(baseFile.getPath().substring(this.fileConfig.getFileMenu().length()));
        }
        fileInfo.setName(baseFile.getName());
        fileInfo.setDeep(0);
        fileInfo.setPathBuffer(this.fileConfig.synPathInMap(fileInfo.toString()));
        if(baseFile.isFile()){
            fileInfo.setSuffix(fileInfo.getName().substring(baseFile.getName().lastIndexOf(".") + 1));
            fileInfo.setLeaf(true);
            fileInfo.setDict(false);
            return fileInfo;
        }
        File[] files = filterFiles(baseFile, fileName, fileOrder);
        if (ValueUtils.isBlank(files)){
            fileInfo.setLeaf(true);
            fileInfo.setDict(true);
            return fileInfo;
        }
        fileInfo.setDict(true);
        fileInfo.setLeaf(false);
        fileInfo.setChildren(new ArrayList<>());
        for (File file : files){
            scan(file, fileInfo, deepCount, fileName, fileOrder);
        }
        return fileInfo;
    }

    @SneakyThrows
    @Override
    public void download(String path, Map funcMap, HttpServletRequest request, HttpServletResponse response) {
        log.info("service download path:{}", path);
        File file = new File(this.fileConfig.absolutePath(path));
        if(!file.exists()){
            throw new BusinessException("没有找到文件：" + file.getAbsoluteFile());
        }
        if(!file.isFile()){
            throw new BusinessException("不能下载文件夹：" + file.getAbsoluteFile());
        }
        String readType = MapUtils.getString(funcMap, "readType", "inline");
        this.download(file, new HashMap(){{
            put("readType", readType);
            put("fileName", file.getName());
        }}, request, response);
    }

    private final FileResponse fileResponse = FileResponse.getInstance(GsonBuilder.gsonDefault().fromJson(
            DataUtils.readFile(DataUtils.USER_DIR + "/config/content-type.json"), Map.class
    ));

    @Override
    public void download(File file, Map funcMap, HttpServletRequest request, HttpServletResponse response) throws SystemException {

        try{
            String fileName = MapUtils.getString(funcMap, "fileName");
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            suffix = suffix.toLowerCase(Locale.ROOT);
            String contentType = contentTypeMap.get(suffix);
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }

            funcMap.put("contentType", contentType);
            funcMap.put("fileName", fileName);
            this.fileResponse.download(file, funcMap, request, response);

        }catch (Exception e){
            if(e instanceof BusinessException){
                throw (BusinessException)e;
            }
            if(e instanceof SystemException){
                throw (SystemException)e;
            }
            throw new SystemException("文件读取异常", e);
        }
    }

    @SneakyThrows
    @Override
    public void resHtml(FileInfo fileInfo, HttpServletResponse response) {
        List<OutputStream> outputStreams = new ArrayList<>();
        try{
            response.setContentType("text/html");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            outputStreams.add(sos);
            ByteArrayInputStream is = new ByteArrayInputStream(this.createHtml(fileInfo).getBytes(StandardCharsets.UTF_8));
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                sos.write(data, 0, nRead);
                sos.flush();
            }
            sos.close();
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw (BusinessException)e;
            }
            if(e instanceof SystemException){
                throw (SystemException)e;
            }
            throw new SystemException("文件读取异常", e);
        }finally {
            if(ValueUtils.isNotBlank(outputStreams)){
                for (OutputStream outputStream : outputStreams){
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        log.error("data close error", e);
                    }
                }
            }
        }
    }

    @Value("${fileScan.auth.userName:}")
    private String userName;
    @Value("${fileScan.auth.password:}")
    private String password;

    @SneakyThrows
    private String getNavigationElement(String curPath, boolean disable){
        curPath = curPath.replaceAll("\\\\", "/");
        StringBuilder sb = new StringBuilder();
        String url = "/file";
        url += "/info-tree-href/";
        String pathName = curPath;
        if(curPath.contains("/")){
            pathName = curPath.substring(curPath.lastIndexOf("/") + 1);
        }
        if(ValueUtils.isBlank(curPath)){
            url = pathName = "/";
        }else{
            url += this.fileConfig.synPathInMap(curPath);
        }
        if(disable){
            sb.append("<a style=\"pointer-events:none; color:#CCC;\" href='");
        }else{
            sb.append("<a href='");
        }
        sb.append(url);
        sb.append("'>");
        sb.append(pathName);
        if(disable){
            sb.append("&nbsp;");
        }else{
            sb.append("&nbsp;<strong>▼</strong>");
        }
        sb.append("</a>&nbsp;");
        return sb.toString();
    }

//    private final static Map<String, String> PATH_MAP = new ConcurrentHashMap<>();
//
//    public String getFingerprint(String md5FingerprintBase64Str){
//        byte[] md5FingerprintBytes = this.fileConfig.dataDecode(md5FingerprintBase64Str);
//        String md5FingerprintHex = ValueUtils.bytesToHex(md5FingerprintBytes);
//        return PATH_MAP.get(md5FingerprintHex);
//    }

    @SneakyThrows
    @NotNull
    private String createHtml(FileInfo fileInfo) {

        InputStream commonCssIo = HandlerInterceptor.class.getClassLoader().getResourceAsStream("common.css");
        assert commonCssIo != null;
        String commonCssContent = ReaderUtils.loadString(commonCssIo, StandardCharsets.UTF_8);
        InputStream fileHtmlIo = HandlerInterceptor.class.getClassLoader().getResourceAsStream("file.html");
        assert fileHtmlIo != null;
        String fileHtml = ReaderUtils.loadString(fileHtmlIo, StandardCharsets.UTF_8);
        fileHtml = fileHtml.replace("${common.css}", commonCssContent);
        byte[] authKeyBytes = DataUtils.MD(
                (DataUtils.MD(this.userName, DataUtils.KEY_MD5)
                        + DataUtils.MD(this.password, DataUtils.KEY_MD5)).getBytes()
                , DataUtils.KEY_MD5);
        String authKeyBase64Str = this.fileConfig.dataEncode(authKeyBytes);
        StringBuilder sb = new StringBuilder();
        if(ValueUtils.isNotBlank(fileInfo.getPath())){
            sb.append("<div class='item'><h1>");
            String iPath = fileInfo.getPath();
            String kHeads = "";
            while (ValueUtils.isNotBlank(iPath)){
                if(ValueUtils.isBlank(kHeads)){
                    kHeads = getNavigationElement(iPath, true);
                }else{
                    kHeads = getNavigationElement(iPath, false) + "\n" + kHeads;
                }
                int index = iPath.lastIndexOf("/");
                if(index <= 0){
                    iPath = "";
                    kHeads = getNavigationElement(iPath, false) + "\n" + kHeads;
                    break;
                }
                iPath = iPath.substring(0, index);
            }
            sb.append(kHeads);
            sb.append("</h1></div>\n");
        }
        fileHtml = fileHtml.replace("${navigation_bar}", sb.toString());
        if(ValueUtils.isNotBlank(fileInfo.getChildren())){
            for(FileInfo fi : fileInfo.getChildren()){
                final String url;
                if(fi.isDict()){
                    String urlDir = "/file";
                    urlDir += "/info-tree-href/";
                    urlDir += fi.getPathBuffer();
                    url = urlDir;
                }else {
                    String urlFile = "/file";
                    urlFile += "/download/" + fi.getPathBuffer();
                    urlFile += "/" + authKeyBase64Str;
                    urlFile += "/" + URLEncoder.encode( fi.getName(), "UTF-8" );
                    if(!fi.getName().contains(".")){
                        urlFile += "." + fi.getSuffix();
                    }
                    url = urlFile;
                }
                sb.append("<div class='item'><a href='");
                sb.append(url);
                sb.append("'>");
                sb.append(fi.getName());
                if(!fi.isDict() && !fi.getName().contains(".")){
                    sb.append(".").append(fi.getSuffix());
                }
                if(fi.isDict()){
                    sb.append("&nbsp;<strong>▶</strong>");
                }
                sb.append("</a>");

                if(!fi.isDict()){
                    sb.append("&nbsp;&nbsp;<input type=\"button\" value=\"复制URL\" onclick=\"clipboardForUri('").append(url).append("')\" >");
                }
                File file = new File(this.fileConfig.absolutePath(fi.getPath()));
                BasicFileAttributes bAttributes = null;
                try {
                    bAttributes = Files.readAttributes(file.toPath(),
                            BasicFileAttributes.class);
                } catch (IOException e) {
                    log.warn("Get file attributes failed:{}", e.getMessage());
                }
                if(bAttributes != null){
                    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<h4>").append("<strong>创建时间:</strong>")
                            .append(DateUtils.friendCNLocalDateTime(DateUtils.parseToLocalDateTime(bAttributes.creationTime().toMillis())));
                    if(bAttributes.lastAccessTime() != null){
                        sb.append("&nbsp;<strong>上次访问时间:</strong>")
                                .append(DateUtils.friendCNLocalDateTime(DateUtils.parseToLocalDateTime(bAttributes.lastAccessTime().toMillis())));
                    }
                    sb.append("</h4>");
                }
                sb.append("</div>");
                sb.append("<hr/>\n");
            }
        }else{
            sb.append("<div class='item' class=\"empty_data\">没有文件</div>");
        }
        return fileHtml.replace("${body}", sb.toString());
    }


}
