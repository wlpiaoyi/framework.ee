package org.wlpiaoyi.framework.ee.resource.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.ee.resource.utils.SpringUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.security.AesCipher;
import org.wlpiaoyi.framework.utils.security.SignVerify;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    文件相关配置
 * {@code @date:}           2023-12-27 16:40:59
 * {@code @version:}:       1.0
 */
@Slf4j
@Component
@Scope("singleton")
public class FileConfig {

    @Getter
    @Value("${resource.tempPath}")
    private String tempPath;

    @Getter
    @Value("${resource.dataPath}")
    private String dataPath;

    @Getter
    private AesCipher aesCipher;
    {
        try {
            Environment env = SpringUtils.getBean(Environment.class);
            String aesKey = env.getProperty("resource.aes.key");
            String aesIV = env.getProperty("resource.aes.iv");
            aesCipher = AesCipher.build().setKey(
                            aesKey
                            ,128)
                    .setIV(aesIV)
                    .loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private SignVerify signVerify;
    {
        try {
            try {
                Environment env = SpringUtils.getBean(Environment.class);
                String publicKey = env.getProperty("resource.sign.publicKey");
                String privateKey = env.getProperty("resource.sign.privateKey");
                signVerify = SignVerify.build().setPublicKey(publicKey).setPrivateKey(privateKey).loadConfig();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getFilePathByFingerprintHex(String fingerprintHex){
        String relativePath = FileUtils.getMd5PathByFingerprintHex(fingerprintHex) + FileUtils.getDataSuffixByFingerprintHex(fingerprintHex);
        return FileUtils.concatAbsolutePath(this.getDataPath(), relativePath) ;
    }
    public String getFilePathByFingerprint(String fingerprint){
        String fingerprintHex = this.parseFingerprintToHex(fingerprint);
        return this.getFilePathByFingerprintHex(fingerprintHex);
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

    public String parseFingerprintToHex(String fingerprint){
        return ValueUtils.bytesToHex(this.dataDecode(fingerprint));
    }

    public String parseFingerprintHexTo(String fingerprintHex){
        return this.dataEncode(ValueUtils.hexToBytes(fingerprintHex.toUpperCase(Locale.ROOT)));
    }

//    @SneakyThrows
//    public String signFile(long id, String fingerprint){
//        byte[] idBytes = ValueUtils.toBytes(id);
//        String idEncode = this.dataEncode(idBytes);
//        String argStr = idEncode + ":" + fingerprint;
//        return this.dataEncode(FileConfig.getSignVerify().sign(argStr.getBytes()));
//    }

    @SneakyThrows
    public boolean verifyFile(long id, String fingerprint, String fileSign){
        byte[] idBytes = ValueUtils.toBytes(id);
        String idEncode = this.dataEncode(idBytes);
        String argStr = idEncode + ":" + fingerprint;
        return this.getSignVerify().verify(argStr.getBytes(), this.dataDecode(fileSign));
    }

    @SneakyThrows
    public String encodeToken(long id, String fingerprint){
        byte[] idBytes = ValueUtils.toBytes(id);
        byte[] fpBytes = fingerprint.getBytes();
        int idL = idBytes.length;
        int fpL = fpBytes.length;
        byte[] valueBytes = new byte[idL + fpL + 2];
        int vi = 0;
        valueBytes[vi ++] = (byte) idBytes.length;
        int vti = 0;
        while (vti < idL){
            valueBytes[vi ++] = idBytes[vti ++];
        }
        valueBytes[vi ++] = (byte) fpL;
        vti = 0;
        while (vti < fpL){
            valueBytes[vi ++] = fpBytes[vti ++];
        }
        return this.dataEncode(this.getAesCipher().encryptFill(valueBytes, 70));
    }

    @SneakyThrows
    public Object[] decodeToken(String token){
        byte[] valueBytes = this.getAesCipher().decryptFill(this.dataDecode(token), 70);
        int vi = 0;
        byte[] lbs = new byte[1];
        lbs[0] = valueBytes[vi ++];
        int idL = (int) ValueUtils.toLong(lbs);
        byte[] idBytes = new byte[idL];
        int vti = 0;
        while (vti < idL){
            idBytes[vti ++] = valueBytes[vi ++];
        }
        lbs[0] = valueBytes[vi ++];
        int fL = (int) ValueUtils.toLong(lbs);
        byte[] fBytes = new byte[fL];
        vti = 0;
        while (vti < fL){
            fBytes[vti ++] = valueBytes[vi ++];
        }
        String fingerprint = new String(fBytes);
        return new Object[]{ValueUtils.toLong(idBytes), fingerprint};
    }

}
