package org.wlpiaoyi.framework.ee.file.manager.config.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.response.R;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.response.ResponseUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

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
public class GlobalExceptionHandler {
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
        int code = 200;
        try{
            if (exception instanceof BusinessException) {
                if(((BusinessException) exception).getCode() == 0){
                    code = 501;
                }else{
                    code = ((BusinessException) exception).getCode();
                }
                ResponseUtils.writeResponseJson(R.data(code, exception.getMessage()), 200, resp);
                return;
            }

            if (exception instanceof NoHandlerFoundException) {
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
                message = "服务器错误[001]";
            }  else if (exception instanceof MethodArgumentNotValidException) {
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
            ResponseUtils.writeResponseJson(R.data(code, message), code, resp);
        }finally {
//            if(ValueUtils.isBlank(message)){
            log.error("BusException Response api:(" + req.getRequestURI() +
                    ") code:(" + code +  ")", exception);
//            }else{
//                log.error("SysException Response api:(" + req.getRequestURI() +
//                        ") code:(" + code +  ") message:(" + message + ")");
//            }
        }
    }

//    /**
//     * validate验证错误处理
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler({MethodArgumentNotValidException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public R handleError(MethodArgumentNotValidException e) {
//        BindingResult br = e.getBindingResult();
//        StringBuilder errorMsg = new StringBuilder();
//        List<String> errorMsgList = new ArrayList<>();
//        if (br.hasErrors()) {
//            br.getAllErrors().forEach(error -> {
//                errorMsg.append(error.getDefaultMessage() + "\n");
//                errorMsgList.add(error.getDefaultMessage());
//            });
//        }
//        return R.fail(errorMsg.toString());
//    }
//
//    /**
//     * 参数异常处理
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(ParamExceptio.class)
//    public R<Object> paramException(ParamException e) {
//
//        R r = new R();
//        r.setCode(Integer.parseInt(e.getCode()));
//        r.setMsg(e.getMessage());
//        r.setSuccess(false);
//        r.setData(null);
//        return r;
//    }

//    /**
//     * 业务异常处理
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(BusinessException.class)
//    public R<Object> businessException(BusinessException e) {
//        return R.fail(e.getMessage());
//    }
//
//
//    /**
//     * 系统异常处理
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(Exception.class)
//    public R<Object> sysException(Exception e) {
//        return R.fail(e.getMessage());
//    }

}
