package org.wlpiaoyi.framework.ee.utils.response;

import lombok.Getter;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 22:23
 * {@code @version:}:       1.0
 */
@Getter
public class R<T> {

    private int code;

    private T data;

    private String message;
    public static <T> R<T> data(int code){
        R<T> r = new R<>();
        r.code = code;
        return r;
    }
    public static <T> R<T> data(int code, String message){
        R<T> r = new R<>();
        r.message = message;
        r.code = code;
        return r;
    }


    public static <T> R<T> data(int code, T data, String message){
        R<T> r = new R<>();
        r.message = message;
        r.data = data;
        r.code = code;
        return r;
    }


    public static <T> R<T>  success(T data){
        return data(200, data, "SUCCESS");
    }
    public static <T> R<T>  success(T data, String message){
        return data(200, message);
    }

    public static <T> R<T>  fail(String message){
        return data(501, message);
    }
    public static <T> R<T>  fail(T data, String message){
        return data(501, data, message);
    }
    public static <T> R<T> error(BusinessException e){
        R<T> r = new R<>();
        r.code = e.getCode();
        r.message = e.getMessage();
        return r;
    }
    public static <T> R<T> error(CatchException e){
        R<T> r = new R<>();
        r.code = e.getCode();
        r.message = e.getMessage();
        return r;
    }
    public static <T> R<T> error(RuntimeException e){
        R<T> r = new R<>();
        r.code = 501;
        r.message = e.getMessage();
        return r;
    }
    public static <T> R<T> error(Exception e){
        R<T> r = new R<>();
        r.code = 502;
        r.message = e.getMessage();
        return r;
    }
}
