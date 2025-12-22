package org.wlpiaoyi.framework.ee.resource.config.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;

/**
 * 拦截器
 */
@Slf4j
public class Interceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String url = request.getRequestURI();
        if(url.startsWith("/file/")){

        }else{
//            String adminSign = request.getHeader("adminSign");
//            if(!FileConfig.getADMIN_SIGN().equals(adminSign)){
//                return false;
//            }
        }
        return true;//如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        //如果设置为true时，请求将会继续执行后面的操作
    }

}
