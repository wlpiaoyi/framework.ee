package org.wlpiaoyi.framework.ee.file.manager.biz.service;

import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 16:00
 * {@code @version:}:       1.0
 */
public interface IFileService {

    Aes getAes();

    boolean upload(FileMenu fileMenu, MultipartFile file) throws IOException;

    void download(FileMenu fileMenu, HttpServletResponse response) throws IOException;

}
