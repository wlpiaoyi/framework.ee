package org.wlpiaoyi.framework.ee.utils.status;


import lombok.Getter;

/**
 * 文件异常
 * @author wlpia
 */
@Getter
public enum File implements Status {


    /** 通用异常 **/
    CommonError("文件异常", 2000),
    EmptyError("空文件异常", 2001),
    LargestError("文件过大异常", 2001),
    RepeatError("文件上传重复", 2003),
    MoveError("文件移动失败", 2004),
    UploadError("文件上传失败", 2005);

    private final String name;
    private final int index;

    File(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (File c : File.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

}
