package org.wlpiaoyi.framework.ee.activiti.engine.history;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.query.Query;
import org.activiti.engine.task.TaskQuery;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/9 21:52
 * {@code @version:}:       1.0
 */
public class BaseService extends org.wlpiaoyi.framework.ee.activiti.engine.BaseService<HistoryService> {
    protected BaseService(ProcessEngine processEngine) {
        super(processEngine, HistoryService.class);
    }

     protected <Q extends Query> Q createQuery(Class<Q> clazz) {
        throw new BusinessException("not support for createQuery with params");
    }
}
