package com.zhua.pro.cms.activiti.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程表单
 */
@Data
public class FlowForm implements Serializable {
    private String deploymentId;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String comment;
    private String applyUserName;
    private String applyName;
    private Date createTime;
    private Date submitTime;
}
