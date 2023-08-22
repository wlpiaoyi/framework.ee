package org.wlpiaoyi.framework.ee.activiti.engine.runtime;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.wlpiaoyi.framework.ee.activiti.utils.ActivitiUtils;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/9 11:33
 * {@code @version:}:       1.0
 */
@Slf4j
public class ProcessInstanceServiceTest {
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

    ProcessInstanceService processInstanceService;

    @Before
    public void setUp() throws Exception {
        processInstanceService = new ProcessInstanceService(processEngine);
    }

    @Test
    public void startProcess(){
        ProcessInstance processInstance = processInstanceService.startProcess("wl_key_6438:1:70003");
        this.processInstanceService.suspendProcessInstanceById(processInstance.getId());
        processInstance = this.processInstanceService.getOne(processInstance.getId());
        Assert.assertTrue("suspendProcess faild",
                ((ExecutionEntityImpl) processInstance).getSuspensionState() == 2);
        this.processInstanceService.activateProcessInstanceById(processInstance.getId());
        processInstance = this.processInstanceService.getOne(processInstance.getId());
        Assert.assertTrue("activateProcess faild",
                ((ExecutionEntityImpl) processInstance).getSuspensionState() == 1);

        this.processInstanceService.deleteProcessInstance(processInstance.getId(), "delete msg");
        processInstance = this.processInstanceService.getOne(processInstance.getId());
        Assert.assertTrue("deleted faild",
                processInstance == null);
        log.info("");
    }



    @After
    public void tearDown() throws Exception {

    }


}
