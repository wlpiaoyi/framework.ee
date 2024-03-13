package org.wlpiaoyi.framework.ee.resource.biz.service;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  文件下载</p>
 * <p><b>{@code @date:}</b>         2023/9/16 16:00</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public interface IFileService {

    /**
     * <p><b>{@code @description:}</b>
     * 保存文件
     * </p>
     *
     * <p><b>@param</b> <b>fileIo</b>
     * {@link Object}
     * 文件IO
     * </p>
     *
     * <p><b>@param</b> <b>entity</b>
     * {@link FileInfo}
     * 文件实体信息
     * </p>
     *
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * 拓展字段
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/13 13:24</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    String save(Object fileIo, FileInfo entity, Map funcMap);


    /**
     * <p><b>{@code @description:}</b>
     * 根据token下载文件
     * </p>
     *
     * <p><b>@param</b> <b>token</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * </p>
     *
     * <p><b>@param</b> <b>request</b>
     * {@link HttpServletRequest}
     * </p>
     *
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/12/30 15:51</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void download(String token, Map funcMap, HttpServletRequest request, HttpServletResponse response);

    /**
     * <p><b>{@code @description:}</b>
     * 根据文件信息对象下载文件
     * </p>
     *
     * <p><b>@param</b> <b>entity</b>
     * {@link FileInfo}
     * </p>
     *
     * <p><b>@param</b> <b>funcMap</b>
     * {@link Map}
     * </p>
     *
     * <p><b>@param</b> <b>request</b>
     * {@link HttpServletRequest}
     * </p>
     *
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/12/30 15:51</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void download(FileInfo entity, Map funcMap, HttpServletRequest request, HttpServletResponse response);


    /**
     * <p><b>{@code @description:}</b>
     * 清理文件
     * </p>
     *
     * <p><b>@param</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/12/30 15:51</p>
     * <p><b>{@code @return:}</b>{@link List<String>}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    List<String> cleanFile();

}
