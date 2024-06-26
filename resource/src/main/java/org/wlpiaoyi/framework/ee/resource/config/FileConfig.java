package org.wlpiaoyi.framework.ee.resource.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.resource.utils.FileUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.security.AesCipher;
import org.wlpiaoyi.framework.utils.security.SignVerify;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    文件相关配置
 * {@code @date:}           2023-12-27 16:40:59
 * {@code @version:}:       1.0
 */
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

    @SneakyThrows
    public String signFile(long id, String fingerprint){
        byte[] idBytes = ValueUtils.toBytes(id);
        String idEncode = this.dataEncode(idBytes);
        String argStr = idEncode + ":" + fingerprint;
        return this.dataEncode(FileConfig.getSignVerify().sign(argStr.getBytes()));
    }

    @SneakyThrows
    public boolean verifyFile(long id, String fingerprint, String fileSign){
        byte[] idBytes = ValueUtils.toBytes(id);
        String idEncode = this.dataEncode(idBytes);
        String argStr = idEncode + ":" + fingerprint;
        return FileConfig.getSignVerify().verify(argStr.getBytes(), this.dataDecode(fileSign));
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
