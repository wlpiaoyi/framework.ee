package org.wlpiaoyi.framework.ee.utils.status;

import lombok.Getter;

/**
 *
 * @author wlpia
 */
@Getter
public enum Account implements Status {


    /** 枚举 **/
    Invalid("无效", 0),
    Valid("有效", 1),
    Freeze("冻结", 10);

    /** 成员变量 **/
    private final String name;
    private final int index;

    Account(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (Account c : Account.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

}
