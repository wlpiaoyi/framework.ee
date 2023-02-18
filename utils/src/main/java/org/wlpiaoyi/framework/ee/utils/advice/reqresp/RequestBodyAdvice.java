package org.wlpiaoyi.framework.ee.utils.advice.reqresp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wlpiaoyi.framework.ee.utils.advice.AnnotationPathUtils;
import org.wlpiaoyi.framework.ee.utils.ConfigModel;

import java.lang.reflect.Type;



@Slf4j
public abstract class RequestBodyAdvice implements org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice {

    public abstract SecurityOption getSecurityOption();

    public abstract ConfigModel getConfigModel();

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage,
                                           MethodParameter methodParameter,
                                           Type type,
                                           Class<? extends HttpMessageConverter<?>> aClass){
        try{
            final boolean[] isCheckParses = {false};
            final RequestBodyAdvice d_this = this;
            RequestMapping rm = methodParameter.getDeclaringClass().getAnnotation(RequestMapping.class);
            String[] paths1 = AnnotationPathUtils.requestMappingValue(rm);
            for (String path1 : paths1) {
                path1 = AnnotationPathUtils.checkMappingValue(path1);
                AnnotationPathUtils.iteratorAllPath2(methodParameter.getMethod(), path1, null, path -> {
                    if(isCheckParses[0]){
                        isCheckParses[0] = d_this.getConfigModel().checkJsonParse(path);
                        return -1;
                    }
                    return 0;
                });
            }
            if(isCheckParses[0]){
                HttpInputMessageImpl inputMessage = new HttpInputMessageImpl();
                inputMessage.setCheckJsonParse(isCheckParses[0]);
                inputMessage.setCharsetName(this.getConfigModel().getCharsetName());
                inputMessage.done(httpInputMessage,
                        methodParameter);
                return inputMessage;
            }else{
                return httpInputMessage;
            }
        }catch (Exception e){
            return httpInputMessage;
        }
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}
