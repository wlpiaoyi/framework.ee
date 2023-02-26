package org.wlpiaoyi.framework.ee.utils.filter.encrypt;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 20:58
 * {@code @version:}:       1.0
 */
public interface SecurityOption {

    /**
     * 获取非对称加密
     * @param inputMessage
     * @return
     */
    Rsa getRsa(HttpInputMessage inputMessage);

    /**
     * 获取对称加密
     * @param objKey
     * @return
     */
    Aes getAes(Object objKey);

    /**
     *
     * @param inputMessage
     * @param methodParameter
     * @return
     */
    boolean isHandleRequest(HttpInputMessage inputMessage,
                            MethodParameter methodParameter);

    /**
     *
     * @param inputMessage
     * @param methodParameter
     * @return
     */
    byte[] handleRequest(HttpInputMessage inputMessage,
                              MethodParameter methodParameter);


    /**
     * 是否处理Response数据
     * @param servletRequest
     * @param servletResponse
     * @param chain
     * @return
     */
    boolean isHandleResponse(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain);

    /**
     * 处理Response数据
     * @param servletRequest
     * @param servletResponse
     * @param chain
     * @return
     */
    void handleResponse(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain);

}
