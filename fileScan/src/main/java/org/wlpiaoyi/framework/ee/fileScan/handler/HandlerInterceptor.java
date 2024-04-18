package org.wlpiaoyi.framework.ee.fileScan.handler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.ee.utils.response.ResponseUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
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
@Component
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
            String uri = request.getRequestURI();
            if(!uri.equals("/")){
                return false;
            }
            response.setHeader("content-type", "text/html; charset=utf-8");
            response.setContentType("text/html; charset=utf-8");
            InputStream md5JsIo = HandlerInterceptor.class.getClassLoader().getResourceAsStream("md5.js");
            assert md5JsIo != null;
            String md5JsContent = ReaderUtils.loadString(md5JsIo, StandardCharsets.UTF_8);
            InputStream loginHtmlIo = HandlerInterceptor.class.getClassLoader().getResourceAsStream("login.html");
            assert loginHtmlIo != null;
            String loginHtmlContent = ReaderUtils.loadString(loginHtmlIo, StandardCharsets.UTF_8);
            loginHtmlContent = loginHtmlContent.replace("${md5.js}", md5JsContent);
            ResponseUtils.writeResponseData(200, loginHtmlContent, response);
            return false;
        }
        return true;
    }

    @Value("${fileScan.auth.userName:}")
    private String userName;
    @Value("${fileScan.auth.password:}")
    private String password;

    @SneakyThrows
    private boolean authCheckForDownload(HttpServletRequest request){
        String uri = request.getRequestURI();
        if(!uri.startsWith("/file/download/")){
            return false;
        }
        String[] uriParts = uri.split("/");
        if(uriParts.length < 5){
            return false;
        }
        String authKeyBase64Str = uriParts[4];
        byte[] authKeyBytes1 = DataUtils.base64Decode(authKeyBase64Str.getBytes());
        byte[] authKeyBytes2 = DataUtils.MD(
                (DataUtils.MD(this.userName, DataUtils.KEY_MD5) + DataUtils.MD(this.password, DataUtils.KEY_MD5)).getBytes()
                , DataUtils.KEY_MD5);
        if(authKeyBytes1.length != authKeyBytes2.length){
            return false;
        }
        for (int i = 0; i < authKeyBytes1.length; i ++){
            byte b1 = authKeyBytes1[i];
            byte b2 = authKeyBytes2[i];
            if(b1 != b2){
                return false;
            }
        }
        return true;
    }
    @SneakyThrows
    private boolean authCheck(HttpServletRequest request){
        if(this.authCheckForDownload(request)){
            return true;
        }
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
        if(!userName.equals(DataUtils.MD(this.userName, DataUtils.KEY_MD5)) | !password.equals(DataUtils.MD(this.password, DataUtils.KEY_MD5))){
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
