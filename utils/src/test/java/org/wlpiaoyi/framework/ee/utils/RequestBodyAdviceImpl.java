package org.wlpiaoyi.framework.ee.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.wlpiaoyi.framework.ee.utils.advice.reqresp.RequestBodyAdvice;
import org.wlpiaoyi.framework.ee.utils.advice.reqresp.SecurityOption;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 15:20
 * {@code @version:}:       1.0
 */
@Slf4j
@ControllerAdvice
public class RequestBodyAdviceImpl extends RequestBodyAdvice {

    @Autowired
    private SecurityOption securityOption;

    @Autowired
    private ConfigModel configModel;

    @Override
    public SecurityOption getSecurityOption() {
        return this.securityOption;
    }

    @Override
    public ConfigModel getConfigModel() {
        return this.configModel;
    }
}
