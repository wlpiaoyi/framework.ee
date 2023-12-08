package org.wlpiaoyi.framework.ee.file.manager;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.wlpiaoyi.framework.ee.file.manager.domain.entity.BaseEntity;
import org.wlpiaoyi.framework.ee.file.manager.utils.SpringUtils;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/14 17:40
 * {@code @version:}:       1.0
 */
@SpringBootApplication(scanBasePackages = {
        "org.wlpiaoyi.framework.ee.file.manager"
})
@ComponentScan(basePackages = {"org.wlpiaoyi.framework.ee.file.manager"})
@MapperScan("org.wlpiaoyi.framework.ee.file.manager")
public class Application implements ApplicationContextAware, BeanFactoryPostProcessor {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(BaseEntity.ZONE));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationInitializer.SpringUtilsBuilder.build().setBeanFactory(beanFactory);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationInitializer.SpringUtilsBuilder.build().setApplicationContext(applicationContext);
    }

}
