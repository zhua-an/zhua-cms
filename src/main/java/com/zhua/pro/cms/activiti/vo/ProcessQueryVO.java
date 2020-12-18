package com.zhua.pro.cms.activiti.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProcessQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String modelName;
    private String processName;
    private String taskName;
    private String titleName;
    private String key;
    private Integer status;
    private Date createTime;

}
