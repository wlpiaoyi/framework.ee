package org.wlpiaoyi.framework.ee.activiti.engine.history;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.wlpiaoyi.framework.ee.activiti.engine.runtime.ProcessInstanceService;
import org.wlpiaoyi.framework.ee.activiti.engine.task.TaskService;
import org.wlpiaoyi.framework.ee.activiti.utils.ActivitiUtils;

import java.util.HashMap;
import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/14 13:17
 * {@code @version:}:       1.0
 */
@Slf4j
public class HistoryServiceTest {

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

    TaskService taskService;

    ProcessInstanceService processInstanceService;

    HistoryService historyService;

    @Before
    public void setUp() throws Exception {
        processInstanceService = new ProcessInstanceService(processEngine);
        historyService = new HistoryService(processEngine);
        taskService = new TaskService(processEngine);
    }

    @Test
    public void history(){
        ProcessInstance processInstance = processInstanceService.startProcess("wl_key_6438:1:70003");
        log.info("processInstance:{}", processInstance);
        List<Task> tasks = this.taskService.getCurrentTasks(processInstance.getId());
        log.info("tasks:{}", tasks);
        this.taskService.claim(tasks.get(0).getId(), "2");
        this.taskService.complete(tasks.get(0).getId(), "2", new HashMap(){{
            put("DEFAULT", "comment+1");
        }}, new HashMap(){{
            put("var1", "value1_1");
            put("var2", "value1_2");
        }});
        tasks = this.taskService.getCurrentTasks(processInstance.getId());
        this.taskService.claim(tasks.get(0).getId(), "2");
        this.taskService.complete(tasks.get(0).getId(), "2", new HashMap(){{
            put("DEFAULT", "comment+2");
        }}, new HashMap(){{
            put("var1", "value2_1");
            put("var3", "value2_3");
        }});
        tasks = this.taskService.getCurrentTasks(processInstance.getId());
        List<HistoricActivityInstance> historicActivityInstances = this.historyService.history(processInstance.getProcessInstanceId());
        log.info("historicActivityInstances:{}", historicActivityInstances);
    }



    @After
    public void tearDown() throws Exception {

    }


}
