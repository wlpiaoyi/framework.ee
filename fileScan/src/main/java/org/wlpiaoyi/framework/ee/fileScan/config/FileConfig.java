package org.wlpiaoyi.framework.ee.fileScan.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.security.AesCipher;

import java.io.File;

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

}
