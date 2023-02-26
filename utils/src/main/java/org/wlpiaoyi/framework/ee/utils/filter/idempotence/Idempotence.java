package org.wlpiaoyi.framework.ee.utils.filter.idempotence;

import java.lang.annotation.*;

/**
 * 幂等性
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    幂等性
 * {@code @date:}           2023/2/16 18:18
 * {@code @version:}:       1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotence {

}
