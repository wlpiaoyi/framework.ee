package org.wlpiaoyi.framework.ee.utils.filter.encrypt;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.filter.FilterSupport;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public abstract class BaseEncryptFilter implements Filter, FilterSupport {

    public abstract SecurityOption getSecurityOption();

    public final void doCustomFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain){
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @SneakyThrows
    @Override
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        String uri = this.getRequestURI(servletRequest);
//        System.out.println("doFilter class:" + this.getClass().getName() + "uri:" + uri);
        ConfigModel configModel = this.getConfigModel();
        servletResponse.setCharacterEncoding(configModel.getCharsetName());
        SecurityOption securityOption = this.getSecurityOption();

        //没有需要的操作
        if(configModel.checkSecurityParse(uri)){
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Aes aes = securityOption.getAes(servletRequest);
        if(aes == null){
            log.error("EncryptFilter.doFilter unfunded aes object");
            return;
        }
        //响应处理 包装响应对象 res 并缓存响应数据
        RequestWrapper reqWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        ResponseWrapper resWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);

        byte[] reqBody = reqWrapper.getBody();
        if(!ValueUtils.isBlank(reqBody)){
            //解密请求报文
            reqBody = aes.decrypt(reqBody);
            reqWrapper.setBody(reqBody);
        }
        //执行业务逻辑 交给下一个过滤器或servlet处理
        chain.doFilter(reqWrapper, resWrapper);
        byte[] resData = resWrapper.getResponseData();
        if(!ValueUtils.isBlank(resData)){
            //加密响应报文
            resData = aes.encrypt(resData);
        }
        OutputStream out = servletResponse.getOutputStream();
        out.write(resData);
        out.flush();
        out.close();

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
