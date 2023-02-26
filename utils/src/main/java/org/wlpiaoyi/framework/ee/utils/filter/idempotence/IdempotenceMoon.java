package org.wlpiaoyi.framework.ee.utils.filter.idempotence;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 17:46
 * {@code @version:}:       1.0
 */
public interface IdempotenceMoon {

    String getKey(ServletRequest servletRequest);

}
