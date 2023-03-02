package org.wlpiaoyi.framework.ee.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.wlpiaoyi.framework.ee.utils.launcher.nacos.LauncherRunner;
import org.wlpiaoyi.framework.ee.utils.launcher.nacos.CloudApplication;
import org.wlpiaoyi.framework.ee.utils.loader.FrameworkEELoader;

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
        LauncherRunner.run("ee.utils", UtilsApplicationTest.class, args);
    }

    private static ApplicationContext APPLICATION_CONTEXT;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        FrameworkEELoader.load(applicationContext, 1633017600000L);
    }
}
