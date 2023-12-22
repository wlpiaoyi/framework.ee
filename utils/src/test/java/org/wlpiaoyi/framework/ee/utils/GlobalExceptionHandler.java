package org.wlpiaoyi.framework.ee.utils;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wlpiaoyi.framework.ee.utils.response.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/23 22:13
 * {@code @version:}:       1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends org.wlpiaoyi.framework.ee.utils.handler.GlobalExceptionHandler {
    @Override
    public R customErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception exception) {
        return null;
    }
}
