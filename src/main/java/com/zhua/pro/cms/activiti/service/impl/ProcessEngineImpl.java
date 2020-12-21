package com.zhua.pro.cms.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.zhua.pro.cms.activiti.entity.FlowData;
import com.zhua.pro.cms.activiti.entity.NodeAssignee;
import com.zhua.pro.cms.activiti.entity.ProcessNode;
import com.zhua.pro.cms.activiti.exception.ActException;
import com.zhua.pro.cms.activiti.service.IProcessEngine;
import com.zhua.pro.cms.activiti.util.ProcessKit;
import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.util.StringUtils;
import com.zhua.pro.cms.vo.LoginUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName ProcessEngineImpl
 * @Description TODO
 * @Author zhua
 * @Date 2020/12/9 15:37
 * @Version 1.0
 */
@Slf4j
@Service
public class ProcessEngineImpl implements IProcessEngine {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private IdentityService identityService;

    @Override
    public String start(String deploymentId) {
        return this.start(deploymentId, null, null);
    }

    @Override
    public String start(String deploymentId, String businessId) {
        return this.start(deploymentId, businessId, null);
    }

    @Override
    public String start(String deploymentId, String businessId, String startUser) {
        // 流程流转任务变量集合
        final HashMap<String, Object> taskVariables = new HashMap<>();
        final FlowData flowData = new FlowData();

        if (StrUtil.isBlank(deploymentId)) {
            throw new ActException("流程发布ID不允许为空");
        }
        if (StrUtil.isBlank(startUser)) {
            // 获取当前登录用户
            startUser = ShiroUtils.getUser().getUsername();
        }

        //保存业务数据
        flowData.setStartUser(startUser);
        flowData.setCurrentUser(startUser);
        flowData.setFirstNode(true);
        flowData.setFirstSubmit(true);
        //设置流程执行人 todo: 如果开启流程后不提交需要设置当前执行人
        //flowData.setNextUser(startUser);
        taskVariables.put(ProcessKit.FLOW_STARTUSER_KEY, startUser);
        taskVariables.put(ProcessKit.FLOW_DATA_KEY, flowData);

        // 设置当前任务的办理人  // TODO: 不知是否有用？
        Authentication.setAuthenticatedUserId(startUser);

        ProcessDefinition processDefinition = ProcessKit.getProcessDefinition(deploymentId);
        if(processDefinition == null) {
            throw new ActException("该流程已被删除或不存在，请刷新页面后重新申请");
        }
        if (processDefinition.isSuspended()) {
            throw new ActException("该流程已被挂起，无法使用，请激活后使用");
        }
        String processDefinitionId = processDefinition.getId();

        ProcessInstance processInstance;
        if (StrUtil.isBlank(businessId)) {
            processInstance =  runtimeService.startProcessInstanceById(processDefinitionId, taskVariables);
        } else {
            // TODO: 使用业务表启动流程
            processInstance =  runtimeService.startProcessInstanceById(processDefinitionId, businessId, taskVariables);
        }

        // 设置流程启动后的相关核心变量
        String processName = processDefinition.getName();
        String processInstanceId = processInstance.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        String taskId = task.getId();

        // 设置流程实例名称
        runtimeService.setProcessInstanceName(processInstanceId, processName);

        flowData.setProcessName(processName);
        flowData.setProcessDefinitionId(processDefinitionId);
        flowData.setProcessInstanceId(processInstanceId);
        flowData.setFirstNodeId(task.getTaskDefinitionKey());
        flowData.setCurrentNodeId(task.getTaskDefinitionKey());
        flowData.setCurrentNodeName(task.getName());
        flowData.setTaskId(taskId);
        flowData.setExecutionId(task.getExecutionId());
        // 储存流程核心流转变量
        taskVariables.put(ProcessKit.FLOW_DATA_KEY, flowData);
        // 储存流程表单流转数据
        taskVariables.put(ProcessKit.FLOW_VARIABLES_KEY, new HashMap<>());
        //设置流程执行人 todo: 如果开启流程后不提交需要设置当前执行人
        //taskService.setAssignee(taskId, startUser);
        taskService.setVariables(taskId, taskVariables);
        log.debug("用户【{}】启动流程【{}】实例【{}】", startUser, processName, processInstance.getId());
        return taskId;
    }

    /**
     * 更新流程相关核心变量
     * @param flowData
     */
    public void updateTaskFlowData(FlowData flowData) {
        Task task = taskService.createTaskQuery().processInstanceId(flowData.getProcessInstanceId()).singleResult();
        if(task == null) {
            //流程结束
            return ;
        }
        String taskId = task.getId();
        Map<String, Object> taskVariables = taskService.getVariables(taskId);
        flowData.setCurrentNodeId(task.getTaskDefinitionKey());
        flowData.setCurrentNodeName(task.getName());
        if(flowData.getFirstNodeId().equals(flowData.getCurrentNodeId())) {
            flowData.setFirstNode(true);
        }
        flowData.setTaskId(taskId);
        flowData.setExecutionId(task.getExecutionId());
        // 储存流程核心流转变量
        taskVariables.put(ProcessKit.FLOW_DATA_KEY, flowData);
        //设置流程执行人
        if(StringUtils.isNotBlank(flowData.getNextUser())) {
            taskService.addCandidateUser(taskId, flowData.getNextUser());
        }
        taskService.setVariables(taskId, taskVariables);
    }

    @Transactional
    @Override
    public void submitTask(Map<String, Object> flowVariables) {
        final Map<String, Object> variables = new HashMap<>();
        final String currentUser = ShiroUtils.getUser().getUsername();
        final FlowData flowData = new FlowData();
        BeanUtil.copyProperties(flowVariables, flowData);

        // 下一节点审批人
        final String nextUser = flowData.getNextUser();
        final String comment = StrUtil.isBlank(flowData.getComment()) ? ProcessKit.DEFAULT_AGREE_COMMENT : flowData.getComment();
        final String taskId = flowData.getTaskId();
        final String processInstanceId = flowData.getProcessInstanceId();
        final int nextNodeNum = flowData.getNextNodeNum();
        final Task currentTask = ProcessKit.getCurrentTask(taskId);
        final String currentNodeId = currentTask.getTaskDefinitionKey();

        if (StrUtil.isBlank(processInstanceId)) {
            throw new ActException("流程实例ID不允许为空");
        }

        /*if (StrUtil.isBlank(nextUser) && nextNodeNum != 0) {
            throw new ActException("下一环节审批人不允许为空");
        }*/

        if (flowData.isFirstSubmit()) {
            flowData.setFirstSubmitTime(new Date());
        }
        flowData.setFirstNode(false);
        flowData.setFirstSubmit(false);

        // 记录每个实例任务节点审批人
        HashMap<String, NodeAssignee> nodeAssignee = flowData.getNodeAssignee();
        if (nodeAssignee == null) {
            nodeAssignee = new HashMap<>();
        }
        nodeAssignee.put(currentNodeId + "_" + taskId, new NodeAssignee(taskId, currentNodeId, currentUser));
        flowData.setNodeAssignee(nodeAssignee);

        variables.put(ProcessKit.FLOW_OUTCOME_KEY, flowData.getOutcome());
        variables.put(ProcessKit.FLOW_VARIABLES_KEY, flowVariables);
        variables.put(ProcessKit.FLOW_DATA_KEY, flowData);
        variables.put(ProcessKit.FORM_CONFIG_KEY, flowData.getFormInfo());

        if(flowData.getVariables() != null && flowData.getVariables().size() > 0) {
            variables.putAll(flowData.getVariables());
        }

        String commentTemp = "[" + (StrUtil.isBlank(flowData.getOutcome()) ? ProcessKit.DEFAULT_AGREE_COMMENT : flowData.getOutcome()) + "]" + comment;

        if (nextNodeNum == 0 || nextNodeNum == 1) {
            // 正常提交任务
            // 添加审批意见
            taskService.addComment(taskId, processInstanceId, commentTemp);

            // TODO: owner不为空说明可能存在委托任务
            /*if (StrUtil.isNotBlank(currentTask.getOwner())) {
                DelegationState delegationState = currentTask.getDelegationState();
                // 把被委托人代理处理后的任务交回给委托人
                if (DelegationState.PENDING == delegationState) {
                    taskService.resolveTask(currentTask.getId());
                }
            }*/
//            if(StringUtils.isNotBlank(nextUser)) {
//                Authentication.setAuthenticatedUserId(nextUser);
//            }

            // 先置空，再设置。否则提交完任务后，历史任务表不会保存审批人。可能是activiti6的bug的吧？
//            taskService.setAssignee(taskId, "");
            taskService.setAssignee(taskId, currentUser);

            // 正式提交任务
            taskService.complete(taskId, variables);
        } else if (nextNodeNum > 1) {
            ProcessKit.nodeJumpTo(taskId, flowData.getTargetNodeId(), currentUser, variables, commentTemp);
        } else {
            throw new ActException("提交任务异常");
        }

        //设置下一任务审批人
        this.updateTaskFlowData(flowData);
    }

    @SneakyThrows
    @Transactional
    @Override
    public void startAndSubmit(String deploymentId, FlowData flowData) {
        LoginUser currentUser = ShiroUtils.getUser();
        String taskId = this.start(deploymentId,  null, null);

        Map<String, Object> currentTaskVariables = this.getTaskVariables(taskId);
        if(flowData.getVariables() != null && flowData.getVariables().size() > 0) {
            currentTaskVariables.putAll(flowData.getVariables());
        }

        FlowData flowDataT = ProcessKit.getFlowData(currentTaskVariables);
        flowDataT.setTheme(flowData.getTheme());
        if(flowDataT.getNextNodeNum() <= 1) {
            if (flowDataT.isFirstSubmit()) {
                flowDataT.setFirstSubmitTime(new Date());
            }
            flowDataT.setFirstNode(false);
            flowDataT.setFirstSubmit(false);
            if(StringUtils.isNotBlank(flowData.getNextUser())) {
                flowDataT.setNextUser(flowData.getNextUser());
            }
        }
        flowDataT.setVariables(flowData.getVariables());
        flowDataT.setFormInfo(flowData.getFormInfo());
        currentTaskVariables.put(ProcessKit.FLOW_DATA_KEY, flowDataT);
        currentTaskVariables.put(ProcessKit.FORM_CONFIG_KEY, flowDataT.getFormInfo());

        if(flowDataT.getNextNodeNum() <= 1) {
            taskService.setAssignee(taskId, currentUser.getUsername());
            // 正式提交任务
            taskService.complete(taskId, currentTaskVariables);

            //设置下一任务审批人
            this.updateTaskFlowData(flowDataT);
        }else {
            taskService.setVariables(taskId, currentTaskVariables);
        }

    }

    @Transactional
    @Override
    public void backToFirstNode(String taskId) {
        final String currentUser = ShiroUtils.getUser().getUsername();
        Map<String, Object> variables = taskService.getVariables(taskId);
        FlowData flowData = ProcessKit.getFlowData(variables);
        flowData.setFirstNode(true);
        flowData.setNextUser(flowData.getStartUser());
        ProcessKit.nodeJumpTo(taskId, flowData.getFirstNodeId(), currentUser, variables, "[驳回]回退首环节");
        //设置下一任务审批人
        this.updateTaskFlowData(flowData);
    }

    @Transactional
    @Override
    public void backToPreNode(String taskId, String comment) {
        final String currentUser = ShiroUtils.getUser().getUsername();
        Map<String, Object> variables = taskService.getVariables(taskId);
        FlowData flowData = ProcessKit.getFlowData(variables);
        Task currentTask = ProcessKit.getCurrentTask(taskId);
        // 获取上一环节
        ProcessNode preOneIncomeNode = ProcessKit.getPreOneIncomeNode(currentTask.getTaskDefinitionKey(), flowData.getProcessDefinitionId());
        if (preOneIncomeNode == null) {
            throw new ActException("驳回失败，preOneIncomeNode空指针异常");
        }
        String preNodeId = preOneIncomeNode.getNodeId();
        // 如果是首环节
        if (preNodeId.equals(flowData.getFirstNodeId())) {
            flowData.setFirstNode(true);
            flowData.setNextUser(flowData.getStartUser());
        } else {
            // 获取目标节点审批人
            int taskType = ProcessKit.getTaskType(preNodeId, flowData.getProcessDefinitionId());
            if (taskType == ProcessKit.USER_TASK_TYPE_NORMAL) {
                List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(flowData.getProcessInstanceId())
                        .activityId(preNodeId).orderByHistoricActivityInstanceEndTime().desc().finished().list();
                if(historicActivityInstanceList == null || historicActivityInstanceList.size() == 0) {
                    throw new ActException("获取上一级任务审批人信息失败");
                }
                String assignee = historicActivityInstanceList.get(0).getAssignee();
                flowData.setNextUser(assignee);
            } else {
                throw new ActException("目前不支持多任务实例驳回");
            }
        }
        //驳回上环节
        ProcessKit.nodeJumpTo(taskId, preNodeId, currentUser, variables, "[驳回]"+comment);

        //设置下一任务审批人
        this.updateTaskFlowData(flowData);
    }

    @Transactional
    @Override
    public void back2Node(String taskId, String targetNodeId) {
        final String currentUser = ShiroUtils.getUser().getUsername();
        Map<String, Object> variables = taskService.getVariables(taskId);
        FlowData flowData = ProcessKit.getFlowData(variables);
        // 如果是首环节
        if (targetNodeId.equals(flowData.getFirstNodeId())) {
            flowData.setFirstNode(true);
            flowData.setNextUser(flowData.getStartUser());
        } else {
            // 获取目标节点审批人
            int taskType = ProcessKit.getTaskType(targetNodeId, flowData.getProcessDefinitionId());
            if (taskType == ProcessKit.USER_TASK_TYPE_NORMAL) {
                HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(flowData.getProcessInstanceId()).activityId(targetNodeId).finished().singleResult();
                String assignee = historicActivityInstance.getAssignee();
                flowData.setNextUser(assignee);
            } else {
                throw new ActException("目前不支持多任务实例驳回");
            }
        }
        FlowElement flowElement = ProcessKit.getFlowElement(targetNodeId, flowData.getProcessDefinitionId());
        ProcessKit.nodeJumpTo(taskId, targetNodeId, currentUser, variables, "[驳回]驳回【" + flowElement.getName() + "】环节");

        //设置下一任务审批人
        this.updateTaskFlowData(flowData);
    }

    @Override
    public Map<String, Object> getHisUserTaskVariables(String processInstanceId) {
        final Map<String, Object> hashMap = new HashMap<>();

        // 获取当前节点表单数据
//        final Map<String, Object> flowVariables = ProcessKit.getHisFlowVariables(processInstanceId);
        final FlowData flowData = ProcessKit.getHisFlowData(processInstanceId);
        // 设置只读
        flowData.setReadOnly(true);

        /*
         * 当前流程绑定的表单代号。
         * 如果在流转过程中把流程绑定的表单移除掉或者更换了表单，它不会使当前流程实例生效。依旧使用的是启动时绑定的表单
         */
        hashMap.put(ProcessKit.FLOW_DATA_KEY, flowData);
        hashMap.put(ProcessKit.FORM_CONFIG_KEY, flowData.getFormInfo());

        log.debug("his hashMap={}", hashMap);
        return hashMap;
    }

    @Override
    public Map<String, Object> getCurrentUserTaskVariables(String taskId) {
        final Map<String, Object> hashMap = new HashMap<>();
        final String currentUser = ShiroUtils.getUser().getUsername();
        Task currentTask = ProcessKit.getCurrentUserTask(taskId);
        if (currentTask == null) {
            // TODO: 流程完结或当前用户没有权限
            return null;
        }

        // 获取当前节点表单数据
        final Map<String, Object> flowVariables = ProcessKit.getFlowVariables(taskId);
        final FlowData flowData = ProcessKit.getFlowData(taskId);

        /*
         * 当前流程绑定的表单。
         * 如果在流转过程中把流程绑定的表单移除掉或者更换了表单，它不会使当前流程实例生效。依旧使用的是启动时绑定的表单
         */
        final String processDefinitionId = flowData.getProcessDefinitionId();
        final String currentNodeId = currentTask.getTaskDefinitionKey();
        final String currentExecutionId = currentTask.getExecutionId();
        final String currentNodeName = currentTask.getName();

        /*
         * 更新任务流转核心数据变量【FlowData】
         */
        // 如果当前任务不是首节点
        if (!flowData.isFirstNode()) {
            flowData.setFirstNode(false);
        }
        flowData.setCurrentUser(currentUser);
        flowData.setCurrentNodeId(currentNodeId);
        flowData.setCurrentNodeName(currentNodeName);
        flowData.setTaskId(taskId);
        flowData.setExecutionId(currentExecutionId);

        hashMap.put(ProcessKit.FLOW_DATA_KEY, flowData);
        hashMap.put(ProcessKit.FORM_CONFIG_KEY, flowData.getFormInfo());

        // 设置下一步路由出口用户任务节点集
        List<UserTask> nextUserTask = ProcessKit.getNextNode(processDefinitionId, currentNodeId, flowVariables);
        List<ProcessNode> processNodes = ProcessKit.convertTo(nextUserTask);
        flowData.setNextNodes(processNodes);
        flowData.setNextNodeNum(processNodes.size());

        // 设置当前环节可驳回的所有任务节点集
        List<ProcessNode> canBackNodes = ProcessKit.getCanBackNodes(currentNodeId, processDefinitionId, flowData.getProcessInstanceId());
        flowData.setCanBackNodes(canBackNodes);

        log.debug("hashMap={}", hashMap);
        return hashMap;
    }

    @Override
    public Map<String, Object> getTaskVariables(String taskId) {
        final Map<String, Object> hashMap = new HashMap<>();
        final String currentUser = ShiroUtils.getUser().getUsername();
        Task currentTask = ProcessKit.getCurrentTask(taskId);
        if (currentTask == null) {
            // TODO: 流程完结或当前用户没有权限
            return null;
        }

        // 获取当前节点表单数据
        final Map<String, Object> flowVariables = ProcessKit.getFlowVariables(taskId);
        final FlowData flowData = ProcessKit.getFlowData(taskId);

        /*
         * 当前流程绑定的表单。
         * 如果在流转过程中把流程绑定的表单移除掉或者更换了表单，它不会使当前流程实例生效。依旧使用的是启动时绑定的表单
         */
        final String processDefinitionId = flowData.getProcessDefinitionId();
        final String currentNodeId = currentTask.getTaskDefinitionKey();
        final String currentExecutionId = currentTask.getExecutionId();
        final String currentNodeName = currentTask.getName();

        /*
         * 更新任务流转核心数据变量【FlowData】
         */
        // 如果当前任务不是首节点
        if (!flowData.isFirstNode()) {
            flowData.setFirstNode(false);
        }
        flowData.setCurrentUser(currentUser);
        flowData.setCurrentNodeId(currentNodeId);
        flowData.setCurrentNodeName(currentNodeName);
        flowData.setTaskId(taskId);
        flowData.setExecutionId(currentExecutionId);

        hashMap.put(ProcessKit.FLOW_DATA_KEY, flowData);
        hashMap.put(ProcessKit.FORM_CONFIG_KEY, flowData.getFormInfo());

        // 设置下一步路由出口用户任务节点集
        List<UserTask> nextUserTask = ProcessKit.getNextNode(processDefinitionId, currentNodeId, flowVariables);
        List<ProcessNode> processNodes = ProcessKit.convertTo(nextUserTask);
        flowData.setNextNodes(processNodes);
        flowData.setNextNodeNum(processNodes.size());

        // 设置当前环节可驳回的所有任务节点集
        List<ProcessNode> canBackNodes = ProcessKit.getCanBackNodes(currentNodeId, processDefinitionId, flowData.getProcessInstanceId());
        flowData.setCanBackNodes(canBackNodes);

        log.debug("hashMap={}", hashMap);
        return hashMap;
    }

    @Override
    public void withdraw(String oldTaskId) throws Exception {
        LoginUser loginUser = ShiroUtils.getUser();
        HistoricTaskInstance historicTaskInstance = ProcessKit.getHistoricTaskInstance(oldTaskId);
        // 目标跳转节点
        final String targetNodeId = historicTaskInstance.getTaskDefinitionKey();
        final String processInstanceId = historicTaskInstance.getProcessInstanceId();
        String processDefinitionId = historicTaskInstance.getProcessDefinitionId();

        if (ProcessKit.isFinished(processInstanceId)) {
            throw new ActException("流程已结束，不能撤回");
        }

        // 判断节点是否已被提交，如已提交则不能撤回
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if(tasks==null || tasks.size() == 0) {
            throw new ActException("流程未启动或已执行完成，无法撤回");
        }else if(tasks.size() > 1) {
            throw new ActException("不支持多任务撤回");
        }
        Task currentTask = tasks.get(0);

        final Map<String, Object> variables = taskService.getVariables(currentTask.getId());

        // 计算期望节点ID
        List<UserTask> nextNodes = ProcessKit.getNextNode(processDefinitionId, targetNodeId, variables);
        if (nextNodes == null || nextNodes.size() != 1) {
            throw new ActException("撤回失败，任务已被提交");
        }

        UserTask userTask = nextNodes.get(0);
        String expectNodeId = userTask.getId();
        String currentNodeId = currentTask.getTaskDefinitionKey();
        if (!currentNodeId.equals(expectNodeId)) {
            throw new ActException("撤回失败，任务已被提交");
        }

        //判断上一任务提交者
        ProcessNode preProcessNode = ProcessKit.getPreOneIncomeNode(currentNodeId, processDefinitionId);
        if(preProcessNode == null) {
            throw new ActException("获取上一节点信息失败");
        }else if(!loginUser.getUsername().equals(preProcessNode.getAssignee())) {
            throw new ActException("该任务非当前用户提交，无法撤回");
        }

//        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //变量
//		Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
        String myActivityId = null;
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .executionId(historicTaskInstance.getExecutionId()).finished().list();
        for(HistoricActivityInstance hai : haiList) {
            if(oldTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }
        FlowNode targetFlow = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);

        Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        log.warn("------->> activityId:" + activityId);
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
        if (null == flowNode) {
            List<SubProcess> subProcessList = bpmnModel.getMainProcess().findFlowElementsOfType(SubProcess.class, true);
            for (SubProcess subProcess : subProcessList) {
                FlowElement flowElement = subProcess.getFlowElement(activityId);
                if (flowElement != null) {
                    flowNode = (FlowNode) flowElement;
                    break;
                }
            }
        }

        //如果不是同一个流程(子流程)不能驳回
        if (!(flowNode.getParentContainer().equals(targetFlow.getParentContainer()))) {
            throw new ActException("此处无法进行驳回操作");
        }

        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(flowNode.getOutgoingFlows());

        //清理活动方向
        flowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        newSequenceFlow.setId(uuid);
        newSequenceFlow.setSourceFlowElement(flowNode);//原节点
        newSequenceFlow.setTargetFlowElement(targetFlow);//目标节点
        newSequenceFlowList.add(newSequenceFlow);
        flowNode.setOutgoingFlows(newSequenceFlowList);

        Authentication.setAuthenticatedUserId(loginUser.getUsername());
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(), "撤回");
        taskService.setAssignee(currentTask.getId(), loginUser.getUsername());

        // 获取当前节点表单数据
//        final Map<String, Object> flowVariables = ProcessKit.getFlowVariables(task.getId());
        //完成任务
//        taskService.complete(task.getId(), flowVariables);
        taskService.complete(currentTask.getId());
        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);
    }

    /**
     *
     *@date   :2014-6-27 上午09:38:36
     *@return :Set
     *@userFor :获得任务中的办理候选人
     */
    private Set<User> getTaskCandidate(String taskId) {
        Set<User> users = new HashSet<>();
        List identityLinkList = taskService.getIdentityLinksForTask(taskId);
        if (identityLinkList != null && identityLinkList.size() > 0) {
            for (Iterator iterator = identityLinkList.iterator(); iterator
                    .hasNext();) {
                IdentityLink identityLink = (IdentityLink) iterator.next();
                if (identityLink.getUserId() != null) {
                    User user = getUser(identityLink.getUserId());
                    if (user != null)
                        users.add(user);
                }
                if (identityLink.getGroupId() != null) {
                    // 根据组获得对应人员
                    List<User> userList = identityService.createUserQuery()
                            .memberOfGroup(identityLink.getGroupId()).list();
                    if (userList != null && userList.size() > 0)
                        users.addAll(userList);
                }
            }

        }
        return users;
    }

    private User getUser(String userId) {
        User user = (User) identityService.createUserQuery().userId(userId)
                .singleResult();
        return user;
    }

    /**
     * 驳回任务
     * @param taskId
     * @param targetNodeId
     * @param currentUser
     * @param variables
     * @param comment
     */
    public void backTask(String taskId,String targetNodeId,String currentUser,Map<String, Object> variables, String comment) {
        Task currentTask = taskService.createTaskQuery()
                .taskId(taskId)
                .includeProcessVariables()     //节点审批信息
                .singleResult();
        if (currentTask == null) {
            throw new ActException("流程未启动或已执行完成");
        }
        if (StringUtils.isNotEmpty(currentTask.getAssignee()) && !currentUser.equals(currentTask.getAssignee())) {
            throw new ActException("当前用户不是审核人，无法进行审核");
        }

        //task.getAssignee() 获取审批人
        if (StringUtils.isEmpty(currentTask.getAssignee())) {
            Set<User> candidates = getTaskCandidate(currentTask.getId());
            if (candidates.isEmpty()) {
                throw new ActException("当前用户不是审核人，无法进行审核");
            }
            for(User user:candidates) {
                if (user.getId().equals(currentUser)) {
                    taskService.claim(currentTask.getId(), currentUser);
                    break;
                }
            }

        }

        String processDefinitionId = currentTask.getProcessDefinitionId();
        String commentTemp = "[" + ProcessKit.DEFAULT_REJECT_COMMENT + "]" + comment;

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //获取目标节点
        FlowNode targetFlow = (FlowNode) bpmnModel.getMainProcess().getFlowElement(targetNodeId);

        Execution execution = runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        log.warn("------->> activityId:" + activityId);
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
        if (null == flowNode) {
            List<SubProcess> subProcessList = bpmnModel.getMainProcess().findFlowElementsOfType(SubProcess.class, true);
            for (SubProcess subProcess : subProcessList) {
                FlowElement flowElement = subProcess.getFlowElement(activityId);
                if (flowElement != null) {
                    flowNode = (FlowNode) flowElement;
                    break;
                }
            }
        }

        //如果不是同一个流程(子流程)不能驳回
        if (!(flowNode.getParentContainer().equals(targetFlow.getParentContainer()))) {
            throw new ActException("此处无法进行驳回操作");
        }

        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(flowNode.getOutgoingFlows());

        //清理活动方向
        flowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        newSequenceFlow.setId(uuid);
        newSequenceFlow.setSourceFlowElement(flowNode);//原节点
        newSequenceFlow.setTargetFlowElement(targetFlow);//目标节点
        newSequenceFlowList.add(newSequenceFlow);
        flowNode.setOutgoingFlows(newSequenceFlowList);

        Authentication.setAuthenticatedUserId(currentUser);
        taskService.addComment(currentTask.getId(), currentTask.getProcessInstanceId(), commentTemp);
        taskService.setAssignee(currentTask.getId(), currentUser);

        // 获取当前节点表单数据
//        final Map<String, Object> flowVariables = ProcessKit.getFlowVariables(task.getId());
        //完成任务
//        taskService.complete(task.getId(), flowVariables);
        taskService.complete(currentTask.getId());
        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);
    }

    /**
     * 撤回
     * @param oldTaskId
     */
    @Override
    public void withdrawApproval(String oldTaskId) {
        final String currentUser = ShiroUtils.getUser().getUsername();
        HistoricTaskInstance historicTaskInstance = ProcessKit.getHistoricTaskInstance(oldTaskId);
        // 目标跳转节点
        final String targetNodeId = historicTaskInstance.getTaskDefinitionKey();
        final String processDefinitionId = historicTaskInstance.getProcessDefinitionId();
        final String processInstanceId = historicTaskInstance.getProcessInstanceId();

        if (ProcessKit.isFinished(processInstanceId)) {
            throw new ActException("流程已结束，不能撤回");
        }

        // 判断节点是否已被提交，如已提交则不能撤回
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (tasks.size() == 1) {
            Task currentTask = tasks.get(0);
            String currentTaskId = currentTask.getId();
            String currentNodeId = currentTask.getTaskDefinitionKey();

            final Map<String, Object> variables = taskService.getVariables(currentTaskId);
            FlowData flowData = ProcessKit.getFlowData(variables);

            UserTask userTask = null;
            // 计算期望节点ID
            List<UserTask> nextNodes = ProcessKit.getNextNode(processDefinitionId, targetNodeId, variables);
            if(nextNodes == null || nextNodes.size() == 0) {
                throw new ActException("撤回失败，任务已被提交");
            }else if(nextNodes != null && nextNodes.size() == 1) {
                userTask = nextNodes.get(0);
            }else if(nextNodes != null && nextNodes.size() > 1) {
                for(UserTask nextNode:nextNodes) {
                    if(nextNode.getId().equals(flowData.getTargetNodeId())) {
                        userTask = nextNode;
                        break;
                    }
                }
                if(userTask == null) {
                    throw new ActException("撤回失败，任务已被提交");
                }
            }else {
                throw new ActException("撤回失败，任务已被提交");
            }

            String expectNodeId = userTask.getId();
            if (!currentNodeId.equals(expectNodeId)) {
                throw new ActException("撤回失败，任务已被提交");
            }
            //判断上一任务提交者
            ProcessNode preProcessNode = ProcessKit.getPreOneIncomeNode(currentNodeId, processDefinitionId);
            if(preProcessNode == null) {
                throw new ActException("获取上一节点信息失败");
            }else if(!currentUser.equals(preProcessNode.getAssignee())) {
                throw new ActException("该任务非当前用户提交，无法撤回");
            }

            // 如果是首环节
            if (targetNodeId.equals(flowData.getFirstNodeId())) {
                flowData.setFirstNode(true);
            }
            flowData.setNextUser(flowData.getStartUser());

            // 撤回
//            ProcessKit.setNextUser(currentUser, variables);
            ProcessKit.nodeJumpTo(currentTaskId, targetNodeId, currentUser, variables, "撤回");

            this.updateTaskFlowData(flowData);
        } else if (tasks.size() > 1) {
            // TODO: 多任务节点撤回
            throw new ActException("目前暂不支持多任务撤回");
        } else {
            throw new ActException("撤回失败，任务已被提交或不存在");
        }

    }
}
