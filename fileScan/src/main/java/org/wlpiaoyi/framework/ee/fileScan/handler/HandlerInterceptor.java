package org.wlpiaoyi.framework.ee.fileScan.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.response.ResponseUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-04-17 16:09:53</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
@Slf4j
public class HandlerInterceptor implements org.springframework.web.servlet.HandlerInterceptor {

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>request</b>
     * {@link HttpServletRequest}
     * </p>
     *
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>@param</b> <b>handler</b>
     * {@link Object}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/4/17 16:16</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(!this.authCheck(request)){
            response.setHeader("content-type", "text/html; charset=utf-8");
            response.setContentType("text/html; charset=utf-8");
            InputStream authIo = HandlerInterceptor.class.getClassLoader().getResourceAsStream("login.html");
            assert authIo != null;
            String mdContent = ReaderUtils.loadString(authIo, StandardCharsets.UTF_8);
            ResponseUtils.writeResponseData(200, mdContent, response);
            return false;
        }
        return true;
    }

    public boolean authCheck(HttpServletRequest request){
        if(ValueUtils.isBlank(request.getCookies())){
            return false;
        }
        String userName = "";
        String password = "";
        for (Cookie cookie : request.getCookies()){
            if(ValueUtils.isNotBlank(userName) && ValueUtils.isNotBlank(password)){
                break;
            }
            if(cookie.getName().equals("auth_username")){
                userName = cookie.getValue();
                continue;
            }
            if(cookie.getName().equals("auth_password")){
                password = cookie.getValue();
            }
        }

        if(ValueUtils.isBlank(userName) || ValueUtils.isBlank(password)){
            return false;
        }
        if(!userName.equals("wlpiaoyi") | !password.equals("0")){
            return false;
        }
        return true;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>request</b>
     * {@link HttpServletRequest}
     * </p>
     *
     * <p><b>@param</b> <b>response</b>
     * {@link HttpServletResponse}
     * </p>
     *
     * <p><b>@param</b> <b>handler</b>
     * {@link Object}
     * </p>
     *
     * <p><b>@param</b> <b>ex</b>
     * {@link Exception}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/4/17 16:16</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) throws Exception {
        System.out.println();
    }

}
