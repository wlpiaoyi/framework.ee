package org.wlpiaoyi.framework.ee.activiti.engine.runtime;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.query.Query;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 23:42
 * {@code @version:}:       1.0
 */
public class ProcessInstanceService extends BaseService{
    public ProcessInstanceService(ProcessEngine processEngine) {
        super(processEngine);
    }

    public ProcessInstanceQuery createQuery() {
        return super.createQuery(ProcessInstanceQuery.class);
    }

    public ProcessInstance getOne(String id){
        return this.getExecService().createProcessInstanceQuery().processInstanceId(id).singleResult();
    }


    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startProcess(String processDefinitionId) {
        ProcessInstance processInstance = this.execService
                .startProcessInstanceById(processDefinitionId);
        return processInstance;
    }
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startProcess(String processDefinitionId, String businessKey, Map<String, Object> variables) {
        ProcessInstance processInstance = this.execService
                .startProcessInstanceById(processDefinitionId, businessKey, variables);
        return processInstance;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessInstance(String processInstanceId, String deleteReason){
        this.execService.deleteProcessInstance(processInstanceId, deleteReason);
    }

    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessInstanceById(String processInstanceId){
        this.execService.suspendProcessInstanceById(processInstanceId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void activateProcessInstanceById(String processInstanceId){
        this.execService.activateProcessInstanceById(processInstanceId);
    }


}
