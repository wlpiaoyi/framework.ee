package org.wlpiaoyi.framework.ee.activiti.engine;

import lombok.Getter;
import org.activiti.engine.*;
import org.activiti.engine.query.Query;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 23:30
 * {@code @version:}:       1.0
 */
public abstract class BaseService<ES> {

    protected ProcessEngine processEngine;

    @Getter
    protected ES execService;

    protected BaseService(ProcessEngine processEngine, Class<ES> esClass) {
        this.processEngine = processEngine;
        if(esClass == RepositoryService.class){
            this.execService = (ES) this.processEngine.getRepositoryService();
        }
        if(esClass == RuntimeService.class){
            this.execService = (ES) this.processEngine.getRuntimeService();
        }
        if(esClass == TaskService.class){
            this.execService = (ES) this.processEngine.getTaskService();
        }
        if(esClass == HistoryService.class){
            this.execService = (ES) this.processEngine.getHistoryService();
        }
        if(esClass == ManagementService.class){
            this.execService = (ES) this.processEngine.getManagementService();
        }
        if(esClass == DynamicBpmnService.class){
            this.execService = (ES) this.processEngine.getDynamicBpmnService();
        }
    }
    protected abstract <Q extends Query> Q createQuery(Class<Q> clazz);
}
