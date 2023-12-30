package org.wlpiaoyi.framework.ee.file.manager.utils;


import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;

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
    public static String mergeToFingerprintHex(InputStream fileInput, String tempPath, String savePath) throws IOException {

        String fileName = StringUtils.getUUID32();
        File tempFile = new java.io.File(tempPath + "/" + fileName);
        OutputStream out = null;
        try {
            byte[] buffer = new byte[1024];
            int readIndex = 0;
            out = new FileOutputStream(tempFile);
            while ((readIndex = fileInput.read(buffer)) != -1) {
                out.write(buffer, 0, readIndex);
            }
        }finally {
            if(out != null){
                out.flush();
                out.close();
            }
        }

        String fingerprintHex = FileUtils.mergeToFingerprintHex(tempFile, savePath);
        return fingerprintHex;
    }

    public static String getMd5ValueByFingerprintHex(String fingerprintHex){
        return fingerprintHex.substring(0, 40);
    }

    public static String getMd5PathByFingerprintHex(String fingerprintHex){
        String md5Value = getMd5ValueByFingerprintHex(fingerprintHex);
        String md5Path = "";
        for (int i = 0; i < md5Value.length(); i+=2) {
            String fn = md5Value.substring(i, i+2);
            md5Path +=  fn + "/";
        }
        return md5Path;
    }
    public static String getDataSuffixByFingerprintHex(String fingerprintHex){
        final String dataValue = fingerprintHex.substring(40);
        return dataValue.substring(0, dataValue.length() / 2) + "." + dataValue.substring(dataValue.length() / 2);
    }

    /**
     * Store file base on file fingerprints
     * @param orgFile
     * @return
     */
    @SneakyThrows
    public static String mergeToFingerprintHex(java.io.File orgFile, String savePath) {
        try{
            final String fingerprintHex =  DataUtils.MD(orgFile, DataUtils.KEY_SHA) + DataUtils.MD(orgFile, DataUtils.KEY_MD5);
            final String md5Path = getMd5PathByFingerprintHex(fingerprintHex);
            final String dataSuffix = getDataSuffixByFingerprintHex(fingerprintHex);
            String oPath = savePath + "/" + md5Path;
            java.io.File md5File = new java.io.File(oPath);
            if (!md5File.exists()) {// 判断目录是否存在
                md5File.mkdirs();
            }
            md5File = new java.io.File(oPath + dataSuffix);
            if(md5File.exists()) return fingerprintHex;
            if(!orgFile.renameTo(md5File)){
                throw new BusinessException("File.MoveError");
            }
            return fingerprintHex;
        } finally {
            if(orgFile.exists()) orgFile.delete();
        }
    }

}

