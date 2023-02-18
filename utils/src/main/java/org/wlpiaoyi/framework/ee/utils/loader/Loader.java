package org.wlpiaoyi.framework.ee.utils.loader;

import lombok.Setter;
import org.springframework.context.ApplicationContext;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/17 16:39
 * {@code @version:}:       1.0
 */
public class Loader {

    @Setter
    private static long timerEpoch = 1633017600000L;

    public static void LoadData(ApplicationContext applicationContext){
        IdempotenceLoader.load(applicationContext);
        IdWorkerLoader.load(applicationContext, timerEpoch);
    }

}
