package org.wlpiaoyi.framework.ee.utils.loader;

import org.springframework.context.ApplicationContext;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/17 16:39
 * {@code @version:}:       1.0
 */
public class FrameworkEELoader {


    public static void load(final ApplicationContext applicationContext, final long timerEpoch){
        IdempotenceLoader.load(applicationContext);
        IdWorkerLoader.load(applicationContext, timerEpoch);
    }

}
