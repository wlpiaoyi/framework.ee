package org.wlpiaoyi.framework.ee.utils.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.ee.utils.response.ResponseUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
//@RestControllerAdvice
public abstract class BaseGlobalExceptionHandler {

    public abstract R customErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception exception);

    /**
     * 业务异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws BusinessException
     */
    @ExceptionHandler(value = BusinessException.class)
    public void defaultBusinessHandler(HttpServletRequest req, HttpServletResponse resp, BusinessException exception) throws IOException {
        String message = exception.getMessage();
        R r = R.data(exception.getCode(), null, message);
        errorHandler(resp, 200, r);
    }

    /**
     * 业务异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws CatchException
     */
    @ExceptionHandler(value = CatchException.class)
    public void defaultCatchHandler(HttpServletRequest req, HttpServletResponse resp, CatchException exception) throws IOException {
        String message = exception.getMessage();
        R r = R.data(exception.getCode(), null, message);
        errorHandler(resp, 200, r);
    }


    /**
     * 404异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws NoHandlerFoundException
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public void defaultNoHandlerFoundHandler(HttpServletRequest req, HttpServletResponse resp, NoHandlerFoundException exception) throws IOException {
        int code = 404;
        String message = "没有找到接口";
        R r = R.data(code, null, message);
        errorHandler(resp, code, r);
    }

    /**
     * 405异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws HttpRequestMethodNotSupportedException
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public void defaultHttpRequestMethodNotSupportedHandler(HttpServletRequest req, HttpServletResponse resp, HttpRequestMethodNotSupportedException exception) throws IOException {
        int code = 405;
        String message = "不支持的方法:" + exception.getMessage();
        R r = R.data(code, null, message);
        errorHandler(resp, code, r);
    }

    /**
     * 412 参数缺失
     * @param req
     * @param resp
     * @param exception
     * @throws MissingServletRequestParameterException
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public void defaultMissingServletRequestParameterHandler(HttpServletRequest req, HttpServletResponse resp, MissingServletRequestParameterException exception) throws IOException {
        int code = 412;
        String message = "参数错误:" + exception.getMessage();
        R r = R.data(code, null, message);
        errorHandler(resp, code, r);
    }

    /**
     * 413 参数错误
     * @param req
     * @param resp
     * @param exception
     * @throws MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public void defaultMethodArgumentTypeMismatchHandler(HttpServletRequest req, HttpServletResponse resp, MethodArgumentTypeMismatchException exception) throws IOException {
        int code = 413;
        String message = "参数错误:" + exception.getMessage();
        R r = R.data(code, null, message);
        errorHandler(resp, code, r);
    }



    /**
     * 400 参数序列化异常
     * @param req
     * @param resp
     * @param exception
     * @throws HttpMessageNotReadableException
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public void defaultHttpMessageNotReadableHandler(HttpServletRequest req, HttpServletResponse resp, HttpMessageNotReadableException exception) throws IOException {
        int code = 400;
        String message = "参数序列化异常:" + exception.getMessage();
        R r = R.data(code, null, message);
        errorHandler(resp, code, r);
    }

    /**
     * 系统异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception exception) throws IOException {
        R r = R.error(exception);
        errorHandler(resp, 500, r);
    }


    /**
     * 异常处理
     * @param resp
     * @param r
     * @param httpCode
     * @throws IOException
     */
    private static void errorHandler(HttpServletResponse resp,
                                     int httpCode, R r) throws IOException {
        try{
            ResponseUtils.writeResponseJson(httpCode, r,  resp);
        } catch (Exception e){
            int code = 500;
            String message = "Unknown error";
            r = R.data(code, null, message);
            ResponseUtils.writeResponseJson(httpCode,r, resp);
        }
    }
}

