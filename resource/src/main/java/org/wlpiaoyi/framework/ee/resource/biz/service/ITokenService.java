package org.wlpiaoyi.framework.ee.resource.biz.service;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.Token;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.TokenVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.ro.TokenRo;
import org.wlpiaoyi.framework.ee.resource.service.IBaseService;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	令牌 服务类接口
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
public interface ITokenService extends IBaseService<Token> {

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 创建管理员临时令牌
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 16:13</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    Token createAdminTempToken();

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取管理员临时令牌
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 16:13</p>
     * <p><b>{@code @author:}</b>wlpiaoyi
     * <hr/>
     */
    Token getAdminTempToken();

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 清空管理员临时令牌
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 16:13</p>
     * <p><b>{@code @author:}</b>wlpiaoyi
     * <hr/>
     */
    void clearAdminTempToken();


}
