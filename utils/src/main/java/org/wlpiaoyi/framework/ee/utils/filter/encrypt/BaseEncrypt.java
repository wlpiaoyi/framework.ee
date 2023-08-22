package org.wlpiaoyi.framework.ee.utils.filter.encrypt;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.ee.utils.filter.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.filter.FilterSupport;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;


/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/3/2 11:27
 * {@code @version:}:       1.0
 */
@Slf4j
public abstract class BaseEncrypt implements FilterSupport {

    protected abstract SecurityOption getSecurityOption();

    /**
     * 解密 Request Body
     * @param request
     * @param response
     * @param obj
     */
    protected abstract void decryptRequestBody(Object request, Object response, Aes aes,  Object obj);

    /**
     * 执行自定义的Filter逻辑
     * @param request
     * @param response
     * @param obj
     */
    protected abstract void doingFilter(Object request, Object response,  Object obj);

    /**
     * 加密 Response Body
     * @param request
     * @param response
     * @param obj
     */
    protected abstract void encryptResponseBody(Object request, Object response, Aes aes,  Object obj);

    protected final boolean checkSecurityParse(Object request, Object response){
        String uri = this.getRequestURI(request);
        log.debug("doFilter class:" + this.getClass().getName() + "uri:" + uri);
        ConfigModel configModel = this.getConfigModel();
        //没有需要的操作
        return configModel.checkSecurityParse(uri);
    }



    protected final void handelFilter(Object request, Object response, Aes aes, Object objReq, Object objDoing, Object objResp){
        this.decryptRequestBody(request, response, aes, objReq);
        //执行业务逻辑 交给下一个过滤器或servlet处理
        this.doingFilter(request, response, objDoing);
        this.encryptResponseBody(request, response, aes, objResp);
    }


}
