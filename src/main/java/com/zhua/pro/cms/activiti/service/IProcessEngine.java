package com.zhua.pro.cms.activiti.service;

import com.zhua.pro.cms.activiti.entity.FlowData;

import java.util.Map;

/**
 */
public interface IProcessEngine {

    /**
     * 启动流程
     * @param deploymentId 流程发布ID
     * @return 活动任务ID
     */
    String start(String deploymentId);


    /**
     * 启动流程（带业务ID）
     * @param deploymentId 流程发布ID
     * @param businessId 业务ID
     * @return 活动任务ID
     */
    String start(String deploymentId, String businessId);

    /**
     * 启动流程（带业务ID）
     * @param deploymentId 流程发布ID
     * @param businessId 业务ID
     * @param startUser 流程发起人
     * @return 活动任务ID
     */
    String start(String deploymentId, String businessId, String startUser);

    /**
     * 提交任务
     * @param flowVariables 流程表单流转数据
     */
    void submitTask(Map<String, Object> flowVariables);

    /**
     * 回退首环节
     * @param taskId 任务ID
     */
    void backToFirstNode(String taskId);

    /**
     * 回退上一环节
     * @param taskId 任务ID
     */
    void backToPreNode(String taskId, String comment);

    /**
     * 回退某环节
     * @param taskId 任务ID
     * @param targetNodeId 目标节点ID
     */
    void back2Node(String taskId, String targetNodeId);

    /**
     * 启动并提交流程
     * @param deploymentId
     * @param flowData
     */
    void startAndSubmit(String deploymentId, FlowData flowData);

    /**
     * 获取历史用户任务流转数据
     * @param processInstanceId 流程实例ID
     * @return map
     */
    Map<String, Object> getHisUserTaskVariables(String processInstanceId);

    /**
     * 获取当前用户任务流转数据
     * @param taskId 当前任务ID
     * @return map
     */
    Map<String, Object> getCurrentUserTaskVariables(String taskId);

    /**
     * 获取任务流转数据
     * @param taskId 当前任务ID
     * @return map
     */
    Map<String, Object> getTaskVariables(String taskId);

    /**
     * 撤回
     * @param taskId
     */
    void withdraw(String taskId) throws Exception;

    /**
     * 通过指令方式进行撤回
     * @param oldTaskId
     */
    void withdrawApproval(String oldTaskId);
}
