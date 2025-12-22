package org.wlpiaoyi.framework.ee.resource.biz.domain.enums;

import lombok.Getter;
import org.wlpiaoyi.framework.ee.resource.domain.enums.BaseEnum;

/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	文件权限类型 枚举
 * {@code @date:} 			2025-12-22 16:56:49
 * {@code @version:}: 		1.0
 */
@Getter
public enum FileRoleTypeEnum implements BaseEnum {


    Default(0, "默认"),
    DownInherit(1, "向下继承"), 
    PassiveDownInherit(2, "被动向下继承"), 
    ;

    private final Integer value;
    private final String desc;


    FileRoleTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static FileRoleTypeEnum getByValue(Integer value){
        for (FileRoleTypeEnum enums : FileRoleTypeEnum.values()) {
            if(enums.value.equals(value)){
                return enums;
            }
        }
        return Default;
    }
}
