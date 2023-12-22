package org.wlpiaoyi.framework.ee.utils.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.ee.utils.response.ResponseUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;
import org.wlpiaoyi.framework.utils.exception.SystemException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {

    protected void doResponse(int code, R r,
                              HttpServletRequest req,
                              HttpServletResponse response,
                              Exception exception) throws IOException {

        log.warn("Response warn api:(" + req.getRequestURI() +
                ") code:(" + code +  ")", exception);
        ResponseUtils.writeResponseJson(code, r, response);
    }


    /**
     * 业务异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws BusinessException
     */
    @ExceptionHandler(value = BusinessException.class)
    public void businessHandler(HttpServletRequest req, HttpServletResponse resp, BusinessException exception) throws IOException {
        String message = exception.getMessage();
        R r = R.data(exception.getCode(), null, message);
        doResponse(200, r, req, resp, exception);
    }

    /**
     * 系统异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws CatchException
     */
    @ExceptionHandler(value = SystemException.class)
    public void systemHandler(HttpServletRequest req, HttpServletResponse resp, SystemException exception) throws IOException {
        String message = exception.getMessage();
        R r = R.data(exception.getCode(), null, message);
        doResponse(exception.getCode(), r, req, resp, exception);
    }

    /**
     * 捕获异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws CatchException
     */
    @ExceptionHandler(value = CatchException.class)
    public void catchHandler(HttpServletRequest req, HttpServletResponse resp, CatchException exception) throws IOException {
        String message = exception.getMessage();
        R r = R.data(exception.getCode(), null, message);
        doResponse(exception.getCode(), r, req, resp, exception);
    }


    /**
     * 404异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws NoHandlerFoundException
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public void noHandlerFoundHandler(HttpServletRequest req, HttpServletResponse resp, NoHandlerFoundException exception) throws IOException {
        int code = 404;
        String message = "没有找到接口";
        R r = R.data(code, null, message);
        doResponse(code, r, req, resp, exception);
    }

    /**
     * 405异常处理
     * @param req
     * @param resp
     * @param exception
     * @throws HttpRequestMethodNotSupportedException
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public void httpRequestMethodNotSupportedHandler(HttpServletRequest req, HttpServletResponse resp, HttpRequestMethodNotSupportedException exception) throws IOException {
        int code = 405;
        String message = "不支持的方法:" + exception.getMessage();
        R r = R.data(code, null, message);
        doResponse(code, r, req, resp, exception);
    }

    /**
     * 412 参数缺失
     * @param req
     * @param resp
     * @param exception
     * @throws MissingServletRequestParameterException
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public void missingServletRequestParameterHandler(HttpServletRequest req, HttpServletResponse resp, MissingServletRequestParameterException exception) throws IOException {
        int code = 412;
        String message = "参数错误:" + exception.getMessage();
        R r = R.data(code, null, message);
        doResponse(code, r, req, resp, exception);
    }

    /**
     * 413 参数错误
     * @param req
     * @param resp
     * @param exception
     * @throws MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public void methodArgumentTypeMismatchHandler(HttpServletRequest req, HttpServletResponse resp, MethodArgumentTypeMismatchException exception) throws IOException {
        int code = 413;
        String message = "参数错误:" + exception.getMessage();
        R r = R.data(code, null, message);
        doResponse(code, r, req, resp, exception);
    }



    /**
     * 400 参数序列化异常
     * @param req
     * @param resp
     * @param exception
     * @throws HttpMessageNotReadableException
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public void httpMessageNotReadableHandler(HttpServletRequest req, HttpServletResponse resp, HttpMessageNotReadableException exception) throws IOException {
        int code = 400;
        String message = "参数序列化异常:" + exception.getMessage();
        R r = R.data(code, null, message);
        doResponse(code, r, req, resp, exception);
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     * @param req
     * @param resp
     * @param exception
     * @throws IOException
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public void constraintViolationExceptionHandler(HttpServletRequest req, HttpServletResponse resp, ConstraintViolationException exception) throws IOException {
        Iterator<ConstraintViolation<?>> iterator = exception.getConstraintViolations().iterator();
        String message = "";
        while (iterator.hasNext()){
            ConstraintViolation<?> constraintViolation = iterator.next();
            message += "\r\n" + constraintViolation.getMessage();
        }

        message = "请求参数不正确:" + message;
        R r = R.data(413, null, message);
        doResponse(r.getCode(), r, req, resp, exception);
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

        String message = null;
        int code = 0;
        try{
            if (exception instanceof BusinessException) {
                if(((BusinessException) exception).getCode() == 0){
                    code = 501;
                }else{
                    code = ((BusinessException) exception).getCode();
                }
                doResponse(200, R.data(code, exception.getMessage()), req, resp, exception);
                return;
            }

            if (exception instanceof SystemException) {
                if(((SystemException) exception).getCode() == 0){
                    code = 401;
                }else{
                    code = ((SystemException) exception).getCode();
                }
                doResponse(code, R.data(code, exception.getMessage()), req, resp, exception);
                return;
            }

            if (exception instanceof CatchException) {
                code = ((CatchException) exception).getCode();
                message = exception.getMessage();
            } else if (exception instanceof NoHandlerFoundException) {
                code = 404;
                message = "没有找到接口";
            } else if (exception instanceof HttpRequestMethodNotSupportedException) {
                code = 405;
                message = "不支持的方法:" + exception.getMessage();
            } else if (exception instanceof MissingServletRequestParameterException) {
                code = 412;
                message = "参数错误:" + exception.getMessage();
            } else if (exception instanceof MethodArgumentTypeMismatchException) {
                code = 413;
                message = "参数错误:" + exception.getMessage();
            } else if (exception instanceof HttpMessageNotReadableException) {
                code = 400;
                message = "参数序列化异常:" + exception.getMessage();
            } else if (exception instanceof BadSqlGrammarException) {
                code = 500;
                message = "服务器错误[sql error]";
            } else if (exception instanceof MethodArgumentNotValidException) {
                BindingResult br = ((MethodArgumentNotValidException)exception).getBindingResult();
                StringBuilder errorMsg = new StringBuilder();
                if (br.hasErrors()) {
                    br.getAllErrors().forEach(error -> {
                        errorMsg.append(error.getDefaultMessage() + "\n");
                    });
                }
                code = 500;
                message = errorMsg.toString();
            } else{
                code = 500;
                message = "服务器错误[500]";
            }
            doResponse(code, R.data(code, message), req, resp, exception);
        }finally {
            if(ValueUtils.isBlank(message)){
                message = "unknown error";
            }
            log.error("Response error api:(" + req.getRequestURI() +
                    ") code:(" + code +  ") message:(" + message + ")", exception);
        }
    }
}

