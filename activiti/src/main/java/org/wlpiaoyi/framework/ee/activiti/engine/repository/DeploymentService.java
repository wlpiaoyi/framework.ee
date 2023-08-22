package org.wlpiaoyi.framework.ee.activiti.engine.repository;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.List;

/**
 * 部署流程管理
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 21:46
 * {@code @version:}:       1.0
 */
public class DeploymentService extends BaseService {

    public DeploymentService(ProcessEngine processEngine) {
        super(processEngine);
    }

    protected DeploymentQuery createQuery() {
        return super.createQuery(DeploymentQuery.class);
    }

    /**
     * 删除之前已经部署的模型, 当前部署的不能删除
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        Deployment deployment = this.createQuery().deploymentId(id).singleResult();
        if(deployment == null){
            return;
        }
        List<Model> models = this.execService.createModelQuery().modelKey(deployment.getKey()).listPage(0, 1);
        if(ValueUtils.isBlank(models)){
            this.execService.deleteDeployment(id);
            return;
        }
        Model model = models.get(0);
        if(deployment.getId().equals(model.getDeploymentId())){
            throw new BusinessException(501,"不能删除最新部署的数据");
        }
        this.execService.deleteDeployment(id);
    }

    /**
     * 获取部署对象
     * @param id
     * @return
     */
    public Deployment getOne(String id) {
        return this.createQuery().deploymentId(id).singleResult();
    }

    /**
     * 根据模型Key获取最新部署对象
     * @param modelKey
     * @return
     */
    public Deployment getOneByModelKey(String modelKey){
        ModelQuery modelQuery = this.processEngine.getRepositoryService().createModelQuery();
        modelQuery.modelKey(modelKey);
        List<Model> models = modelQuery.listPage(0, 1);
        if(ValueUtils.isBlank(models)){
            return null;
        }
        Model model = models.get(0);
        if(ValueUtils.isBlank(model.getDeploymentId())){
            return null;
        }
        Deployment deployment = this.getOne(model.getDeploymentId());
        return deployment;
    }


    /**
     * 部署流程
     * @param model
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Deployment deploy(Model model) {
        Deployment deployment = this.execService.createDeployment()
                .addString(model.getKey() + ".bpmn", model.getMetaInfo()
                        .replace("targetNamespace=\"http://activiti.org/bpmn\"",
                                "targetNamespace=\"" + model.getCategory() + "\""))
                .name(model.getName())
                .key(model.getKey())
                .tenantId(model.getTenantId())
                .category(model.getCategory())
                .deploy();
        model.setDeploymentId(deployment.getId());
        this.execService.saveModel(model);
        return deployment;
    }
}
