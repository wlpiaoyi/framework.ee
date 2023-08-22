package org.wlpiaoyi.framework.ee.utils.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/23 11:57
 * {@code @version:}:       1.0
 */
public interface FilterSupport {

    /**
     * 获取配置
     * @return
     */
    ConfigModel getConfigModel();

    /**
     * 获取请求的URI
     * @param servletRequest
     * @return
     */
    String getRequestURI(Object servletRequest);



}
