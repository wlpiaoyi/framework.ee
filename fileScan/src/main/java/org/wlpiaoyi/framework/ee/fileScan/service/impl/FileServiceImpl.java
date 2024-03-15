package org.wlpiaoyi.framework.ee.fileScan.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.fileScan.config.FileConfig;
import org.wlpiaoyi.framework.ee.fileScan.domain.model.FileInfo;
import org.wlpiaoyi.framework.ee.fileScan.service.IFileService;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.SystemException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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

    private static Map<String, String> contentTypeMap = new HashMap(){{
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

    @SneakyThrows
    private FileInfo scan(File childFile, FileInfo parent, int deepCount){
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
        fileInfo.setPath(fileInfo.toString());
        fileInfo.setDeep(parent.getDeep() + 1);
        fileInfo.setFingerprint(this.fileConfig.dataEncode(this.fileConfig.getAesCipher().encrypt(fileInfo.toString().getBytes(StandardCharsets.UTF_8))));
        if(childFile.isFile()){
            fileInfo.setSuffix(fileInfo.getName().substring(childFile.getName().lastIndexOf(".") + 1));
            fileInfo.setLeaf(true);
            fileInfo.setDict(false);
            return fileInfo;
        }
        File[] files = childFile.listFiles();
        if (ValueUtils.isBlank(files)){
            fileInfo.setLeaf(true);
            fileInfo.setDict(true);
            return fileInfo;
        }
        fileInfo.setLeaf(false);
        fileInfo.setDict(true);
        fileInfo.setChildren(new ArrayList<>());
        for (File file : files){
            scan(file, fileInfo, deepCount - 1);
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
     * <p><b>{@code @date:}</b>2024/3/11 23:00</p>
     * <p><b>{@code @return:}</b>{@link FileInfo}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    @SneakyThrows
    public FileInfo scanFileInfo(File baseFile, int deepCount){
        FileInfo fileInfo = new FileInfo();
        if(baseFile == null){
            baseFile = new File(this.fileConfig.getFileMenu());
            fileInfo.setRoot(true);
        }
        fileInfo.setName(baseFile.getName());
        fileInfo.setPath(fileInfo.toString());
        fileInfo.setDeep(0);
        fileInfo.setFingerprint(this.fileConfig.dataEncode(this.fileConfig.getAesCipher().encrypt(fileInfo.toString().getBytes(StandardCharsets.UTF_8))));
        if(baseFile.isFile()){
            fileInfo.setSuffix(fileInfo.getName().substring(baseFile.getName().lastIndexOf(".") + 1));
            fileInfo.setLeaf(true);
            fileInfo.setDict(false);
            return fileInfo;
        }
        File[] files = baseFile.listFiles();
        if (ValueUtils.isBlank(files)){
            fileInfo.setLeaf(true);
            fileInfo.setDict(true);
            return fileInfo;
        }
        fileInfo.setDict(true);
        fileInfo.setLeaf(false);
        fileInfo.setChildren(new ArrayList<>());
        for (File file : files){
            scan(file, fileInfo, deepCount);
        }
        return fileInfo;
    }

    @SneakyThrows
    @Override
    public void download(String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response) {
        String path = new String(this.fileConfig.getAesCipher().decrypt(this.fileConfig.dataDecode(fingerprint)));
        File file = new File(this.fileConfig.getFileMenu() + path);
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

    @Override
    public void download(File file, Map funcMap, HttpServletRequest request, HttpServletResponse response) {

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
            this.partDownload(file, funcMap, request, response);

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

    /**
     * <p><b>{@code @description:}</b>
     * 分片下载任务处理
     * </p>
     *
     * <p><b>@param</b> <b>dataInput</b>
     * {@link BufferedInputStream}
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
     * <p><b>{@code @date:}</b>2024/3/13 13:19</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    private boolean handlePartDownload(BufferedInputStream dataInput, Map funcMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String range = request.getHeader(HttpHeaders.RANGE);
        if (ValueUtils.isNotBlank(range) && !"null".equals(range)) {
            long point = 0L;
            // tell the client to allow accept-ranges
            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            int writerType = MapUtils.getInteger(funcMap, "writerType");
            long contentLength = MapUtils.getLong(funcMap, "contentLength");
            long fileLength = MapUtils.getLong(funcMap, "fileLength");
            String readType = MapUtils.getString(funcMap, "readType");
            String rangBytes = range.replaceFirst("bytes=", "");
            String contentRange;
            if (rangBytes.endsWith("-")) { // bytes=270000-
                writerType = 1;
                point = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                /* 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节） */
                contentLength = fileLength - point;
                /*
                 断点开始
                 响应的格式
                 Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                 */
                contentRange = "bytes " + Long.toString(point) + "-" +
                        Long.toString(fileLength - 1) + "/" +
                        Long.toString(fileLength);
            } else { // bytes=270000-320000
                writerType = 2;
                long startIndex = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                long endIndex = Long.parseLong(rangBytes.substring(rangBytes.indexOf("-") + 1));
                point = startIndex;
                /* 客户端请求的是 270000-320000 之间的字节 */
                contentLength = endIndex - startIndex;
                contentLength ++;
                /*
                 断点开始
                 响应的格式
                 Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                 */
                contentRange = range.replace("=", " ") + "/" + Long.toString(fileLength);
            }
            response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
            dataInput.skip(point);

            funcMap.put("writerType", writerType);
            funcMap.put("contentLength", contentLength);
            funcMap.put("fileLength", fileLength);
            funcMap.put("readType", readType);
            return true;
        }
        return false;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 根据文件对象下载数据
     * </p>
     *
     * <p><b>@param</b> <b>file</b>
     * {@link File}
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
     * <p><b>{@code @date:}</b>2024/3/13 13:20</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    private void partDownload(File file, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        List<Closeable> closeables = new ArrayList<>();
        try{
            String contentType = MapUtils.getString(funcMap, "contentType");
            String fileName = MapUtils.getString(funcMap, "fileName");
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }
            OutputStream dataOutput = response.getOutputStream();
            response.reset();
            response.setContentType(contentType);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String readType = MapUtils.getString(funcMap, "readType", "inline");

            final long fileLength = file.length();

            /*
             0: 下载全部数据
             1: 分片下载(start-)
             2: 分片下载(start-end)
             */
            int writerType = 0;
            long contentLength = 0L;

            BufferedInputStream dataInput = new BufferedInputStream(Files.newInputStream(file.toPath()));
            closeables.add(dataInput);

            funcMap.put("writerType", writerType);
            funcMap.put("contentLength", contentLength);
            funcMap.put("fileLength", fileLength);
            funcMap.put("readType", readType);
            if(this.handlePartDownload(dataInput, funcMap, request, response)){
                writerType = MapUtils.getInteger(funcMap, "writerType");
                contentLength = MapUtils.getLong(funcMap, "contentLength");
                readType = MapUtils.getString(funcMap, "readType");
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            }else{
                response.setStatus(HttpServletResponse.SC_OK);
                contentLength = fileLength;
            }

            /*
             如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
             Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
             */
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
            response.setContentType(contentType);
            //设置文件长度
            response.setHeader("Content-disposition", readType + ";filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            // response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getName(), Charsets.UTF_8.name()));

            closeables.add(dataOutput);
            long readLength = 0;
            int bSize = 1024;
            byte[] bytes = new byte[bSize];
            if (writerType == 2) {
                int l;
                long clb = contentLength - bSize;
                while (readLength <= clb) {
                    l = dataInput.read(bytes);
                    readLength += l;
                    dataOutput.write(bytes, 0, l);
                    dataOutput.flush();
                }
                clb = contentLength - readLength;
                if (clb > 0) {
                    l = dataInput.read(bytes, 0, (int) clb);
                    dataOutput.write(bytes, 0, l);
                    dataOutput.flush();
                }
            } else {
                int l;
                while ((l = dataInput.read(bytes)) != -1) {
                    dataOutput.write(bytes, 0, l);
                    dataOutput.flush();
                }
            }
            dataOutput.flush();
            dataOutput.close();
            dataInput.close();
            closeables.remove(dataOutput);
            closeables.remove(dataInput);
        }catch (Exception e){
            if(e instanceof BusinessException){
                throw (BusinessException)e;
            }
            if(e instanceof SystemException){
                throw (SystemException)e;
            }
            if (e instanceof ClientAbortException){
                log.warn("write data error:{}", e.getCause().toString());
                return;
            }
            throw new SystemException("文件读取异常", e);
        }finally {
            if(ValueUtils.isNotBlank(closeables)){
                for (Closeable closeable : closeables){
                    if(closeable instanceof Flushable){
                        try {
                            ((Flushable) closeable).flush();
                        } catch (IOException e) {
                            log.warn("download flush obj failed:{}", e.getCause().toString());
                        }
                    }
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        log.warn("download close obj failed:{}", e.getCause().toString());
                    }
                }
            }
        }
    }

    @Value("${resource.ethName}")
    private String ethName;

    @SneakyThrows
    @Override
    public void resHtml(FileInfo fileInfo, HttpServletResponse response) {
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        String ip = "127.0.0.1";
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            System.out.println("网卡接口名称：" + nif.getName());
            // 获得与该网络接口绑定的 IP 地址，一般只有一个
            Enumeration<InetAddress> addresses = nif.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) { // 只关心 IPv4 地址
                    System.out.println("网卡接口地址：" + addr.getHostAddress());
                    System.out.println();
                    if(nif.getName().equals(ethName)){
                        ip = addr.getHostAddress();
                    }
                }
            }
        }

        List<OutputStream> outputStreams = new ArrayList<>();
        try{
            response.setContentType("text/html");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            outputStreams.add(sos);
            ByteArrayInputStream is = new ByteArrayInputStream(createHtml(fileInfo, ip).getBytes(StandardCharsets.UTF_8));
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
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @NotNull
    private static String createHtml(FileInfo fileInfo, String ip) {
        StringBuilder sb = new StringBuilder();
        for(FileInfo fi : fileInfo.getChildren()){
            String url = "http://" + ip + ":8080/file";
            if(fi.isDict()){
                url += "/info-tree-href/";
                url += fi.getFingerprint();
                url += "?1=1";
            }else {
                url += "/download/" + fi.getFingerprint();
                url += "/" + fi.getName();
                if(!fi.getName().contains(".")){
                    url += "." + fi.getSuffix();
                }
            }
            sb.append("<a href='");
            sb.append(url);
            sb.append("'>");
            sb.append(fi.getPath());
            sb.append("</a>");
            if(fi.isDict()){
                sb.append("&nbsp;>>");
            }
            sb.append("\n<hr/>\n");
        }
        return sb.toString();
    }

}
