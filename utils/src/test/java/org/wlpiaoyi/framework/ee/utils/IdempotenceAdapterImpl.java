package org.wlpiaoyi.framework.ee.utils;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.wlpiaoyi.framework.ee.utils.advice.handle.IdempotenceMoon;
import org.wlpiaoyi.framework.ee.utils.advice.handle.IdempotenceAdapter;
import org.wlpiaoyi.framework.ee.utils.advice.ConfigModel;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Coder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 17:49
 * {@code @version:}:       1.0
 */
@Component
public class IdempotenceAdapterImpl extends IdempotenceAdapter {


    @Autowired
    private ConfigModel configModel;

    public ConfigModel getConfigModel() {
        return configModel;
    }

    public class IdempotenceMoonImpl implements IdempotenceMoon {

        @SneakyThrows
        @Override
        public String getKey(HttpServletRequest request, HttpServletResponse response, Object handler) {
            return new String(Coder.encryptMD5(request.getRequestURI().getBytes(StandardCharsets.UTF_8)))
                    + new String(Coder.encryptMD5(request.getHeader("token").getBytes(StandardCharsets.UTF_8)));
        }
    }

    private IdempotenceMoon idempotenceMoon = new IdempotenceMoonImpl();

    @Override
    public IdempotenceMoon getIdempotenceMoon() {
        return this.idempotenceMoon;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
