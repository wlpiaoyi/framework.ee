package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import org.wlpiaoyi.framework.ee.resource.biz.service.IFolderService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.Folder;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.FolderMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FolderVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.ro.FolderRo;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	文件夹 服务类实现
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
@Primary
@Service
public class FolderServiceImpl extends BaseServiceImpl<FolderMapper, Folder> implements IFolderService {


}
