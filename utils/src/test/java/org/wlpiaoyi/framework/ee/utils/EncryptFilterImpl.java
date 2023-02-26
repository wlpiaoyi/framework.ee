package org.wlpiaoyi.framework.ee.utils;

import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.filter.encrypt.BaseEncryptFilter;
import org.wlpiaoyi.framework.ee.utils.filter.encrypt.SecurityOption;

import javax.servlet.ServletRequest;

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
    public String getRequestURI(ServletRequest servletRequest) {
        return ((RequestFacade) servletRequest).getRequestURI();
    }

    private SecurityOption securityOption = new SecurityOptionImpl();

    @Override
    public SecurityOption getSecurityOption() {
        return this.securityOption;
    }
}
