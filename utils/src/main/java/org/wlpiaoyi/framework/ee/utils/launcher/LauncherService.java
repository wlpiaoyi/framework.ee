package org.wlpiaoyi.framework.ee.utils.launcher;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

/**
 * 应用启动回调
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/13 9:38
 * {@code @version:}:       1.0
 */
public interface LauncherService extends Ordered, Comparable<LauncherService> {
    void launcher(SpringApplicationBuilder builder, String appName, String profile, boolean isLocalDev);

    default int getOrder() {
        return 0;
    }

    default int compareTo(LauncherService o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}