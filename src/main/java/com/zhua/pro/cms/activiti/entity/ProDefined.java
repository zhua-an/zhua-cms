package com.zhua.pro.cms.activiti.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Describe: 流程定义实体
 * */
@Data
public class ProDefined implements Serializable {

    /**
     * 流程定义编号
     * */
    private String id;

    /**
     * 流程定义名称
     * */
    private String name;

    /**
     * 流程定义标识
     * */
    private String key;

    /**
     * 分类
     */
    private String category;

    /**
     * 流程定义版本
     * */
    private int version;

    /**
     * xml 资源文件名称
     * */
    private String bpmn;

    /**
     * png 资源文件名称
     * */
    private String png;

    /**
     * 部署 Id
     * */
    private String deploymentId;

    /**
     * 部署时间
     */
    private Date deploymentTime;

    /**
     * 是否挂起
     */
    private boolean isSuspended;

}
