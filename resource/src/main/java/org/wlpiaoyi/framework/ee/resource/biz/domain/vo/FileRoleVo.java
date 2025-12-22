package org.wlpiaoyi.framework.ee.resource.biz.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileRole;

import java.io.Serializable;

/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	文件权限 视图实体类
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileRoleVo extends FileRole implements Serializable {
	private static final long serialVersionUID = 1L;

}
