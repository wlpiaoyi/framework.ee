package org.wlpiaoyi.framework.ee.fileScan.service;

import org.wlpiaoyi.framework.ee.fileScan.domain.model.FileInfo;
import org.wlpiaoyi.framework.utils.exception.SystemException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>    </p>
 * <p><b>{@code @date:}</b>           2024/3/11 22:14</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */

public interface IFileService {


    /**
     * <p><b>{@code @description:}</b>
     * 扫描文件
     * </p>
     *
     * <p><b>@param</b> <b>baseFile</b>
     * {@link File}
     * </p>
     *
     * <p><b>@param</b> <b>deepCount</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/11 23:01</p>
     * <p><b>{@code @return:}</b>{@link FileInfo}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    FileInfo scanFileInfo(File baseFile, int deepCount);

    /**
     * <p><b>{@code @description:}</b>
     * 下载文件
     * </p>
     *
     * <p><b>@param</b> <b>fingerprint</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * </p>
     *
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/11 23:34</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    void download(String fingerprint, Map funcMap, HttpServletRequest request, HttpServletResponse response);

    /**
     * <p><b>{@code @description:}</b>
     * 下载文件
     * </p>
     *
     * <p><b>@param</b> <b>fileIs</b>
     * {@link InputStream}
     * </p>
     *
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * </p>
     *
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/11 22:17</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    void download(File file, Map funcMap, HttpServletRequest request, HttpServletResponse response) throws SystemException;


    void resHtml(FileInfo fileInfo, HttpServletResponse response);


    String getFingerprint(String base64Md5FingerprintStr);

}
