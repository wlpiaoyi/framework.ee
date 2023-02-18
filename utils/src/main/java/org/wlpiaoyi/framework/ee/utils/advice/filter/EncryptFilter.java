package org.wlpiaoyi.framework.ee.utils.advice.filter;

import lombok.SneakyThrows;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.advice.reqresp.SecurityOption;
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
@Component
public class EncryptFilter implements Filter {

    @Autowired
    private SecurityOption securityOption;
    @Autowired
    private ConfigModel configModel;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @SneakyThrows
    @Override
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        servletResponse.setCharacterEncoding(this.configModel.getCharsetName());
        if(this.configModel.checkSecurityParse(((RequestFacade) servletRequest).getRequestURI())){
            if(this.securityOption.isHandleResponse(servletRequest, servletResponse, chain)){
                this.securityOption.handleResponse(servletRequest, servletResponse, chain);
            }else{
                Aes aes = this.securityOption.getAes(servletRequest);
                if(aes == null){
                    return;
                }

                //响应处理 包装响应对象 res 并缓存响应数据
                RequestWrapper reqWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
                ResponseWrapper resWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
                if(!ValueUtils.isBlank(reqWrapper.getBody())){
                    byte[] body = reqWrapper.getBody();
                    //解密请求报文
                    reqWrapper.setBody(aes.decrypt(body));
                }else{
                    reqWrapper.setBody(new byte[0]);
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
        }else{
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
