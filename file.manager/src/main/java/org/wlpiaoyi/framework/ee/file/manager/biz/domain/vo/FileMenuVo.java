package org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件目录 视图实体类
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileMenuVo extends FileMenu implements Serializable {
	private static final long serialVersionUID = 1L;


}
