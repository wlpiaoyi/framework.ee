package org.wlpiaoyi.framework.ee.activiti.engine.task;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.query.Query;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/9 21:58
 * {@code @version:}:       1.0
 */
public class TaskService extends BaseService{
    public TaskService(ProcessEngine processEngine) {
        super(processEngine);
    }

    public TaskQuery createQuery() {
        TaskQuery query = this.createQuery(TaskQuery.class);
        return query;
    }

    public List<Task> getCurrentTasks(String processInstanceId){
        return this.getCurrentTasks(processInstanceId, null, null);
    }

    /**
     * 获取当前任务
     * @param processInstanceId
     * @param user
     * @param group
     * @return
     */
    public List<Task> getCurrentTasks(String processInstanceId, String user, String group){
        TaskQuery query = this.createQuery();
        query.processInstanceId(processInstanceId);
        if(ValueUtils.isNotBlank(user)){
            query.taskCandidateUser(user);
        }
        if(ValueUtils.isNotBlank(group)){
            query.taskCandidateGroup(group);
        }
        return query.list();
    }

    public Task getCurrentTask(String processInstanceId){
        return this.getCurrentTask(processInstanceId, null, null);
    }
    /**
     * 获取当前任务
     * @param processInstanceId
     * @param user
     * @param group
     * @return
     */
    public Task getCurrentTask(String processInstanceId, String user, String group){
        TaskQuery query = this.createQuery();
        query.processInstanceId(processInstanceId);
        if(ValueUtils.isNotBlank(user)){
            query.taskCandidateUser(user);
        }
        if(ValueUtils.isNotBlank(group)){
            query.taskCandidateGroup(group);
        }
        List<Task> tasks = query.listPage(0, 1);
        if(ValueUtils.isBlank(tasks)) {
            return null;
        }
        return tasks.get(0);
    }

    /**
     * 签收任务
     * @param taskId
     * @param user
     */
    @Transactional(rollbackFor = Exception.class)
    public void claim(String taskId, String user){
        this.execService.claim(taskId, user);
    }

    /**
     * 取消签收
     * @param taskId
     */
    @Transactional(rollbackFor = Exception.class)
    public void unclaim(String taskId){
        this.execService.unclaim(taskId);
    }

    /**
     * 完成任务
     * @param taskId
     * @param user
     * @param comment
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId, String user,String comment){
        this.complete(taskId, user, null, new HashMap(){{
            put("DEFAULT", comment);
        }}, null);
    }

    /**
     * 完成任务
     * @param taskId
     * @param user
     * @param commentMap
     * @param variables
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId, String user,
                         Map<String, String> commentMap,
                         Map<String, ? extends Object> variables){
        Task task = this.createQuery().taskId(taskId).singleResult();
        if(ValueUtils.isBlank(task)){
            throw new BusinessException("not find task");
        }
        if(ValueUtils.isBlank(task.getAssignee())){
            throw new BusinessException("task must claim before do complete");
        }
        if(!task.getAssignee().equals(user)){
            throw new BusinessException("task assignee must equals user");
        }
        if(ValueUtils.isBlank(task.getProcessInstanceId())){
            throw new BusinessException("processInstanceId is null");
        }
        this.complete(taskId, user, task.getProcessInstanceId(), commentMap, variables);
    }

    /**
     * 完成任务
     * @param taskId
     * @param user
     * @param processInstanceId
     * @param commentMap
     * @param variables
     */
    @Transactional(rollbackFor = Exception.class)
    protected void complete(String taskId,
                         String user,
                         String processInstanceId,
                         Map<String, String> commentMap,
                         Map<String, ? extends Object> variables){
        Task task = this.execService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null){
            throw new BusinessException(510, "没有找到指定的任务");
        }
        if(ValueUtils.isBlank(task.getAssignee())){
            throw new BusinessException("该任务没有签收");
        }
        if(!task.getAssignee().equals(user)){
            throw new BusinessException("任务签收人不对");
        }
        if(ValueUtils.isNotBlank(processInstanceId) && ValueUtils.isNotBlank(commentMap)){
            for(Map.Entry<String, String> entry : commentMap.entrySet()){
                this.execService.addComment(task.getId(), processInstanceId, entry.getKey(), entry.getValue());
            }
        }
        if(ValueUtils.isNotBlank(variables)){
            this.execService.setVariables(taskId, variables);
        }
        this.execService.complete(taskId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void stopProcess(String taskId,
                            String user,
                            Map<String, String> commentMap,
                            Map<String, ? extends Object> variables) {
        //  当前任务
        Task task = this.createQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = this.processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        this.claim(taskId, user);
        //  完成当前任务
        this.complete(task.getId(), user, null, null);

    }

    public List<Comment> getCommentByProgress(String progressInstanceId){
        return this.execService.getProcessInstanceComments(progressInstanceId);
    }
    public List<VariableInstance> getVariableInstancesLocalByTaskIds(Set<String> taskIds){
        return this.execService.getVariableInstancesLocalByTaskIds(taskIds);
    }


}
