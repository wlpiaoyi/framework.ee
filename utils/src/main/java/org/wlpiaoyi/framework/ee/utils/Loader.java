package org.wlpiaoyi.framework.ee.utils;

import org.springframework.context.ApplicationContext;
import org.wlpiaoyi.framework.ee.utils.advice.handle.IdempotenceAdapter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/17 16:39
 * {@code @version:}:       1.0
 */
public class Loader {
    public static void LoadData(ApplicationContext applicationContext){
        IdempotenceAdapter.loader(applicationContext);
    }

}
