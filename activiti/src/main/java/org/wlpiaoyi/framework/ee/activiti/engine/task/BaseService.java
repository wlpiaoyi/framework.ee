package org.wlpiaoyi.framework.ee.activiti.engine.task;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.query.Query;
import org.activiti.engine.task.TaskQuery;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/9 21:52
 * {@code @version:}:       1.0
 */
public class BaseService extends org.wlpiaoyi.framework.ee.activiti.engine.BaseService<TaskService> {
    protected BaseService(ProcessEngine processEngine) {
        super(processEngine, TaskService.class);
    }

     protected  <Q extends Query> Q createQuery(Class<Q> clazz) {
        if(clazz == TaskQuery.class){
            TaskQuery query = this.execService.createTaskQuery();
            return (Q) query;
        }
        return null;
    }
}
