package org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo;

import lombok.EqualsAndHashCode;
import java.io.Serializable;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.domain.entity.BaseEntity;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 视图实体类
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
@lombok.Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoVo extends FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Object expandObj;

}
