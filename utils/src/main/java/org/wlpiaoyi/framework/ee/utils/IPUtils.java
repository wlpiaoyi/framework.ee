package org.wlpiaoyi.framework.ee.utils;

import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {
    /**
     * 获取IP
     *
     * @param request
     * @return
     */
    public static String getIP(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String headerIP = request.getHeader("x-real-ip");
        if (ValueUtils.isBlank(headerIP) || "null".equals(headerIP)) {
            headerIP = request.getHeader("x-forwarded-for");
        }
        if (!ValueUtils.isBlank(headerIP) && !"null".equals(headerIP)) {
            ip = headerIP;
        }
        return ip;
    }
}
