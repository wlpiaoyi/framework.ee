package org.wlpiaoyi.framework.ee.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.ee.utils.exception.BusinessException;
import org.wlpiaoyi.framework.ee.utils.status.File;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Coder;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;

public class FileUtils extends DataUtils{

    @Setter @Getter
    private static String contextPath;

    /** according to config file it should be relative path or absolute path **/
    public final static String BASE_PATH;
    /** file path must should be relative path **/
    public final static String FILE_PATH;
    /** temp path must should be relative path **/
    public final static String TEMP_PATH;

    static {
        String basePath = null;
        String filePath = null;
        String tempPath = null;
        try {
            Properties properties = ReaderUtils.loadProperties(
                    DataUtils.USER_DIR + "/config/application.properties");
            basePath = properties.getProperty("server.servlet.user.dir");
            filePath = properties.getProperty("server.servlet.user.dir.file");
            tempPath = properties.getProperty("server.servlet.user.dir.temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ValueUtils.isBlank(basePath) || basePath.equals("./")){
            basePath = DataUtils.USER_DIR;
        }
        if(ValueUtils.isBlank(filePath)){
            filePath = "/file";
        }
        if(ValueUtils.isBlank(tempPath)){
            filePath = "/file/temp";
        }
        BASE_PATH = basePath;
        FILE_PATH = BASE_PATH + filePath;
        TEMP_PATH = BASE_PATH + tempPath;
    }

    @Setter @Getter
    private static int maxFileSize = 1024 * 1024 * 50;

    /**
     * Store file base on file fingerprints
     * @return
     */
    public static String moveFileMD5(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BusinessException(File.EmptyError);
        }
        if(file.isEmpty()){
            throw new BusinessException(File.EmptyError);
        }
        String fileName = StringUtils.getUUID32();
        FileUtils.makeDir(FileUtils.TEMP_PATH);
        java.io.File dest = new java.io.File(FileUtils.TEMP_PATH + "/" + fileName);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException(File.CommonError.getIndex(), e.getMessage());
        }
        String md5Path = FileUtils.moveFileMD5(dest);
        return md5Path;

    }


    /**
     * Store file base on file fingerprints
     * @param orgFile
     * @return
     */
    @SneakyThrows
    public static String moveFileMD5(java.io.File orgFile) throws Exception {
        try{
            String fileName = new BigInteger(1, Coder.encryptMD5(new FileInputStream(orgFile))).toString(16);
            String oPath = "";
            for (int i = 0; i < fileName.length(); i+=2) {
                String fn = fileName.substring(i, i+2);
                oPath +=  fn + "/";
            }
            java.io.File md5File = new java.io.File(FileUtils.FILE_PATH + "/" + oPath);
            //判断目录是否存在
            if (!md5File.exists()) {
                md5File.mkdirs();
            }
            oPath += "data";
            md5File = new java.io.File(FileUtils.FILE_PATH + "/" + oPath);
            if(md5File.exists()) {
                return oPath;
            }
            if(!orgFile.renameTo(md5File)){
                throw new BusinessException(File.MoveError);
            }
            return oPath;
        } finally {
            if(orgFile.exists()) {
                orgFile.delete();
            }
        }
    }

}
