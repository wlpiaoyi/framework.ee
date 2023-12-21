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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.file.manager.utils.FileUtils;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.security.AesCipher;
import org.wlpiaoyi.framework.utils.security.RsaCipher;
import org.wlpiaoyi.framework.utils.security.SignVerify;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private AesCipher aesCipher;
    {
        try {
            aesCipher = AesCipher.build().setKey(
                    "104ed7522903443d8b905223907eebbb2fa0978db6dd47d8b7d2c9cbef3b41eb"
                    ,128)
                    .setIV("a1cd567E90123456")
                    .loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private static SignVerify signVerify;

    static {
        try {
            String publicKey = "MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZp\n" +
                    "RV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fn\n" +
                    "xqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuE\n" +
                    "C/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJ\n" +
                    "FnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImo\n" +
                    "g9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAMyDBLj55PknyzfXRfzByz3MDmt5FPwMN0HO\n" +
                    "00v6c3tV0l4E0oZuW/IOdXSF0TdTaa2jHQMarkPP5v8Mc83oZ50splFBJ6F0y+Jk7lvOh8bHTl46\n" +
                    "on5W0T7w8Qy8/LR8BZNVgcj9Mizcxd1eVKQAXIMgb6u2MZ8ryZEA+lWALOSd";
            String privateKey = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2\n" +
                    "USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4\n" +
                    "O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmC\n" +
                    "ouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCB\n" +
                    "gLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhR\n" +
                    "kImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIULqGv+4HdEYM5CqUFM48ksAmDFko==";
            signVerify = SignVerify.build().setPublicKey(publicKey).setPrivateKey(privateKey).loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getFilePath(String fingerprintHex){
        return this.dataPath + "/" + FileUtils.getMd5PathByFingerprint(fingerprintHex) + FileUtils.getDataSuffixByFingerprint(fingerprintHex);
    }
    @SneakyThrows
    public String dataEncode(byte[] bytes){
        String res = new String(DataUtils.base64Encode(bytes));
        res = res.replaceAll("\n", "");
        res = res.replaceAll("\r", "");
        res = res.replaceAll("/", "_");
        return res;
    }
    @SneakyThrows
    public byte[] dataDecode(String str){
        str = str.replaceAll("_", "/");
        byte[] bytes = DataUtils.base64Decode(str.getBytes());
        return bytes;
    }

    @Autowired
    private IFileMenuService fileMenuService;


    @SneakyThrows
    @Override
    public boolean upload(FileMenu fileMenu, MultipartFile file, HttpServletResponse response) throws IOException {
        List<InputStream> inputStreams = new ArrayList<>();
        try{

            String fingerprintHex = FileUtils.moveToFingerprintHex(file, this.tempPath, this.dataPath);
            if(ValueUtils.isBlank(fileMenu.getId())){
                fileMenu.setId(IdUtils.nextId());
            }

            if(ValueUtils.isBlank(fileMenu.getName())){
                fileMenu.setName(file.getOriginalFilename());
            }
            if(ValueUtils.isBlank(fileMenu.getSuffix())){
                if(fileMenu.getName().contains(".")){
                    fileMenu.setSuffix(fileMenu.getName().substring(fileMenu.getName().lastIndexOf(".") + 1));
                }else if(file.getOriginalFilename().contains(".")){
                    fileMenu.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
                }
            }

            fileMenu.setSize(DataUtils.getSize(this.getFilePath(fingerprintHex)));
            fileMenu.setFingerprint(this.dataEncode(ValueUtils.hexToBytes(fingerprintHex.toUpperCase(Locale.ROOT))));
            fileMenu.setToken(this.dataEncode(this.aesCipher.encrypt(fileMenu.getId().toString().getBytes())));
            if(fileMenu.getIsVerifySign() == 1){
                FileInputStream orgFileIo = new FileInputStream(this.getFilePath(fingerprintHex));
                inputStreams.add(orgFileIo);
                InputStream tokenByteInput = new ByteArrayInputStream(fileMenu.getToken().getBytes());
                final String dataSign = this.dataEncode(signVerify.sign(orgFileIo));
                final String tokenSign = this.dataEncode(signVerify.sign(tokenByteInput));
                tokenByteInput.close();
                response.setHeader("file-sign", tokenSign + "," + dataSign);
                try {
                    orgFileIo.close();
                    inputStreams.remove(orgFileIo);
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
        Long id = new Long(new String(this.aesCipher.decrypt(this.dataDecode(token))));
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
        List<OutputStream> outputStreams = new ArrayList<>();
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
                   if(!signVerify.verify(bis, this.dataDecode(tokenSign))){
                       throw new BusinessException("无权访问文件");
                   }
                   bis.close();
                   inputStreams.remove(bis);
                   FileInputStream fis = new FileInputStream(ogPath);
                   inputStreams.add(fis);
                   if(!signVerify.verify(fis, this.dataDecode(dataSign))){
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
            String filename = fileMenu.getName();
            if(!filename.contains(".")){
                filename += "." + fileMenu.getSuffix();
            }
            response.setHeader("Content-disposition", readType + ";filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8.name()));
//            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileMenu.getName(), Charsets.UTF_8.name()));

            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            outputStreams.add(sos);
            FileInputStream fis = new FileInputStream(ogPath);
            inputStreams.add(fis);
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = fis.read(data, 0, data.length)) != -1) {
                sos.write(data, 0, nRead);
                sos.flush();
            }
            sos.close();
            outputStreams.remove(sos);
            fis.close();
            inputStreams.remove(fis);
        }catch (Exception e){
            if(e instanceof  BusinessException){
                throw (BusinessException)e;
            }
            throw new BusinessException("文件读取异常", e);
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<String> cleanFile() {
        List<String> fingerprints = this.fileMenuService.cleanFile();
        if(ValueUtils.isBlank(fingerprints)){
            return null;
        }
        for (String fingerprint : fingerprints){
            String fingerprintHex = ValueUtils.bytesToHex(this.dataDecode(fingerprint));
            String filePath = this.getFilePath(fingerprintHex);
            File file = new File(filePath);
            if(file.exists()){
                if(file.delete()){
                    log.info("file.clean delete success for file [{}]", file.getAbsolutePath());
                }else{
                    log.warn("file.clean delete failed for file [{}]", file.getAbsolutePath());
                }
            }
            String fingerprintPath = FileUtils.getMd5PathByFingerprint(fingerprintHex);
            while (fingerprintPath.length() > 0 && fingerprintPath.contains("/")){
                fingerprintPath = fingerprintPath.substring(0, fingerprintPath.lastIndexOf("/"));
                String absPath = this.dataPath + "/" + fingerprintPath;
                File removeFile = new File(absPath);
                if(ValueUtils.isNotBlank(removeFile.list())){
                    break;
                }
                if(removeFile.delete()){
                    log.info("file.clean delete success for path [{}]", removeFile.getAbsolutePath());
                }else{
                    log.warn("file.clean delete failed for path [{}]", removeFile.getAbsolutePath());
                }
            }
        }
        return fingerprints;
    }


}
