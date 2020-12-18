package com.zhua.pro.cms.activiti.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 流程节点数据
 */
@Data
@Accessors(chain = true)
public class ProcessNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nodeId;
    private String nodeName;
    private String assignee;
    private String candidateUserIds;
    private String candidateGroupIds;

    public ProcessNode(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }
}
