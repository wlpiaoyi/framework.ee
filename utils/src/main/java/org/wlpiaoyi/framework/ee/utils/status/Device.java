package org.wlpiaoyi.framework.ee.utils.status;

/**
 *
 * @author wlpia
 */
public enum Device implements Status{

    /** **/
    ContainsError("已存在当前硬件码", 2101),
    UsingError("已使用当前硬件码", 2102),
    NullError("没有硬件码", 2103);

    private final String name;
    private final int index;

    Device(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static Device getEnum(int index) {
        for (Device c : Device.values()) {
            if (c.getIndex() == index) {
                return c;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }
    public int getIndex() {
        return index;
    }
}
