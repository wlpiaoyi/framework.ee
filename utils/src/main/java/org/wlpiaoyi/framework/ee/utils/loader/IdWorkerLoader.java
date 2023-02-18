package org.wlpiaoyi.framework.ee.utils.loader;

import org.springframework.context.ApplicationContext;
import org.wlpiaoyi.framework.ee.utils.ConfigModel;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.snowflake.IdWorker;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/18 18:47
 * {@code @version:}:       1.0
 */
public class IdWorkerLoader {

    protected static IdWorker ID_WORKER = null;

    static void load(ApplicationContext applicationContext, long timerEpoch){
        ConfigModel configModel = applicationContext.getBean(ConfigModel.class);
        byte workerId = configModel.getWorkerId();
        byte datacenterId = configModel.getDatacenterId();
        if(workerId < 0 || datacenterId < 0){
            throw new BusinessException("机器Id必须介于0~31之间");
        }
        ID_WORKER = new IdWorker(workerId, datacenterId, timerEpoch);
    }
}
