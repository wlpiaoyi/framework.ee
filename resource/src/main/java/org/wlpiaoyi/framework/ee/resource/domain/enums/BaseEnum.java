package org.wlpiaoyi.framework.ee.resource.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * TODO
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/12/19 11:00</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public interface BaseEnum extends IEnum {

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    String name();

    /**
     * 获取枚举描述
     *
     * @return 描述
     */
    String getDesc();

}
