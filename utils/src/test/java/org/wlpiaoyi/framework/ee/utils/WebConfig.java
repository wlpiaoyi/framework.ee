package org.wlpiaoyi.framework.ee.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wlpiaoyi.framework.ee.utils.advice.handle.IdempotenceAdapter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 12:18
 * {@code @version:}:       1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private IdempotenceAdapter idempotenceAdapter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.idempotenceAdapter)
                .addPathPatterns("/**");
    }
}
