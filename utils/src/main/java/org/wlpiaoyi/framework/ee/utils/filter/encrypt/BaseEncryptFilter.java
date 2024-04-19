package org.wlpiaoyi.framework.ee.utils.filter.encrypt;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * 加密Filter
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    加密
 * {@code @date:}           2023/2/18 10:05
 * {@code @version:}:       1.0
 */
@Slf4j
@Order(Integer.MIN_VALUE + 1)
//@Component
public abstract class BaseEncryptFilter extends BaseEncrypt implements Filter {


    @SneakyThrows
    @Override
    protected void decryptRequestBody(Object request, Object response, Aes aes,  Object obj) {
        RequestWrapper reqWrapper = (RequestWrapper) request;
        byte[] reqBody = reqWrapper.getBody();
        if(!ValueUtils.isBlank(reqBody)){
            //解密请求报文
            reqBody = aes.decrypt(reqBody);
        }
        reqWrapper.setBody(reqBody);

    }

    @SneakyThrows
    @Override
    protected void doingFilter(Object request, Object response,  Object obj) {
        ServletRequest servletRequest = (ServletRequest) request;
        ServletResponse servletResponse = (ServletResponse) response;
        FilterChain chain = (FilterChain) obj;
        chain.doFilter(servletRequest, servletResponse);
    }

    @SneakyThrows
    @Override
    protected void encryptResponseBody(Object request, Object response, Aes aes,  Object obj) {
        ResponseWrapper respWrapper = (ResponseWrapper) response;
        byte[] respData = respWrapper.getResponseData();
        if(!ValueUtils.isBlank(respData)){
            //加密响应报文
            respData = aes.encrypt(respData);
        }
        ServletResponse servletResponse = (ServletResponse) obj;
        OutputStream out = servletResponse.getOutputStream();
        out.write(respData);
        out.flush();
        out.close();
    }

    @SneakyThrows
    @Override
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {

        servletResponse.setCharacterEncoding(this.getConfigModel().getCharsetName());
        //没有需要的操作
        if(!super.checkSecurityParse(servletRequest, servletResponse)){
            this.doingFilter(servletRequest, servletResponse, chain);
            return;
        }

        SecurityOption securityOption = this.getSecurityOption();
        Aes aes = securityOption.getAes(servletRequest);
        if(aes == null){
            log.error("EncryptFilter.doFilter unfunded aes object");
            return;
        }

        //响应处理 包装响应对象 res 并缓存响应数据
        RequestWrapper reqWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        ResponseWrapper respWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
        this.handelFilter(reqWrapper, respWrapper, aes, null, chain, servletResponse);

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
