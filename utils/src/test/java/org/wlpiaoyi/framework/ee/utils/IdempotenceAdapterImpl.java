package org.wlpiaoyi.framework.ee.utils;

import lombok.SneakyThrows;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.filter.idempotence.BaseIdempotenceFilter;
import org.wlpiaoyi.framework.ee.utils.filter.idempotence.IdempotenceMoon;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Coder;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 17:49
 * {@code @version:}:       1.0
 */
@Component
public class IdempotenceAdapterImpl extends BaseIdempotenceFilter {


    @Autowired
    private ConfigModel configModel;

    public ConfigModel getConfigModel() {
        return configModel;
    }

    @Override
    public String getRequestURI(ServletRequest servletRequest) {
        if(servletRequest instanceof RequestFacade){
            return ((RequestFacade) servletRequest).getRequestURI();
        }else if(servletRequest instanceof HttpServletRequestWrapper){
            return ((HttpServletRequestWrapper) servletRequest).getRequestURI();
        }
        return null;
    }

    @Override
    public void doCustomFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        System.out.println();
    }

    public class IdempotenceMoonImpl implements IdempotenceMoon {
        @SneakyThrows
        @Override
        public String getKey(ServletRequest servletRequest) {

            if(servletRequest instanceof RequestFacade){
                return new BigInteger(Coder.encryptMD5(((RequestFacade) servletRequest).getRequestURI().getBytes(StandardCharsets.UTF_8))).toString(16)
                        + new BigInteger(Coder.encryptMD5(((RequestFacade) servletRequest).getHeader("token").getBytes(StandardCharsets.UTF_8))).toString(16);
            }else if(servletRequest instanceof HttpServletRequestWrapper){
                return new BigInteger(Coder.encryptMD5(((HttpServletRequestWrapper) servletRequest).getRequestURI().getBytes(StandardCharsets.UTF_8))).toString(16)
                        + new BigInteger(Coder.encryptMD5(((HttpServletRequestWrapper) servletRequest).getHeader("token").getBytes(StandardCharsets.UTF_8))).toString(16);
            }
            return null;
        }
    }

    private IdempotenceMoon idempotenceMoon = new IdempotenceMoonImpl();

    @Override
    public IdempotenceMoon getIdempotenceMoon() {
        return this.idempotenceMoon;
    }

}
