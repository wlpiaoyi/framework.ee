package org.wlpiaoyi.framework.ee.utils.launcher.nacos;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.wlpiaoyi.framework.ee.utils.launcher.LauncherService;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 启动应用
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/13 9:34
 * {@code @version:}:       1.0
 */
public class LauncherRunner {
    public static ConfigurableApplicationContext run(String appName, Class source, String... args) {
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
        return builder.run(args);
    }

    static SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class source, String... args) {
        Assert.hasText(appName, "[appName]服务名不能为空");
        ConfigurableEnvironment environment = new StandardEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new SimpleCommandLinePropertySource(args));
        propertySources.addLast(new MapPropertySource("systemProperties", environment.getSystemProperties()));
        propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", environment.getSystemEnvironment()));
        String[] activeProfiles = environment.getActiveProfiles();
        List<String> profiles = Arrays.asList(activeProfiles);
        List<String> presetProfiles = new ArrayList(Arrays.asList("dev", "test", "prod"));
        presetProfiles.retainAll(profiles);
        List<String> activeProfileList = new ArrayList(profiles);
        Function<Object[], String> joinFun = StringUtils::arrayToCommaDelimitedString;
        SpringApplicationBuilder builder = new SpringApplicationBuilder(new Class[]{source});
        String profile;
        if (activeProfileList.isEmpty()) {
            profile = "dev";
            activeProfileList.add(profile);
            builder.profiles(new String[]{profile});
        } else {
            if (activeProfileList.size() != 1) {
                throw new RuntimeException("同时存在环境变量:[" + StringUtils.arrayToCommaDelimitedString(activeProfiles) + "]");
            }

            profile = activeProfileList.get(0);
        }

        String startJarPath = LauncherRunner.class.getResource("/").getPath().split("!")[0];
        String activePros = (String)joinFun.apply(activeProfileList.toArray());
        System.out.printf("----启动中，读取到的环境变量:[%s]，jar地址:[%s]----%n", activePros, startJarPath);
        Properties props = System.getProperties();
        props.setProperty("spring.application.name", appName);
        props.setProperty("spring.profiles.active", profile);
        props.setProperty("info.version", "1.0.1");
        props.setProperty("info.desc", appName);
        props.setProperty("file.encoding", StandardCharsets.UTF_8.name());
        props.setProperty("framework.ee.env", profile);
        props.setProperty("framework.ee.name", appName);
        props.setProperty("framework.ee.is-local", String.valueOf(isLocalDev()));
        props.setProperty("framework.ee.dev-mode", "prod".equals(profile) ? "false" : "true");
        props.setProperty("loadbalancer.client.name", appName);
        props.setProperty("spring.cloud.nacos.discovery.server-addr", NacosConstant.NACOS_ADDR);
        props.setProperty("spring.cloud.nacos.config.server-addr", NacosConstant.NACOS_ADDR);
        props.setProperty("spring.cloud.sentinel.transport.dashboard", NacosConstant.SENTINEL_ADDR);
        props.setProperty("spring.zipkin.base-url", NacosConstant.ZIPKIN_ADDR);
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("spring.main.allow-bean-definition-overriding", "true");
        defaultProperties.setProperty("spring.sleuth.sampler.percentage", "1.0");
        defaultProperties.setProperty("spring.cloud.alibaba.seata.tx-service-group", appName.concat("-group"));
        defaultProperties.setProperty("spring.cloud.nacos.config.file-extension", NacosConstant.NACOS_CONFIG_FORMAT);
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].data-id", NacosConstant.sharedDataId());
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].group", NacosConstant.NACOS_CONFIG_GROUP);
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[0].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].data-id", NacosConstant.sharedDataId(profile));
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].group", NacosConstant.NACOS_CONFIG_GROUP);
        defaultProperties.setProperty("spring.cloud.nacos.config.shared-configs[1].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
        builder.properties(defaultProperties);
        builder.bannerMode(Banner.Mode.LOG);
        List<LauncherService> launcherList = new ArrayList();
        ServiceLoader.load(LauncherService.class).forEach(launcherList::add);
        (launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList())).forEach((launcherService) -> {
            launcherService.launcher(builder, appName, profile, isLocalDev());
        });
        return builder;
    }

    static boolean isLocalDev() {
        String osName = System.getProperty("os.name");
        return StringUtils.hasText(osName) && !"LINUX".equalsIgnoreCase(osName);
    }
}
