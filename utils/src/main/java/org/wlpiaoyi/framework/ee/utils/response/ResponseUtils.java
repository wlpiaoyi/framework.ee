package org.wlpiaoyi.framework.ee.utils.response;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author wlpia
 */
public class ResponseUtils {

    private static final String CONTENT_TYPE_VALUE_JSON = "application/json";
    private static final String CONTENT_CHARSET_ENCODE = "utf-8";
    public static void writeResponseJson(int code,
                                         @Nullable Object json,
                                         @NonNull HttpServletResponse response) throws IOException {
//        response.setHeader("content-type", "application/json;charset=" + CONTENT_CHARSET_ENCODE);
        response.setStatus(code);
        response.setContentType(CONTENT_TYPE_VALUE_JSON);
        String repStr;
        if(json != null){
            if(json instanceof String){
                repStr = (String) json;
            }else if(json instanceof StringBuffer){
                repStr = ((StringBuffer) json).toString();
            }else if(json instanceof StringBuilder){
                repStr = ((StringBuilder) json).toString();
            }else if(json instanceof byte[]){
                repStr = new String((byte[]) json, CONTENT_CHARSET_ENCODE);
            }else{
                repStr = GsonBuilder.gsonDefault().toJson(json);
            }
        }else{
            repStr = "";
        }
        response.getWriter().write(repStr);
    }
}
