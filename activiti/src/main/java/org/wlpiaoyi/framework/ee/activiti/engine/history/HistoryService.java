package org.wlpiaoyi.framework.ee.activiti.engine.history;

import com.google.gson.Gson;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.task.Comment;
import org.wlpiaoyi.framework.ee.activiti.entity.history.HistoricActivityInstanceEntityImpl;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.util.*;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/14 12:33
 * {@code @version:}:       1.0
 */
public class HistoryService extends BaseService{
    public HistoryService(ProcessEngine processEngine) {
        super(processEngine);
    }

    public List<HistoricActivityInstance> history(String progressInstanceId){
        List<HistoricActivityInstance> historicActivityInstances = this.execService.createHistoricActivityInstanceQuery()
                .processInstanceId(progressInstanceId).orderByHistoricActivityInstanceStartTime().asc().
                orderByHistoricActivityInstanceEndTime().asc().list();
        TaskService taskService = this.processEngine.getTaskService();
        List<Comment> comments = taskService.getProcessInstanceComments(progressInstanceId);
        Map<String, VariableInstance> variables = taskService.getVariableInstances(historicActivityInstances.get(historicActivityInstances.size() - 1).getTaskId());
        Gson gson = GsonBuilder.gsonDefault();
        for (HistoricActivityInstance entity : historicActivityInstances){
            HistoricActivityInstanceEntityImpl entityImpl = gson.fromJson(gson.toJsonTree(entity), HistoricActivityInstanceEntityImpl.class);
            historicActivityInstances.set(historicActivityInstances.indexOf(entity), entityImpl);
            entityImpl.setComments(new ArrayList<>());
            entityImpl.setVariables(new ArrayList<>());
            for(Comment comment : comments){
                if(!comment.getTaskId().equals(entity.getTaskId())){
                    continue;
                }
                entityImpl.getComments().add(comment);
            }
            comments.removeAll(entityImpl.getComments());
//            for(VariableInstance variable : variables){
//                if(!variable.getTaskId().equals(entity.getTaskId())){
//                    continue;
//                }
//                entityImpl.getVariables().add(variable);
//            }
//            variables.removeAll(entityImpl.getVariables());
        }
        return historicActivityInstances;
    }

}
