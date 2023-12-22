package org.wlpiaoyi.framework.ee.file.manager.biz.service;

import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;

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
    /**
     * 根据文件指纹获取路径
     * @param fingerprintHex
     * @return
     */
    String getFilePathByFingerprintHex(String fingerprintHex);
    String getFilePathByFingerprint(String fingerprint);

    /**
     *
     * @param fileMenu
     * @param fingerprint
     */
    void synFileMenuByFingerprint(FileMenu fileMenu, String fingerprint);

    /**
     * 上传文件
     * @param fileMenu
     * @param file
     * @param response
     * @return
     * @throws IOException
     */
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
