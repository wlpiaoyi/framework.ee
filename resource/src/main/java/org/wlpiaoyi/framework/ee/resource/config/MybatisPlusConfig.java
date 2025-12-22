package org.wlpiaoyi.framework.ee.resource.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
	 /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
//    @Bean
//    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
//        return configuration -> {
//            TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
//            String basePackage = "org.wlpiaoyi.framework.ee.resource.biz.domain.enums";
//            PackageUtils.iteratorClazz(basePackage, new PackageUtils.IteratorRun() {
//                @Override
//                public void run(Class<?> aClass) {
//                    Class<BaseEnum> eClass = (Class<BaseEnum>) aClass;
//                    registry.register(eClass, new EnumTypeHandler<>(eClass));
//                }
//            });
//            // 注册所有你需要的枚举
//        };
//    }
//    @Bean
//    public void registerTypeHandlers(TypeHandlerRegistry registry) {
//        // 为每个具体的枚举类型注册一个 handler 实例
//
//        String basePackage = "org.wlpiaoyi.framework.ee.resource.biz.domain.enums";
//        PackageUtils.iteratorClazz(basePackage, new PackageUtils.IteratorRun() {
//            @Override
//            public void run(Class<?> aClass) {
//                Class<BaseEnum> eClass = (Class<BaseEnum>) aClass;
//                registry.register(eClass, new EnumTypeHandler<>(eClass));
//            }
//        });
//    }
}
