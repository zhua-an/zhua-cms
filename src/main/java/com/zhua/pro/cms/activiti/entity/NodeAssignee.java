package com.zhua.pro.cms.activiti.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 流程实例每个实例任务节点审批人
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class NodeAssignee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String nodeId;
    private String assignee;

}
