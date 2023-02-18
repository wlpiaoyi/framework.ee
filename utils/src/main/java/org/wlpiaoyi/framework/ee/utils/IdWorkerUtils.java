package org.wlpiaoyi.framework.ee.utils;

import org.springframework.context.ApplicationContext;
import org.wlpiaoyi.framework.ee.utils.loader.IdWorkerLoader;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.snowflake.IdWorker;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/18 18:23
 * {@code @version:}:       1.0
 */
public class IdWorkerUtils extends IdWorkerLoader {

    public static IdWorker getIdWorker(){
        return ID_WORKER;
    }

}
