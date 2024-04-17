package org.wlpiaoyi.framework.ee.fileScan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.wlpiaoyi.framework.ee.fileScan.handler.HandlerInterceptor;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-04-17 18:27:39</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */

@Configuration
public class WebMvcConfigurationSupport extends org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport {
    @Override
    public void addInterceptors(InterceptorRegistry interceptor) {
        interceptor.addInterceptor(new HandlerInterceptor()).addPathPatterns("/**");
    }
}
