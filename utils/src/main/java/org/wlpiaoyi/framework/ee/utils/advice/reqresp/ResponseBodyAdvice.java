package org.wlpiaoyi.framework.ee.utils.advice.reqresp;

import com.google.gson.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.wlpiaoyi.framework.ee.utils.ConfigModel;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.web.response.R;
import org.wlpiaoyi.framework.utils.web.response.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
public abstract class ResponseBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice<Object> {

    protected abstract SecurityOption getSecurityOption();

    public abstract ConfigModel getConfigModel();

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        Object res = o;
        if(res != null && this.getConfigModel().checkJsonParse(serverHttpRequest.getURI().getPath())){
            Gson gson = GsonBuilder.gsonDefault();
            JsonElement value = gson.toJsonTree(res);
            res = gson.fromJson(value, Map.class);
        }
        return res;
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
        int code = 200;
        try{

            if (exception instanceof BusinessException) {
                code = ((BusinessException) exception).getCode();
                message = ((BusinessException) exception).getMessage();
            }else if (exception instanceof CatchException) {
                code = ((CatchException) exception).getCode();
                message = ((CatchException) exception).getMessage();
            }else if (exception instanceof NoHandlerFoundException) {
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
//            } else if (exception instanceof BadSqlGrammarException) {
//                code = 500;
//                message = "服务器错误[001]";
            } else{
                code = 500;
                message = "服务器错误[500]";
            }
            R r = R.data(code, null, message);
            ResponseUtils.writeResponseJson(r,  200, resp);
        }finally {
            log.error("BusException Response api:(" + req.getRequestURI() +
                        ") code:(" + code +  ")", exception);
        }
    }
}

