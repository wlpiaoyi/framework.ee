package org.wlpiaoyi.framework.ee.utils.launcher.nacos;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;
/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/13 11:44
 * {@code @version:}:       1.0
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDiscoveryClient
//@EnableFeignClients({"org.wlpiaoyi.framework.ee.utils.loader"})
@SpringBootApplication
public @interface CloudApplication {
}
