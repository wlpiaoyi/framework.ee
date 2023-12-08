package org.wlpiaoyi.framework.ee.file.manager.utils;


import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    public static String moveToFingerprintPath(MultipartFile file, String tempPath, String savePath) throws IOException {
        if(file.isEmpty()){
            throw new BusinessException("File.EmptyError");
        }
        String fileName = StringUtils.getUUID32();
        FileUtils.makeDir(tempPath);

        java.io.File tempFile = new java.io.File(tempPath + "/" + fileName);
        file.transferTo(tempFile);

        String md5Value = FileUtils.moveToFingerprintPath(tempFile, savePath);
        return md5Value;

    }

    public static String parseFingerprintToPath(String md5Value){
        String args[] = md5Value.split(":");
        final String md5Path = args[0];
        final String md5Size = args[1];
        String oPath = parseMd5PathToPath(md5Path);
        return oPath + "." + md5Size;
    }
    private static String parseMd5PathToPath(String md5Path){
        String oPath = "";
        for (int i = 0; i < md5Path.length(); i+=2) {
            String fn = md5Path.substring(i, i+2);
            oPath +=  fn + "/";
        }
        return oPath;
    }


    /**
     * Store file base on file fingerprints
     * @param orgFile
     * @return
     */
    @SneakyThrows
    public static String moveToFingerprintPath(java.io.File orgFile, String savePath) {
        try{
            final String md5Path = DataUtils.MD5(orgFile);
            final String md5Size = DataUtils.MD5((DataUtils.getSize(orgFile.getAbsolutePath()) + "").getBytes(StandardCharsets.UTF_8));
            String oPath = FileUtils.parseMd5PathToPath(md5Path);
            java.io.File md5File = new java.io.File(savePath + "/" + oPath);
            if (!md5File.exists()) {// 判断目录是否存在
                md5File.mkdirs();
            }
            md5File = new java.io.File(savePath + "/" + oPath + "." + md5Size);
            if(md5File.exists()) return md5Path + ":" + md5Size;
            if(!orgFile.renameTo(md5File)){
                throw new BusinessException("File.MoveError");
            }
            return md5Path + ":" + md5Size;
        } finally {
            if(orgFile.exists()) orgFile.delete();
        }
    }

}

