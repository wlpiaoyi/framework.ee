package org.wlpiaoyi.framework.ee.utils.status;

import lombok.Getter;

/**
 *
 * @author wlpia
 */
@Getter
public enum Login implements Status {

    UnLogin("未登录", 2000);

    // 成员变量
    private final String name;
    private final int index;

    Login(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (Login c : Login.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

}
