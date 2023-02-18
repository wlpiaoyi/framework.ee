package org.wlpiaoyi.framework.ee.utils.advice.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 17:46
 * {@code @version:}:       1.0
 */
public interface IdempotenceMoon {

    String getKey(HttpServletRequest request, HttpServletResponse response, Object handler);

}
