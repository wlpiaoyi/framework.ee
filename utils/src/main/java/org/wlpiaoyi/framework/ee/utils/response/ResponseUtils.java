package org.wlpiaoyi.framework.ee.utils.response;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.SystemException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wlpia
 */
@Slf4j
public class ResponseUtils {

    private static final String CONTENT_TYPE_KEY = "content-type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json;charset=utf-8";
    public static void writeResponseJson(int code, @Nullable Object json,
                                         @NonNull HttpServletResponse response) throws IOException {
        response.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE_JSON);
        response.setContentType(CONTENT_TYPE_VALUE_JSON);
        ResponseUtils.writeResponseData(code, json, response);
    }

    public static void writeResponseData(int code, @Nullable Object data,
                                         @NonNull HttpServletResponse response) throws IOException {
        String repStr;
        if(data != null){
            if(data instanceof String){
                repStr = (String) data;
            }else if(data instanceof StringBuffer){
                repStr = ((StringBuffer) data).toString();
            }else if(data instanceof StringBuilder){
                repStr = ((StringBuilder) data).toString();
            }else{
                repStr = GsonBuilder.gsonDefault().toJson(data);
            }
        }else{
            repStr = "";
        }
        response.setStatus(code);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(repStr.getBytes(StandardCharsets.UTF_8).length));
        response.getWriter().write(repStr);
    }
}
