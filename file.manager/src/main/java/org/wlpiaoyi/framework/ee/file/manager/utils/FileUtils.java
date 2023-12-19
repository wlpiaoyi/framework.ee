package org.wlpiaoyi.framework.ee.file.manager.utils;


import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.IOException;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 15:43
 * {@code @version:}:       1.0
 */
public class FileUtils extends DataUtils{

    /**
     * Store file base on file fingerprints
     * @return
     */
    public static String moveToFingerprint(MultipartFile file, String tempPath, String savePath) throws IOException {
        if(file.isEmpty()){
            throw new BusinessException("File.EmptyError");
        }
        String fileName = StringUtils.getUUID32();
        FileUtils.makeDir(tempPath);

        java.io.File tempFile = new java.io.File(tempPath + "/" + fileName);
        file.transferTo(tempFile);

        String fingerprint = FileUtils.moveToFingerprint(tempFile, savePath);
        return fingerprint;

    }
    public static String getMd5ValueByFingerprint(String fingerprint){
        return fingerprint.substring(0, 32);
    }

    public static String getMd5PathByFingerprint(String fingerprint){
        String md5Value = getMd5ValueByFingerprint(fingerprint);
        String md5Path = "";
        for (int i = 0; i < md5Value.length(); i+=2) {
            String fn = md5Value.substring(i, i+2);
            md5Path +=  fn + "/";
        }
        return md5Path;
    }
    public static String getDataSuffixByFingerprint(String fingerprint){
        final String dataValue = fingerprint.substring(32);
        return dataValue.substring(0, 32) + "." + dataValue.substring(32);
    }

    /**
     * Store file base on file fingerprints
     * @param orgFile
     * @return
     */
    @SneakyThrows
    public static String moveToFingerprint(java.io.File orgFile, String savePath) {
        try{
            final String fingerprint = DataUtils.MD5PLUS(orgFile);
            final String md5Path = getMd5PathByFingerprint(fingerprint);
            final String dataSuffix = getDataSuffixByFingerprint(fingerprint);
            String oPath = savePath + "/" + md5Path;
            java.io.File md5File = new java.io.File(oPath);
            if (!md5File.exists()) {// 判断目录是否存在
                md5File.mkdirs();
            }
            md5File = new java.io.File(oPath + dataSuffix);
            if(md5File.exists()) return fingerprint;
            if(!orgFile.renameTo(md5File)){
                throw new BusinessException("File.MoveError");
            }
            return fingerprint;
        } finally {
            if(orgFile.exists()) orgFile.delete();
        }
    }

}

