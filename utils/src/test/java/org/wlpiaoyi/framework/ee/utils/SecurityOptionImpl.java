package org.wlpiaoyi.framework.ee.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.filter.encrypt.SecurityOption;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 22:18
 * {@code @version:}:       1.0
 */
@Slf4j
@Component
public class SecurityOptionImpl implements SecurityOption {

    @Autowired
    private ConfigModel configModel;

    @Override
    public Rsa getRsa(HttpInputMessage inputMessage) {
        return null;
    }

    @SneakyThrows
    @Override
    public Aes getAes(Object objKey) {
        return Aes.create().setKey("abcd567890ABCDEF1234567890ABCDEF").setIV("abcd567890123456").load();
    }

    @Override
    public boolean isHandleRequest(HttpInputMessage inputMessage, MethodParameter methodParameter) {
        return false;
    }

    @Override
    public byte[] handleRequest(HttpInputMessage inputMessage, MethodParameter methodParameter) {
        return null;
    }

    @Override
    public boolean isHandleResponse(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        return false;
    }

    @Override
    public void handleResponse(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {

    }


}
