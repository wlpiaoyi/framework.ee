package org.wlpiaoyi.framework.ee.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.wlpiaoyi.framework.ee.utils.advice.handle.IdempotenceAdapter;
import org.wlpiaoyi.framework.ee.utils.loader.nacos.ApplicationRunner;
import org.wlpiaoyi.framework.ee.utils.loader.nacos.CloudApplication;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/13 9:31
 * {@code @version:}:       1.0
 */
@CloudApplication
//@ComponentScan({"org.springblade.online"})
public class UtilsApplicationTest implements ApplicationContextAware {
    public static void main(String[] args) {
        ApplicationRunner.run("ee.utils", UtilsApplicationTest.class, args);
    }

    private static ApplicationContext APPLICATION_CONTEXT;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Loader.LoadData(applicationContext);
    }
}
