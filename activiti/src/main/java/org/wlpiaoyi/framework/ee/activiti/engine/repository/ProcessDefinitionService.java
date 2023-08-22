package org.wlpiaoyi.framework.ee.activiti.engine.repository;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 23:08
 * {@code @version:}:       1.0
 */
public class ProcessDefinitionService extends BaseService {
    public ProcessDefinitionService(ProcessEngine processEngine) {
        super(processEngine);
    }

    protected ProcessDefinitionQuery createQuery() {
        return super.createQuery(ProcessDefinitionQuery.class);
    }

    public ProcessDefinition getOne(String id) {
        return this.execService.getProcessDefinition(id);
    }

    public ProcessDefinition getOneByDeploymentId(String deploymentId) {
        List<ProcessDefinition> definitions = this.execService.createProcessDefinitionQuery().deploymentId(deploymentId).list();
        if(ValueUtils.isBlank(definitions)){
            return null;
        }
        return definitions.get(0);
    }

}
