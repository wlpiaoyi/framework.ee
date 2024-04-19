package org.wlpiaoyi.framework.ee.resource;

import org.jetbrains.annotations.NotNull;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/14 17:40
 * {@code @version:}:       1.0
 */
//Dspring.config.location=/data/config/application-sms.yml
@SpringBootApplication(scanBasePackages = {
        "org.wlpiaoyi.framework.ee.resource"
})
@ComponentScan(basePackages = {"org.wlpiaoyi.framework.ee.resource"})
@MapperScan("org.wlpiaoyi.framework.ee.resource")
public class Application implements ApplicationContextAware, BeanFactoryPostProcessor {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(BaseEntity.ZONE));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationInitializer.SpringUtilsBuilder.build().setBeanFactory(beanFactory);
    }
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        ApplicationInitializer.SpringUtilsBuilder.build().setApplicationContext(applicationContext);
    }

}
