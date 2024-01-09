package org.wlpiaoyi.framework.ee.resource.utils;


import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 15:43
 * {@code @version:}:       1.0
 */
public class FileUtils extends DataUtils{

    private static final Random random = new Random();

    public static String createTempFilePath(String targetPath){
        String filePath= targetPath + "/" + DateUtils.formatToString(new Date(), "yyyyMMddHHmmss_SSSSSS") + "." + Math.abs(random.nextInt() % 10000);
        return filePath;

    }

    public static String writeFileToTargetPath(InputStream fileInput, String targetPath) throws IOException {
        String filePath = createTempFilePath(targetPath);
        File tempFileWriting = new java.io.File(filePath + ".writing");
        OutputStream out = null;
        try {
            byte[] buffer = new byte[1024];
            int readIndex = 0;
            out = new FileOutputStream(tempFileWriting);
            while ((readIndex = fileInput.read(buffer)) != -1) {
                out.write(buffer, 0, readIndex);
            }
        }catch (Exception e){
            if(out != null){
                out.flush();
                out.close();
                out = null;
            }
            tempFileWriting.delete();
            throw e;
        }finally {
            if(out != null){
                out.flush();
                out.close();
                out = null;
            }
        }
        String tempFilePath = filePath + ".done.temp";
        File tempFile = new java.io.File(tempFilePath);
        tempFileWriting.renameTo(tempFile);
        return tempFilePath;
//        String fingerprintHex = FileUtils.mergeToFingerprintHex(tempFile, savePath);
//        return fingerprintHex;
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

    public static String concatAbsolutePath(String basePath, String relativePath){
        String absolutePath = basePath;
        if(!absolutePath.endsWith("/")){
            absolutePath = absolutePath + "/";
        }
        if(relativePath.startsWith("/")){
            relativePath = relativePath.substring(1);
        }
        return absolutePath + relativePath;
    }

    public static String getDataSuffixByFingerprintHex(String fingerprintHex){
        final String dataValue = fingerprintHex.substring(40);
        return dataValue.substring(0, dataValue.length() / 2) + "." + dataValue.substring(dataValue.length() / 2);
    }

    @SneakyThrows
    public static String getFingerprintHex(java.io.File orgFile){
        return DataUtils.MD(orgFile, DataUtils.KEY_SHA) + DataUtils.MD(orgFile, DataUtils.KEY_MD5);
    }

    /**
     * Store file base on file fingerprints
     * @param tempPath
     * @param fingerprintHex
     * @param savePath
     * @return: boolean
     * @author: wlpia
     * @date: 2024/1/4 15:40
     */
    @SneakyThrows
    public static boolean mergeByFingerprintHex(java.io.File tempPath, final String fingerprintHex, String savePath) {
        try{
            final String md5Path = getMd5PathByFingerprintHex(fingerprintHex);
            final String dataSuffix = getDataSuffixByFingerprintHex(fingerprintHex);
            //文件夹路径
            String dirPath = FileUtils.concatAbsolutePath(savePath, md5Path);
            java.io.File md5File = new java.io.File(dirPath);
            if (!md5File.exists()) {// 判断目录是否存在
                md5File.mkdirs();
            }
            //文件路径
            String filePath = dirPath + dataSuffix;
            md5File = new java.io.File(filePath);
            if(md5File.exists()) return true;
            if(!tempPath.renameTo(md5File)){
                throw new BusinessException("File.MoveError");
            }
            return false;
        } finally {
            if(tempPath.exists()) tempPath.delete();
        }
    }

}

