package org.wlpiaoyi.framework.ee.file.manager.biz.service;

import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 16:00
 * {@code @version:}:       1.0
 */
public interface IFileService {

<<<<<<< HEAD
    /**
     * 上传文件
     * @param fileMenu
     * @param file
     * @param response
     * @return
     * @throws IOException
     */
=======
>>>>>>> d2f1f3dbbe59bc761fe5a13e02585178edcf2ba6
    boolean upload(FileMenu fileMenu, MultipartFile file, HttpServletResponse response) throws IOException;

    /**
     * 下载文件
     * @param token
     * @param fingerprint
     * @param funcMap
     * @param request
     * @param response
     */
    void download(String token, String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response);
    void download(FileMenu fileMenu, Map funcMap, HttpServletRequest request, HttpServletResponse response);


    /**
     * 清理文件
     * @return
     */
    List<String> cleanFile();

}
