package org.wlpiaoyi.framework.ee.utils;

import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.filter.encrypt.BaseEncryptFilter;
import org.wlpiaoyi.framework.ee.utils.filter.encrypt.SecurityOption;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/23 22:14
 * {@code @version:}:       1.0
 */
@Component
public class EncryptFilterImpl extends BaseEncryptFilter {
    @Autowired
    private ConfigModel configModel;

    public ConfigModel getConfigModel() {
        return configModel;
    }

    @Override
    public String getRequestURI(Object servletRequest) {
        return ((RequestFacade) servletRequest).getRequestURI();
    }

    private SecurityOption securityOption = new SecurityOptionImpl();


    public SecurityOption getSecurityOption() {
        return this.securityOption;
    }

}
