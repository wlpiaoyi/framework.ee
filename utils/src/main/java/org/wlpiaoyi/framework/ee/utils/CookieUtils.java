package org.wlpiaoyi.framework.ee.utils;

import lombok.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author wlpiaoyi
 * @Date 2022/2/21 9:42 AM
 * @Version 1.0
 */
public class CookieUtils {


    public static Cookie getCookie(@NonNull String name){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return CookieUtils.getCookie(request, name);
    }

    public static Cookie getCookie(@NonNull HttpServletRequest request, @NonNull String name){
        Cookie[] cookies = request.getCookies();
        if(ValueUtils.isBlank(cookies)) return null;
        for (Cookie cookie : cookies){
            if(ValueUtils.isBlank(cookie.getName())) continue;
            if(cookie.getName().equals(name)) return cookie;
        }
        return null;
    }

    public static String getCookieValue(@NonNull String name){
        Cookie cookie = CookieUtils.getCookie(name);
        if(cookie == null) return null;
        return cookie.getValue();
    }

    public static String getCookieValue(@NonNull HttpServletRequest request, @NonNull String name){
        Cookie cookie = CookieUtils.getCookie(request, name);
        if(cookie == null) return null;
        return cookie.getValue();
    }

    public static void addCookie(@NonNull HttpServletResponse response, @NonNull String name, @NonNull String value){
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }

    public static void addCookie(@NonNull String name, @NonNull String value){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }
}
