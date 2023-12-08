package org.wlpiaoyi.framework.ee.file.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.wlpiaoyi.framework.ee.file.manager.utils.SpringUtils;


@Slf4j
class ApplicationInitializer {

    public static class SpringUtilsBuilder extends SpringUtils{
        private SpringUtilsBuilder(){}
        public static SpringUtilsBuilder build(){
            return new SpringUtilsBuilder();
        }
        public SpringUtilsBuilder setBeanFactory(ConfigurableListableBeanFactory beanFactory){
            SpringUtils.beanFactory = beanFactory;
            return this;
        }
        public SpringUtilsBuilder setApplicationContext(ApplicationContext applicationContext){
            SpringUtils.applicationContext = applicationContext;
            return this;
        }
    }

}
