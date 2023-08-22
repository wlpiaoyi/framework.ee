package org.wlpiaoyi.framework.ee.activiti.engine.runtime;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.query.Query;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 23:29
 * {@code @version:}:       1.0
 */
abstract class BaseService extends org.wlpiaoyi.framework.ee.activiti.engine.BaseService<RuntimeService> {
    public BaseService(ProcessEngine processEngine) {
        super(processEngine, RuntimeService.class);
    }

    @Override
    protected  <Q extends Query> Q createQuery(Class<Q> clazz) {
        if(clazz == ProcessInstanceQuery.class){
            ProcessInstanceQuery query = this.execService.createProcessInstanceQuery();
            return (Q) query;
        }
        if(clazz == ExecutionQuery.class){
            ExecutionQuery query = this.execService.createExecutionQuery();
            return (Q) query;
        }
        return null;
    }
}
