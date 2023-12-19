package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.file.manager.utils.FileUtils;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/8 16:51
 * {@code @version:}:       1.0
 */
@Slf4j
@Primary
@Service
public class FileServiceImpl implements IFileService {
    @Value("${file.manager.tempPath}")
    private String tempPath;

    @Value("${file.manager.dataPath}")
    private String dataPath;




    @Getter
    private Aes aes;
    {
        try {
            aes = Aes.create().setKey("abcd567890ABCDEF12D4567890ABCDEF").setIV("a1cd567E90123456").load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private static Rsa rsa;

    static {
        try {
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQtcKZfLEQmfxFNhBqvYWeLTaRDw4izgpKS0vD\n" +
                    "QJj3uXiDb59NQnvFHjdyQ/DPOJNmRCuIrm/uR9RC8PwelYUUiDaR/oOw0CiX602kEShflxGaMbnt\n" +
                    "qHho50+Ikd954iZCVjihJcYtyHKbxNYn80IDlr7aoQ47LLrqBS61kWCO3wIDAQAB";
            String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJC1wpl8sRCZ/EU2EGq9hZ4tNpEP\n" +
                    "DiLOCkpLS8NAmPe5eINvn01Ce8UeN3JD8M84k2ZEK4iub+5H1ELw/B6VhRSINpH+g7DQKJfrTaQR\n" +
                    "KF+XEZoxue2oeGjnT4iR33niJkJWOKElxi3IcpvE1ifzQgOWvtqhDjssuuoFLrWRYI7fAgMBAAEC\n" +
                    "gYEAhpMx1PF77Rd23pqHq+xyXYZoj4AzwjRgp3TckUj6uK4YIAtnVz8zwT33jGEYim1vzpQo9CRc\n" +
                    "3XiZMmPP41VjeyJ9IElogGerAk25TlZrwY6YGSOfLCj5xxFCutZB/PZJHtuEG6pZx4qvVMfV8mND\n" +
                    "2By93WkC+LZ6Q8yUgbt446kCQQD3A0ue1De38nwwT43A3tkbELCauuUy6nTpijvJfYZ6buhO2+Bp\n" +
                    "E2ba8rt2sU5vdqjcGgLo78idwISGaK6oYBlNAkEAlfma0NcSGcwf61lNWiuX9NqX9gQFEaHUPZM/\n" +
                    "dqK/vl6Y5J3zRRQMuS0QXX0x8+a6BTrhFzwXNg5tR3Y25TGS2wJAS5S5jcbXqbRLpaih8jL98Vch\n" +
                    "AqdPPE4bKd5/Pr7m6A2JjZ+fwecK4NHG5KGKI3cGYhqfa1D7bLGcm1fqoWCOPQJAEI8SrORSN072\n" +
                    "Z0Hg7IfLq1lHVf5zoNLBYFsVsr+ddCN1tihKZ+Ii1X9IQ0pDba6X82Pg3nPgDDPjlPRUc1HZ6QJB\n" +
                    "ALMdY7bOv+oPDfDvEQ5GbxXliZX0UHXz71nv4OxWKlqQi0QWnhyUY4/zp+qpQ96f9ICui4Gc32z2\n" +
                    "6JIa0fP/UBU=";
            rsa = Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey)
                    .setSignatureAlgorithm(Rsa.SIGNATURE_ALGORITHM_SHA512_WITH_RSA)
                    .setKeyAlgorithm(Rsa.KEY_ALGORTHM_RSA)
                    .loadKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getFilePath(String fingerprint){
        return this.dataPath + "/" + FileUtils.getMd5PathByFingerprint(fingerprint) + FileUtils.getDataSuffixByFingerprint(fingerprint);
    }
    @SneakyThrows
    public String dataEncode(byte[] bytes){
        String res = new String(DataUtils.base64Encode(bytes));
        res = res.replaceAll("\n", "");
        res = res.replaceAll("\r", "");
        res = res.replaceAll("/", "_");
//        res = URLEncoder.encode(res, "UTF-8");
        return res;
    }
    @SneakyThrows
    public byte[] dataDecode(String str){
//        str = URLDecoder.decode(str, "UTF-8");
        str = str.replaceAll("_", "/");
        byte[] bytes = DataUtils.base64Decode(str.getBytes());
        return bytes;
    }

    @Autowired
    private IFileMenuService fileMenuService;


    @SneakyThrows
    @Override
    public boolean upload(FileMenu fileMenu, MultipartFile file, HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        try{

            String fingerprint = FileUtils.moveToFingerprint(file, this.tempPath, this.dataPath);
            if(ValueUtils.isBlank(fileMenu.getId())){
                fileMenu.setId(IdUtils.nextId());
            }

            if(ValueUtils.isBlank(fileMenu.getName())){
                fileMenu.setName(file.getOriginalFilename());
            }
            if(ValueUtils.isBlank(fileMenu.getSize()) && file.getOriginalFilename().contains(".")){
                fileMenu.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            }
            fileMenu.setSize(DataUtils.getSize(this.getFilePath(fingerprint)));
            fileMenu.setFingerprint(this.dataEncode(ValueUtils.hexToBytes(fingerprint.toUpperCase(Locale.ROOT))));
            fileMenu.setToken(this.dataEncode(this.aes.encrypt(fileMenu.getId().toString().getBytes())));
            if(fileMenu.getIsVerifySign() == 1){
                FileInputStream orgFileIo = new FileInputStream(this.getFilePath(fingerprint));
                inputStream = orgFileIo;
                InputStream tokenByteInput = new ByteArrayInputStream(fileMenu.getToken().getBytes());
                final String dataSign = this.dataEncode(rsa.sign(orgFileIo));
                final String tokenSign = this.dataEncode(rsa.sign(tokenByteInput));
                tokenByteInput.close();
                response.setHeader("file-sign", tokenSign + "," + dataSign);
                try {
                    orgFileIo.close();
                    inputStream = null;
                } catch (Exception e) {
                    throw e;
                }
            }
            if(this.fileMenuService.count(Wrappers.<FileMenu>lambdaQuery().eq(FileMenu::getId, fileMenu.getId())) > 0){
                return this.fileMenuService.updateById(fileMenu);
            }else{
                return this.fileMenuService.save(fileMenu);
            }
        } finally {
            if(inputStream != null){
                try{
                    inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

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

//        put("mp4", "video/mpeg4");
//        put("wmv", "video/x-ms-wmv");
        put("mp4", "video/mp4");
        put("wmv", "video/wmv");
        put("mp3", "audio/mp3");

        put("default", "application/octet-stream");
    }};


    @SneakyThrows
    @Override
    public void download(String token, String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        Long id = new Long(new String(this.getAes().decrypt(this.dataDecode(token))));
        FileMenu fileMenu = this.fileMenuService.getById(id);
        if(fileMenu == null){
            throw new BusinessException("没有找到文件");
        }
        String fmFingerprint = ValueUtils.bytesToHex(this.dataDecode(fileMenu.getFingerprint()));
        String ogFingerprint = ValueUtils.bytesToHex(this.dataDecode(fingerprint));
        if(!fmFingerprint.equals(ogFingerprint)){
            throw new BusinessException("文件验证失败");
        }
        this.download(fileMenu, funcMap,request, response);

    }
    @Override
    public void download(FileMenu fileMenu, Map funcMap, HttpServletRequest request, HttpServletResponse response){
        OutputStream outputStream = null;
        List<InputStream> inputStreams = new ArrayList<>();
        try{
            String fingerprint = ValueUtils.bytesToHex(this.dataDecode(fileMenu.getFingerprint()));
            String ogPath = this.getFilePath(fingerprint);
            if(fileMenu.getIsVerifySign() == 1){
                String fileSign = request.getHeader("file-sign");
                if(ValueUtils.isBlank(fileSign)){
                    throw new BusinessException("无权访问文件");
                }
               try{
                   String[] args = fileSign.split(",");
                   String tokenSign = args[0];
                   String dataSign = args[1];
                   ByteArrayInputStream bis = new ByteArrayInputStream(fileMenu.getToken().getBytes());
                   inputStreams.add(bis);
                   if(!rsa.verify(bis, this.dataDecode(tokenSign))){
                       throw new BusinessException("无权访问文件");
                   }
                   bis.close();
                   inputStreams.remove(bis);
                   FileInputStream fis = new FileInputStream(ogPath);
                   inputStreams.add(fis);
                   if(!rsa.verify(fis, this.dataDecode(dataSign))){
                       throw new BusinessException("无权访问文件");
                   }
                   fis.close();
                   inputStreams.remove(fis);
               }catch (Exception e){
                   throw new BusinessException("无权访问文件", e);
               }
            }
            String ft = fileMenu.getSuffix();
            if(ValueUtils.isNotBlank(ft)){
                ft = ft.toLowerCase(Locale.ROOT);
            }
            String contentType = contentTypeMap.get(ft);
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }
            response.setContentType(contentType);
            response.setCharacterEncoding(Charsets.UTF_8.name());
            String readType = MapUtils.getString(funcMap, "readType", "inline");
            response.setHeader("Content-disposition", readType + ";filename=" + URLEncoder.encode(fileMenu.getName(), Charsets.UTF_8.name()));
//            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileMenu.getName(), Charsets.UTF_8.name()));

            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            outputStream = sos;
            FileInputStream fis = new FileInputStream(ogPath);
            inputStreams.add(fis);
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = fis.read(data, 0, data.length)) != -1) {
                sos.write(data, 0, nRead);
                sos.flush();
            }
            fis.close();
            inputStreams.remove(fis);
        }catch (Exception e){
            if(e instanceof  BusinessException){
                throw (BusinessException)e;
            }
            throw new BusinessException("文件读取异常", e);
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(ValueUtils.isNotBlank(inputStreams)){
                for (InputStream inputStream : inputStreams){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public int deleteByFingerprints(List<String> deleteByFingerprints) {

//        return this.base deleteByFingerprints(deleteByFingerprints);
        return 0;
    }


}
