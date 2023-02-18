package org.wlpiaoyi.framework.ee.utils.advice.handle;

import java.lang.annotation.*;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/16 18:18
 * {@code @version:}:       1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotence {

}
