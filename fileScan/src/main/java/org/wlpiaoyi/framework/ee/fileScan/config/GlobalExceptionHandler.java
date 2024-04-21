package org.wlpiaoyi.framework.ee.fileScan.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.wlpiaoyi.framework.ee.utils.response.R;

import java.io.IOException;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>    </p>
 * <p><b>{@code @date:}</b>           2024/4/21 10:16</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */

@Slf4j
@Order(1)
@ControllerAdvice
public class GlobalExceptionHandler extends org.wlpiaoyi.framework.ee.utils.handler.GlobalExceptionHandler{


    @Override
    protected void doResponse(int code, R r, HttpServletRequest req, HttpServletResponse response, Exception exception) throws IOException {
        super.doResponse(code, r, req, response, exception);
    }


    /**
     * 值范围异常异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws IllegalArgumentException
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public void illegalArgumentHandler(HttpServletRequest req, HttpServletResponse resp, IllegalArgumentException exception) throws IOException {
        String message = exception.getMessage();
        R r = R.data(413, null, message);
        doResponse(413, r, req, resp, exception);
    }


    protected Object[] expandErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception exception){
        return null;
    }
}