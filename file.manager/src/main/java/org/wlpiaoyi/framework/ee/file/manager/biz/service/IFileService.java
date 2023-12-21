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

    boolean upload(FileMenu fileMenu, MultipartFile file, HttpServletResponse response) throws IOException;

    void download(String token, String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response);
    void download(FileMenu fileMenu, Map funcMap, HttpServletRequest request, HttpServletResponse response);


    int deleteByFingerprints(List<String> deleteByFingerprints);

}
