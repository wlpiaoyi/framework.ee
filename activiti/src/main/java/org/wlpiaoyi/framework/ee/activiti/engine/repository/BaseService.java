package org.wlpiaoyi.framework.ee.activiti.engine.repository;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.query.Query;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ModelQuery;
import org.wlpiaoyi.framework.utils.exception.CatchException;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 21:46
 * {@code @version:}:       1.0
 */
abstract class BaseService extends org.wlpiaoyi.framework.ee.activiti.engine.BaseService<RepositoryService> {

    protected BaseService(ProcessEngine processEngine) {
        super(processEngine, RepositoryService.class);

    }

    /**
     * 获取查询对象
     * @param clazz
     * @return
     * @param <Q>
     */
    protected  <Q extends Query> Q createQuery(Class<Q> clazz){
        if(clazz == DeploymentQuery.class){
            return (Q) this.execService.createDeploymentQuery();
        }
        if(clazz == ModelQuery.class){
            return (Q) this.execService.createModelQuery();
        }
        return null;
    }
}
