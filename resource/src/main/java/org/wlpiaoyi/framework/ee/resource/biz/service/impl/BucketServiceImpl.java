package org.wlpiaoyi.framework.ee.resource.biz.service.impl;

import org.wlpiaoyi.framework.ee.resource.biz.service.IBucketService;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.Bucket;
import org.wlpiaoyi.framework.ee.resource.biz.domain.mapper.BucketMapper;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.BucketVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.ro.BucketRo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.wlpiaoyi.framework.ee.resource.service.impl.BaseServiceImpl;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	桶 服务类实现
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
@Primary
@Service
public class BucketServiceImpl extends BaseServiceImpl<BucketMapper, Bucket> implements IBucketService {


}
