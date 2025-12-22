package org.wlpiaoyi.framework.ee.resource.biz.domain.enums;

import lombok.Getter;
import org.wlpiaoyi.framework.ee.resource.domain.enums.BaseEnum;

/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	对外数据权限,二进制 枚举
 * {@code @date:} 			2025-12-22 16:56:49
 * {@code @version:}: 		1.0
 */
@Getter
public enum DataIndexEnum implements BaseEnum {


    View(0b1, "查看"), 
    Download(0b10, "下载"), 
    Edit(0b100, "修改/删除"), 
    ;

    private final Integer value;
    private final String desc;


    DataIndexEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static DataIndexEnum getByValue(Integer value){
        for (DataIndexEnum enums : DataIndexEnum.values()) {
            if(enums.value.equals(value)){
                return enums;
            }
        }
        return View;
    }
}
