package org.wlpiaoyi.framework.ee.utils.status;


import lombok.Getter;

/**
 * @author wlpia
 */
@Getter
public enum Base implements Status{

    /** **/
    Invalid("无效", 0),
    Valid("有效", 1);

    /** 成员变量 **/
    private final String name;
    private final int index;

    Base(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (Base c : Base.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
}
