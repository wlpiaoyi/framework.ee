package org.wlpiaoyi.framework.ee.fileScan.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.security.AesCipher;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    文件相关配置
 * {@code @date:}           2023-12-27 16:40:59
 * {@code @version:}:       1.0
 */
@Getter
@Component
@Scope("singleton")
public class FileConfig {


    @Value("${fileScan.fileMenu}")
    private String fileMenu;


    public String absolutePath(String relaPath){
        String path = this.fileMenu.replaceAll("\\\\", "/");
        relaPath = relaPath.replaceAll("\\\\", "/");
        if(!path.endsWith("/")){
            path += "/";
        }
        if(relaPath.startsWith("/")){
            relaPath = relaPath.substring(1);
        }
        return path + relaPath;
    }


    private final AesCipher aesCipher;
    {
        try {
            aesCipher = AesCipher.build().setKey(
                            "214ed7522903443d8b905223907eebbb2fa0978db6dd47d8b7d2c9cbef3b41eb"
                            ,128)
                    .setIV("a1cd567E90123456")
                    .loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    void init(){

    }

    @SneakyThrows
    public String dataEncode(byte[] dataBytes){
        String base64Str = new String(DataUtils.base64Encode(dataBytes));
        base64Str = base64Str.replaceAll("[\r\n]", "");
        base64Str = base64Str.replaceAll("/", "_");
        base64Str = base64Str.replaceAll("\\+", ".");
        while (base64Str.endsWith("=")){
            base64Str = base64Str.substring(0, base64Str.length() - 1);
        }
        return base64Str;
    }
    @SneakyThrows
    public byte[] dataDecode(String base64Str){
        base64Str = base64Str.replaceAll("_", "/");
        base64Str = base64Str.replaceAll("\\.", "+");
        return DataUtils.base64Decode(base64Str.getBytes());
    }

    @SneakyThrows
    public String synPathInMap(String path){
        byte[] shaBytes = DataUtils.MD(path.getBytes(), DataUtils.KEY_SHA);
        String fingerprint = ValueUtils.bytesToHex(shaBytes);
        String res = PATH_MAP.putIfAbsent(fingerprint, path);
        if(ValueUtils.isBlank(res)){
            String absolutePath = CACHE_BASE_PATH + this.getFingerprintPath(fingerprint);
            File absoluteFile = new File(absolutePath);
            absoluteFile.mkdirs();
            absolutePath += ".dat";
            absoluteFile = new File(absolutePath);
            if(!absoluteFile.exists()){
                WriterUtils.overwrite(absoluteFile, path, StandardCharsets.UTF_8);
            }
        }
        return this.dataEncode(shaBytes);
    }
    private String getFingerprintPath(String fingerprint){
        StringBuilder fingerprintPath = new StringBuilder();
        for (int i = 0; i < fingerprint.length(); i+=2) {
            String fn = fingerprint.substring(i, i+2);
            fingerprintPath.append(fn).append("/");
        }
        return fingerprintPath.toString();
    }

    @SneakyThrows
    public String getPathByBuffer(String buffer){
        byte[] shaBytes = this.dataDecode(buffer);
        String fingerprint = ValueUtils.bytesToHex(shaBytes);
        String path = PATH_MAP.get(ValueUtils.bytesToHex(shaBytes));
        if(ValueUtils.isBlank(path)){
            String absolutePath = CACHE_BASE_PATH + this.getFingerprintPath(fingerprint) + ".dat";
            path = ReaderUtils.loadString(absolutePath, StandardCharsets.UTF_8);
            if(ValueUtils.isBlank(path)){
                return null;
            }
            PATH_MAP.put(fingerprint, path);
        }
        return path;
    }


    private final static Map<String, String> PATH_MAP = new ConcurrentHashMap<>();
    private final static String CACHE_BASE_PATH;

    static {
        String cacheBasePath = DataUtils.USER_DIR.replaceAll("\\\\", "/");
        if(!cacheBasePath.endsWith("/")){
            cacheBasePath = cacheBasePath + "/";
        }
        cacheBasePath += "cache/fileScan/";
        CACHE_BASE_PATH = cacheBasePath;
        File cacheBaseFile = new File(CACHE_BASE_PATH);
        if(!cacheBaseFile.exists()){
            cacheBaseFile.mkdirs();
        }
    }
}
