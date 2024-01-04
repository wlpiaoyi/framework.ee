package org.wlpiaoyi.framework.ee.file.manager.config.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
@ControllerAdvice
public class GlobalExceptionHandler extends org.wlpiaoyi.framework.ee.utils.handler.GlobalExceptionHandler{

    /**
     * 220 解码错误
     * @param req
     * @param resp
     * @param exception
     * @throws MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public void illegalArgumentHandler(HttpServletRequest req, HttpServletResponse resp, IllegalArgumentException exception) throws IOException {
        int code = 220;
        String message = "解码错误:" + exception.getMessage();
        R r = R.data(code, null, message);
        doResponse(code, r, req, resp, exception);
    }
}
