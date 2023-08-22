package org.wlpiaoyi.framework.ee.activiti.engine.task;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.wlpiaoyi.framework.ee.activiti.engine.history.HistoryService;
import org.wlpiaoyi.framework.ee.activiti.engine.runtime.ProcessInstanceService;
import org.wlpiaoyi.framework.ee.activiti.utils.ActivitiUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.HashMap;
import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/10 19:50
 * {@code @version:}:       1.0
 */
@Slf4j
public class TaskServiceTest {
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
    public void todo(){
        ProcessInstance processInstance = processInstanceService.startProcess("wl_key_6438:1:70003");
        log.info("processInstance:{}", processInstance);
        TaskQuery query = this.taskService.createQuery();
//        query.taskCandidateUser("1_1");
        List<Task> tasks = query.list();
        log.info("tasks:{}", tasks);
        this.processInstanceService.deleteProcessInstance(processInstance.getId(), "delete msg");
        processInstance = this.processInstanceService.getOne(processInstance.getId());
    }



    @Test
    public void currentTask(){
        ProcessInstance processInstance = processInstanceService.startProcess("wl_key_6438:1:70003");
        log.info("processInstance:{}", processInstance);
        List<Task> tasks = this.taskService.getCurrentTasks(processInstance.getId());
        log.info("tasks:{}", tasks);
        this.processInstanceService.deleteProcessInstance(processInstance.getId(), "delete msg");
        processInstance = this.processInstanceService.getOne(processInstance.getId());

    }

    @Test
    public void complete(){
        ProcessInstance processInstance = processInstanceService.startProcess("wl_key_6438:1:70003");
        log.info("processInstance:{}", processInstance);
        List<Task> tasks = this.taskService.getCurrentTasks(processInstance.getId());
        log.info("tasks:{}", tasks);
        this.taskService.claim(tasks.get(0).getId(), "2");
        this.taskService.complete(tasks.get(0).getId(), "2", new HashMap(){{
            put("DEFAULT", "comment+1");
        }}, new HashMap(){{
            put("var1", "value1");
        }});
        tasks = this.taskService.getCurrentTasks(processInstance.getId());
        this.taskService.claim(tasks.get(0).getId(), "2");
        this.taskService.complete(tasks.get(0).getId(), "2", new HashMap(){{
            put("DEFAULT", "comment+2");
        }}, new HashMap(){{
            put("var1", "value2");
        }});
        tasks = this.taskService.getCurrentTasks(processInstance.getId());
        log.info("tasks size:{}", tasks.size());
//        this.taskService.getCommentByProgress(processInstance.getProcessInstanceId());
//        this.taskService.getva



    }
    @Test
    public void stopProgress(){
        ProcessInstance processInstance = processInstanceService.startProcess("wl_key_6438:1:70003");
        log.info("processInstance:{}", processInstance);
        List<Task> tasks = this.taskService.getCurrentTasks(processInstance.getId());
        log.info("tasks:{}", tasks);
        this.taskService.claim(tasks.get(0).getId(), "2");
        this.taskService.complete(tasks.get(0).getId(), "2", "comment test");
        tasks = this.taskService.getCurrentTasks(processInstance.getId());
        this.taskService.stopProcess(tasks.get(0).getId(), "2", null, null);
        tasks = this.taskService.getCurrentTasks(processInstance.getId());
        log.info("tasks size:{}", tasks.size());

    }



    @After
    public void tearDown() throws Exception {

    }

}
