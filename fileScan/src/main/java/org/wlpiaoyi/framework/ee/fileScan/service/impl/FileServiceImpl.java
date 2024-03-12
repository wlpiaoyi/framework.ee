package org.wlpiaoyi.framework.ee.fileScan.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
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
        put("wmv", "video/wmv");
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
        if(file == null){
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

        List<Closeable> closeables = new ArrayList<>();
        try{
            String fileName = MapUtils.getString(funcMap, "fileName");
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            suffix = suffix.toLowerCase(Locale.ROOT);
            String contentType = contentTypeMap.get(suffix);
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }

            ((Map) funcMap).put("contentType", contentType);
            ((Map) funcMap).put("fileName", fileName);
            this.downloadPart(file, funcMap, request, response);

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
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void downloadPart(File file, Map<String, ?> funcMap, HttpServletRequest request, HttpServletResponse response){
        List<Closeable> closeables = new ArrayList<>();
        try{
            String contentType = MapUtils.getString(funcMap, "contentType");
            String fileName = MapUtils.getString(funcMap, "fileName");
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setContentType(contentType);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String readType = MapUtils.getString(funcMap, "readType", "inline");

            long fileLength = file.length();
            InputStream ins = new FileInputStream(file);

            long point = 0L;
            int rangeSwitch = 0;
            long contentLength;

            BufferedInputStream bis = new BufferedInputStream(ins);
            // tell the client to allow accept-ranges
//            response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

            // client requests a file block download start byte
            String range = request.getHeader(HttpHeaders.RANGE);
            if (ValueUtils.isNotBlank(range) && !"null".equals(range)) {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                String rangBytes = range.replaceFirst("bytes=", "");
                if (rangBytes.endsWith("-")) { // bytes=270000-
                    rangeSwitch = 1;
                    point = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                    contentLength = Math.min(fileLength - point, fileLength / 20); // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
                    contentLength = Math.max(1024 * 1024 * 30, contentLength);
                } else { // bytes=270000-320000
                    rangeSwitch = 2;
                    String startIndex = rangBytes.substring(0, rangBytes.indexOf("-"));
                    String endIndex = rangBytes.substring(rangBytes.indexOf("-") + 1);
                    point = Long.parseLong(startIndex);
                    // 客户端请求的是 270000-320000 之间的字节
                    contentLength = Math.min(Long.parseLong(endIndex) - point + 1 - point, fileLength / 20);
                    contentLength = Math.max(1024 * 1024 * 30, contentLength);
                }
                readType = "inline";
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                contentLength = fileLength;
            }

            // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
            // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

            // 断点开始
            // 响应的格式是:
            // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            if (rangeSwitch == 1) {
                String contentRange = new StringBuffer("bytes ").append(new Long(point).toString()).append("-")
                        .append(new Long(fileLength - 1).toString()).append("/")
                        .append(new Long(fileLength).toString()).toString();
                response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
                bis.skip(point);
            } else if (rangeSwitch == 2) {
                String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
                response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
                bis.skip(point);
            } else {
                String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
                        .append(fileLength).toString();
                response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
            }
            response.setContentType(contentType);
            //设置文件长度
            response.setHeader("Content-disposition", readType + ";filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            closeables.add(out);
            int n = 0;
            long readLength = 0;
            int bsize = 1024;
            byte[] bytes = new byte[bsize];
            if (rangeSwitch == 2) {
                // 针对 bytes=27000-39000 的请求，从27000开始写数据
                while (readLength <= contentLength - bsize) {
                    n = bis.read(bytes);
                    readLength += n;
                    out.write(bytes, 0, n);
                }
                if (readLength <= contentLength) {
                    n = bis.read(bytes, 0, (int) (contentLength - readLength));
                    out.write(bytes, 0, n);
                }
            } else {
                while ((n = bis.read(bytes)) != -1) {
                    out.write(bytes, 0, n);
                }
            }
            out.flush();
            out.close();
            bis.close();
            closeables.remove(out);
            closeables.remove(bis);

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


    @SneakyThrows
    @Override
    public void resHtml(FileInfo fileInfo, HttpServletResponse response) {
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        String ip = "127.0.0.1";
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            // 获得与该网络接口绑定的 IP 地址，一般只有一个
            Enumeration<InetAddress> addresses = nif.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) { // 只关心 IPv4 地址
                    System.out.println("网卡接口名称：" + nif.getName());
                    System.out.println("网卡接口地址：" + addr.getHostAddress());
                    System.out.println();
                    if(nif.getName().equals("eth2")){
                        ip = addr.getHostAddress();
                    }
                }
            }
        }

        List<OutputStream> outputStreams = new ArrayList<>();
        try{
            StringBuffer sb = new StringBuffer();
            for(FileInfo fi : fileInfo.getChildren()){
                String url = "http://" + ip + ":8080/file";
                if(fi.isDict()){
                    url += "/info-tree-href/";
                }else {
                    url += "/download/";
                }
                url += fi.getFingerprint() + "?readType=inline";
                sb.append("<a href='");
                sb.append(url);
                sb.append("'>");
                sb.append(fi.getPath());
                sb.append("</a>");
                if(!fi.isDict()){
                    sb.append("&nbsp;<a href=\"#\" onclick=\"alert('已经复制Url');window.clipboardData.setData('Text','" + url + "');\">复制Url</a>");
                }
                sb.append("\n<hr/>\n");
            }
            response.setContentType("text/html");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            outputStreams.add(sos);
            ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
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
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
