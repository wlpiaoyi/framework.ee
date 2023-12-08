package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper.FileMenuMapper;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileMenuVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro.FileMenuRo;
import org.wlpiaoyi.framework.ee.file.manager.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件目录 服务类实现
 * {@code @date:} 			2023-12-08 16:48:27
 * {@code @version:}: 		1.0
 */
@Slf4j
@Primary
@Service
public class FileMenuServiceImpl extends BaseServiceImpl<FileMenuMapper, FileMenu> implements IFileMenuService {


}
