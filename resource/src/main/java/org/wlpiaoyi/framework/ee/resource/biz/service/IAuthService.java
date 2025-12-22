package org.wlpiaoyi.framework.ee.resource.biz.service;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 令牌认证
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/21 15:59</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public interface IAuthService {

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 管理员认证
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>token</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 15:59</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    boolean authAdmin(String token);

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 用户认证
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>token</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 15:59</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     */
    boolean authUser(String token);

}
