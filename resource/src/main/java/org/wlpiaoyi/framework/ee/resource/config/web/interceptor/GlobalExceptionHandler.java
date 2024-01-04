package org.wlpiaoyi.framework.ee.resource.config.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.wlpiaoyi.framework.ee.utils.response.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 21:48
 * {@code @version:}:       1.0
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
}
