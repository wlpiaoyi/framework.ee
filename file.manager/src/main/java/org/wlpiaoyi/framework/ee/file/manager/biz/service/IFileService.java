package org.wlpiaoyi.framework.ee.file.manager.biz.service;

import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
     * 保存文件
     * @param fileIo 文件IO
     * @param fileInfo 文件实体信息
     * @return: java.lang.String file-sign
     * @author: wlpia
     * @date: 2023/12/30 15:51
     */
    String save(InputStream fileIo, FileInfo fileInfo);

    /**
     * 下载文件
     * @param token
     * @param fingerprint
     * @param funcMap
     * @param request
     * @param response
     */
    void download(String token, String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response);
    void download(FileInfo fileInfo, Map funcMap, HttpServletRequest request, HttpServletResponse response);


    /**
     * 清理文件
     * @return
     */
    List<String> cleanFile();

}
