package org.wlpiaoyi.framework.ee.activiti.engine.repository;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.wlpiaoyi.framework.ee.activiti.engine.repository.DeploymentService;
import org.wlpiaoyi.framework.ee.activiti.engine.repository.ModelService;
import org.wlpiaoyi.framework.ee.activiti.engine.repository.ProcessDefinitionService;
import org.wlpiaoyi.framework.ee.activiti.utils.ActivitiUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/8 20:25
 * {@code @version:}:       1.0
 */
@Slf4j
public class DeploymentServiceTest {
    static ProcessEngine processEngine;

    static {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        //&nullCatalogMeansCurrent=true没有这个就不能自动创建表
        dataSourceBuilder.url("jdbc:mysql://127.0.0.1:3306/activti?" +
                "useUnicode=true" +
                "&characterEncoding=utf-8" +
                "&useSSL=true" +
                "&serverTimezone=GMT%2B8");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("00000000");
        processEngine = ActivitiUtils.buildProcessEngine(dataSourceBuilder.build());
    }

    DeploymentService deploymentService;

    ProcessDefinitionService processDefinitionService;

    ModelService modelService;


    @Before
    public void setUp() throws Exception {
        processDefinitionService = new ProcessDefinitionService(processEngine);
        deploymentService = new DeploymentService(processEngine);
        modelService = new ModelService(processEngine);
    }

    @Test
    public void deploy(){
        ModelQuery query = this.modelService.createQuery();
        query.modelNameLike("%wl%");
        long count = query.count();
        Assert.assertTrue("deploy null model", count > 0);
        List<Model> models = query.listPage(0, 10);
        Model model = models.get(0);
        log.info("deploy model:id={},name={},key={}", model.getId(), model.getName(), model.getKey());
        Deployment deployment = this.deploymentService.deploy(model);
        ProcessDefinition processDefinition = this.processDefinitionService.getOneByDeploymentId(deployment.getId());
        log.info("deploy deployment:id={}", deployment.getId());
        log.info("deploy processDefinition:id={}", processDefinition.getId());
    }

    @Test
    public void delete(){
        this.deploymentService.delete("82501");
        log.info("");
    }


    @After
    public void tearDown() throws Exception {


    }
}
