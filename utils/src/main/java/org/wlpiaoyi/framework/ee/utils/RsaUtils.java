package org.wlpiaoyi.framework.ee.utils;

import lombok.NonNull;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;

/**
 *
 */
public class RsaUtils {

    /**
     * 加密
     * @param rsa
     * @param bytes
     * @return base64
     */
    public static String encrypt(@NonNull Rsa rsa, @NonNull byte[] bytes) {
        try {
            byte[] encodedData = rsa.encryptByPrivateKey(bytes);
            String base64Str = StringUtils.base64Encode(encodedData);
            return base64Str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密
     * @param rsa
     * @param value base64
     * @return byte[]
     */
    public static byte[] decrypt(@NonNull Rsa rsa, @NonNull String value) {
        try {
            byte[] bytes = StringUtils.base64DecodeToBytes(value);
            byte[] decodedData = rsa.decryptByPrivateKey(bytes);
            return decodedData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
