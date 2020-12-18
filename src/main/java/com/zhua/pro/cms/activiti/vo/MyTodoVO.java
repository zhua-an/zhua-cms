package com.zhua.pro.cms.activiti.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Create by Kalvin on 2020/4/22.
 */

@Data
@ToString
@Accessors(chain = true)
public class MyTodoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String originalAssignee;
    private String owner;
    private String assignee;
    private String processInstanceId;
    private String executionId;
    private Date createTime;
    private Integer suspensionState;    // 1：激活；2：挂起
    private Integer processDefinitionVersion;

    private String processName;
    private String startUser;
}
