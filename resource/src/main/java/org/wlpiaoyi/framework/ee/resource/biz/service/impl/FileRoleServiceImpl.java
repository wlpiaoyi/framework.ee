package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import org.wlpiaoyi.framework.ee.resource.biz.service.IFileRoleService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileRole;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.FileRoleMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileRoleVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.ro.FileRoleRo;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	文件权限 服务类实现
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
@Primary
@Service
public class FileRoleServiceImpl extends BaseServiceImpl<FileRoleMapper, FileRole> implements IFileRoleService {


}
