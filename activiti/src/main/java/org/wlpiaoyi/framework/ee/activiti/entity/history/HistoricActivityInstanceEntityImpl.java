package org.wlpiaoyi.framework.ee.activiti.entity.history;

import lombok.Data;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.task.Comment;

import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/14 13:47
 * {@code @version:}:       1.0
 */
@Data
public class HistoricActivityInstanceEntityImpl extends org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl {

    private List<Comment> comments;

    private List<VariableInstance> variables;

}
