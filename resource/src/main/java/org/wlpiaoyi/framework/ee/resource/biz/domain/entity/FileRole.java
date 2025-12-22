package org.wlpiaoyi.framework.ee.resource.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;
import lombok.experimental.Accessors;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.wlpiaoyi.framework.ee.resource.biz.domain.enums.DataIndexEnum;
import org.wlpiaoyi.framework.ee.resource.biz.domain.enums.FileRoleTypeEnum;


/**
 * <p><b>{@code @author:}</b>         admin:DESKTOP-RLUL55B</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 文件权限 实体类
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025-12-22 17:09:27</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Data
@Accessors(chain=true)
@TableName("res_file_role")
@Schema(description = "文件权限")
@EqualsAndHashCode(callSuper = true)
public class FileRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /** name **/
    @Schema(name = "name" , description = "name")
    @NotBlank(message = "name不能为空")
    private String name;

    /** 对外数据权限,二进制:DataIndex(0b1:查看-View, 0b10:下载-Download, 0b100:修改/删除-Edit) **/
    @Schema(name = "permissionValue" , description = "二进制:DataIndex(0b1:查看-View, 0b10:下载-Download, 0b100:修改/删除-Edit)")
    @NotNull(message = "对外数据权限,二进制不能为空")
    private DataIndexEnum permissionValue;

    /** 文件权限类型:FileRoleType(0:默认-Default, 1 :向下继承-DownInherit,  2:被动向下继承-PassiveDownInherit) **/
    @Schema(name = "permissionType" , description = " 1 :向下继承-DownInherit,  2:被动向下继承-PassiveDownInherit)")
    @NotNull(message = "文件权限类型不能为空")
    private FileRoleTypeEnum permissionType;

}
