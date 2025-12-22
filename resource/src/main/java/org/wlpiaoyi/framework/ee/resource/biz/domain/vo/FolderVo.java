package org.wlpiaoyi.framework.ee.resource.biz.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.Folder;

/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	文件夹 视图实体类
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderVo extends Folder implements Serializable {
	private static final long serialVersionUID = 1L;

}
