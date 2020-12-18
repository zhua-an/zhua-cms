package com.zhua.pro.cms.controller.activiti;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhua.pro.cms.activiti.entity.FlowData;
import com.zhua.pro.cms.activiti.entity.FlowForm;
import com.zhua.pro.cms.activiti.exception.ActException;
import com.zhua.pro.cms.activiti.ext.CustomProcessDiagramGenerator;
import com.zhua.pro.cms.activiti.service.IProcessEngine;
import com.zhua.pro.cms.activiti.service.IProcessImage;
import com.zhua.pro.cms.activiti.service.IWorkFlowService;
import com.zhua.pro.cms.activiti.util.ProcessKit;
import com.zhua.pro.cms.activiti.vo.*;
import com.zhua.pro.cms.core.PageInfo;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.util.StringUtils;
import com.zhua.pro.cms.vo.LoginUser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @ClassName LeaveBillAction
 * @Description TODO
 * @Author zhua
 * @Date 2020/12/8 11:48
 * @Version 1.0
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/workflow")
public class WorkflowAction {

    private IdentityService identityService;
    private RepositoryService repositoryService;
    private TaskService taskService;
    private HistoryService historyService;

    private IProcessEngine processEngine;
    private IWorkFlowService workFlowService;
    private IProcessImage processImage;

    /********************************** 个人流程操作 ************************************************/

    /**
     * Describe: 我的流程
     * Param: modelAndView
     * Return: 我的流程
     * */
    @GetMapping("/flow/init")
    public ModelAndView flowInit(ModelAndView modelAndView){
        modelAndView.setViewName("page/admin/activiti/flow/flow-list");
        return modelAndView;
    }

    /**
     * Describe: 获取我的流程
     * Param: modelAndView
     * Return: R
     * */
    @GetMapping("/flow/page")
    public R flowPage(PageInfo pageInfo, ProcessQueryVO processQueryVO){
        R r = new R();
        LoginUser loginUser = ShiroUtils.getUser();
        processQueryVO.setUsername(loginUser.getUsername());

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotBlank(processQueryVO.getUsername())) {
            historicProcessInstanceQuery.startedBy(processQueryVO.getUsername());
        }
        if (StrUtil.isNotBlank(processQueryVO.getProcessName())) {
            historicProcessInstanceQuery.processInstanceNameLike(ProcessKit.jointLike(processQueryVO.getProcessName()));
//            historicProcessInstanceQuery.processDefinitionName(processQueryVO.getProcessName());
        }

        historicProcessInstanceQuery.orderByProcessInstanceStartTime().desc();

        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());

        List<MyApplyVO> myApplyVOS = new ArrayList<>();
        historicProcessInstances.forEach(historicProcessInstance -> {
            MyApplyVO myApplyVO = new MyApplyVO();
            BeanUtil.copyProperties(historicProcessInstance, myApplyVO);

            String processInstanceId = historicProcessInstance.getId();
            StringBuilder currentTaskNames = new StringBuilder();
            StringBuilder currentTaskIds = new StringBuilder();

            final FlowData flowData = ProcessKit.getHisFlowData(processInstanceId);
            myApplyVO.setTheme(flowData.getTheme());

            if (historicProcessInstance.getEndTime() == null) {
                // 未提交、审批中
                myApplyVO.setProcessStatus(ProcessKit.FLOW_STATUS_RUNNING); // 流程状态。未提交、审批中、审批通过、拒绝
                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .unfinished().list();
                // 未提交
                if (flowData.isFirstNode()) {
                    myApplyVO.setProcessStatus(ProcessKit.FLOW_STATUS_NOT_SUBMIT);
                }
                // 获取当前处理任务节点名称
                list.forEach(taskInstance -> {
                    if (currentTaskIds.length() > 0) {
                        currentTaskIds.append(",");
                        currentTaskNames.append(",");
                    }
                    currentTaskIds.append(taskInstance.getId());
                    currentTaskNames.append(taskInstance.getName());
                });
            } else {
                // 审批通过、拒绝
                myApplyVO.setProcessStatus(ProcessKit.FLOW_STATUS_END); // 流程状态。未提交、审批中、审批通过、拒绝
            }

            myApplyVO.setCurrentTaskNames(currentTaskNames.toString());
            myApplyVO.setCurrentTaskIds(currentTaskIds.toString());
            myApplyVO.setSubmitTime(flowData.getFirstSubmitTime());

            myApplyVOS.add(myApplyVO);
        });
        return r.successPage(historicProcessInstanceQuery.count(), myApplyVOS);
    }

    /**
     * 流程表单页面
     * @param processInstanceId 流程实例ID
     * @return mv
     */
    @GetMapping(value = "process/{processInstanceId}/form")
    public ModelAndView processForm(@PathVariable String processInstanceId) {
        ModelAndView mv = new ModelAndView("page/admin/activiti/flow/flow_info");
        Map<String, Object> hisUserTaskVariables = processEngine.getHisUserTaskVariables(processInstanceId);
        if (hisUserTaskVariables != null) {
            FlowData flowData = ProcessKit.getFlowData(hisUserTaskVariables);
            mv.addObject("flowData", flowData);
            mv.addObject("formConfig", flowData.getFormInfo());
        } else {
            // TODO: 跳转其它页面
        }
        return mv;
    }

    /**
     * 历史审批记录
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/task/hisComment")
    public ModelAndView hisComment(String processInstanceId) {
        ModelAndView mv = new ModelAndView("page/admin/activiti/task/his_comment");

        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId, "comment");
        ArrayList<CommentVO> commentVOS = new ArrayList<>();
        for (int i = comments.size() - 1; i >= 0; i--) {
            CommentVO commentVO = new CommentVO();
            Comment comment = comments.get(i);
            BeanUtil.copyProperties(comment, commentVO);
            HistoricTaskInstance historicTaskInstance = ProcessKit.getHistoricTaskInstance(comment.getTaskId());
            commentVO.setUsername(historicTaskInstance.getAssignee());
            commentVO.setTaskName(historicTaskInstance.getName());
            commentVOS.add(commentVO);
        }

        mv.addObject("comments", commentVOS);
        mv.addObject("processInstanceId", processInstanceId);
        return mv;
    }


    /**
     * Describe: 发起流程
     * Param: modelAndView
     * Return: 发起流程
     * */
    @GetMapping("/flow/add")
    public ModelAndView flowAdd(ModelAndView modelAndView){
        modelAndView.setViewName("page/admin/activiti/flow/flow-add");
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery().orderByDeploymentId().desc().list();
        if(deploymentList == null || deploymentList.size() == 0) {
            throw new ActException("请先部署流程后再提交申请");
        }
        modelAndView.addObject("deploymentList", deploymentList);
        List<User> userList = identityService.createUserQuery().list();
        modelAndView.addObject("userList", userList);
        return modelAndView;
    }

    /**
     * 根据流程实例Id,获取实时流程图片
     *
     * @param processInstanceId
     * @param outputStream
     * @return
     */
    @GetMapping("/flow/displayFlowCurrPic")
    public void getFlowImgByInstanceId(String processInstanceId, OutputStream outputStream) {
        try {
            if (StringUtils.isEmpty(processInstanceId)) {
                log.error("processInstanceId is null");
                return;
            }
            // 获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            // 获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceId().asc().list();
            // 高亮已经执行流程节点ID集合
            List<String> highLightedActivitiIds = new ArrayList<>();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                highLightedActivitiIds.add(historicActivityInstance.getActivityId());
            }

            List<HistoricProcessInstance> historicFinishedProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).finished()
                    .list();
            ProcessDiagramGenerator processDiagramGenerator = null;
            // 如果还没完成，流程图高亮颜色为绿色，如果已经完成为红色
            if (CollectionUtils.isEmpty(historicFinishedProcessInstances)) {
                // 如果不为空，说明已经完成
//                processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
                processDiagramGenerator = new DefaultProcessDiagramGenerator();
            } else {
                processDiagramGenerator = new CustomProcessDiagramGenerator();
            }

            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 高亮流程已发生流转的线id集合
            List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstances);

            // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
            InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitiIds, highLightedFlowIds, "宋体", "微软雅黑", "黑体", null, 2.0);

            // 输出图片内容
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            log.error("processInstanceId" + processInstanceId + "生成流程图失败，原因：" + e.getMessage(), e);
        }

    }

    /**
     * 获取已经流转的线
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转： 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(targetFlowNode.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }

                    highLightedFlowIds.add(highLightedFlowId);
                }

            }

        }
        return highLightedFlowIds;
    }

    /**
     * 流程流转图，已执行节点和流程线高亮显示
     * @param processInstanceId 流程实例ID
     * @param response res
     */
    @GetMapping(value = "process/{processInstanceId}/flowChart/image.png")
    public void processFlowChartImage(@PathVariable String processInstanceId, HttpServletResponse response) {
        try {
            byte[] bytes = processImage.getFlowImgByProcInstId(processInstanceId);
            response.setContentType("image/png");
            OutputStream os = response.getOutputStream();
            os.write(bytes);
            os.close();
        } catch (Exception e) {
            throw new ActException("生成流转流程图失败：" + e.getMessage());
        }
    }

    /**
     * 发起流程
     * @param form
     * //启动流程实例，字符串"vacation"是BPMN模型文件里process元素的id
     * ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacation");
     * //流程实例启动后，流程会跳转到请假申请节点
     * Task vacationApply = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
     * //设置请假申请任务的执行人
     * taskService.setAssignee(vacationApply.getId(), req.getUserId().toString());
     *
     * //设置流程参数：请假天数和表单ID
     * //流程引擎会根据请假天数days>3判断流程走向
     * //formId是用来将流程数据和表单数据关联起来
     * Map<String, Object> args = new HashMap<>();
     * args.put("days", req.getDays());
     * args.put("formId", formId);
     *
     * //完成请假申请任务
     * taskService.complete(vacationApply.getId(), args);
     * @return
     */
    @PostMapping("/flow/startAndSubmit")
    public R startAndSubmit(FlowForm form, FlowData flowData) {
        try {
            LoginUser user = ShiroUtils.getUser();
            Map<String, Object> variables = new HashMap<>();
//            val betweenDay = DateUtil.between(Date.from( form.getStartTime().atZone( ZoneId.systemDefault()).toInstant()),
//                    Date.from( form.getEndTime().atZone( ZoneId.systemDefault()).toInstant()), DateUnit.DAY);
            val betweenDay = DateUtil.between(form.getStartTime(), form.getEndTime(), DateUnit.DAY);
            variables.put("days", betweenDay);
            form.setApplyUserName(user.getUsername());
            form.setApplyName(user.getName());
            form.setCreateTime(new Date());
            form.setSubmitTime(new Date());

            flowData.setVariables(variables);
            flowData.setFormInfo(form);
           processEngine.startAndSubmit(form.getDeploymentId(), flowData);
        }catch (Exception e) {
            e.printStackTrace();
            return R.error("发起流程异常:"+e.getLocalizedMessage());
        }

        /*val user = ShiroUtils.getUser();
        val betweenDay = DateUtil.between(Date.from( form.getStartTime().atZone( ZoneId.systemDefault()).toInstant()),
                Date.from( form.getEndTime().atZone( ZoneId.systemDefault()).toInstant()), DateUnit.DAY);
        HashMap<String, Object> paramsMap = Maps.newHashMapWithExpectedSize(2);
        if(StrUtil.isNotBlank(form.getApproveId())) {
            paramsMap.put("user", form.getApproveId());
        }
        paramsMap.put("applyUser", user.getUsername());
        paramsMap.put("days", betweenDay);
        ProcessInstance leave = runtimeService.startProcessInstanceById(form.getDeploymentId(), paramsMap);*/

        return R.success("发起流程成功");
    }

    /**
     * 提交流程
     * @param form
     * @param flowData
     * @return
     */
    @PostMapping("/flow/submit")
    public R submit(FlowForm form, FlowData flowData) {
        try {
            LoginUser user = ShiroUtils.getUser();
            Map<String, Object> variables = new HashMap<>();
            val betweenDay = DateUtil.between(form.getStartTime(), form.getEndTime(), DateUnit.DAY);
            variables.put("days", betweenDay);

            form.setApplyUserName(user.getUsername());
            form.setApplyName(user.getName());
            form.setSubmitTime(new Date());

            Map<String, Object> currentUserTaskVariables = processEngine.getCurrentUserTaskVariables(String.valueOf(flowData.getTaskId()));
            if (currentUserTaskVariables != null) {
                FlowData flowDataT = ProcessKit.getFlowData(currentUserTaskVariables);
                flowDataT.setNextUser(flowData.getNextUser());
                flowDataT.setNextGroup(flowData.getNextGroup());
                flowDataT.setCcUser(flowData.getCcUser());
                flowDataT.setOutcome(flowData.getOutcome());
                flowDataT.setTargetNodeId(flowData.getTargetNodeId());
                flowDataT.setComment(form.getComment());
                flowDataT.setFormInfo(form);
                flowDataT.setVariables(variables);
                Map map = JSONObject.parseObject(JSONObject.toJSONString(flowDataT), Map.class);
                currentUserTaskVariables.putAll(map);
            }
            processEngine.submitTask(currentUserTaskVariables);
        }catch (Exception e) {
            e.printStackTrace();
            return R.error("发起流程异常:"+e.getLocalizedMessage());
        }
        return R.success("发起流程成功");
    }

    /**
     * Describe: 待办任务
     * Param: modelAndView
     * Return: 待办任务
     * */
    @GetMapping("/task/init")
    public ModelAndView taskInit(){
        ModelAndView modelAndView = new ModelAndView("page/admin/activiti/task/task-list");
        return modelAndView;
    }

    /**
     * 待办任务
     * @param processQueryVO
     * @return
     */
    @GetMapping("/task/todoList")
    public R todoList(PageInfo<ProcessQueryVO> pageInfo, ProcessQueryVO processQueryVO) {
        val user = ShiroUtils.getUser();
        processQueryVO.setUsername(user.getUsername());

        TaskQuery taskQuery = taskService.createTaskQuery();

        if (StrUtil.isNotBlank(processQueryVO.getUsername())) {
            taskQuery.taskCandidateOrAssigned(processQueryVO.getUsername());
        }
        if (StrUtil.isNotBlank(processQueryVO.getTaskName())) {
            taskQuery.taskNameLike(ProcessKit.jointLike(processQueryVO.getTaskName()));
        }
        if (StrUtil.isNotBlank(processQueryVO.getProcessName())) {
            taskQuery.processDefinitionNameLike(ProcessKit.jointLike(processQueryVO.getProcessName()));
        }

        taskQuery.orderByTaskCreateTime().desc();

        List<MyTodoVO> myTodoVOS = new ArrayList<>();
        List<Task> taskList = taskQuery.orderByTaskCreateTime().desc().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());
        taskList.forEach(task -> {
            MyTodoVO myTodoVO = new MyTodoVO();
            BeanUtil.copyProperties(task, myTodoVO);
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            myTodoVO.setProcessName(historicProcessInstance.getName());
            myTodoVO.setStartUser(historicProcessInstance.getStartUserId());
            myTodoVO.setProcessDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion());
            myTodoVOS.add(myTodoVO);
        });

        return R.successPage(taskQuery.count(), myTodoVOS);
    }

    /**
     * 任务表单页面
     * @param taskId 任务ID
     * @return mv
     */
    @GetMapping(value = "/task/{taskId}/form")
    public ModelAndView taskForm(@PathVariable String taskId) {
        ModelAndView mv = new ModelAndView("page/admin/activiti/flow/flow_info");
        Map<String, Object> currentUserTaskVariables = processEngine.getCurrentUserTaskVariables(taskId);
        if (currentUserTaskVariables != null) {
            FlowData flowData = ProcessKit.getFlowData(currentUserTaskVariables);
            mv.addObject("flowData", flowData);
            mv.addObject("formConfig", flowData.getFormInfo());
        } else {
            // TODO: 跳转其它页面
        }
        return mv;
    }

    /**
     * 办理页面
     * @param taskId
     * @return
     */
    @GetMapping(value = "/task/{taskId}/approve")
    public ModelAndView approve(@PathVariable String taskId) {
        ModelAndView mv = new ModelAndView("page/admin/activiti/task/task-approve");
        Map<String, Object> currentUserTaskVariables = processEngine.getCurrentUserTaskVariables(taskId);
        if (currentUserTaskVariables != null) {
            FlowData flowData = ProcessKit.getFlowData(currentUserTaskVariables);
            mv.addObject("flowData", flowData);
            mv.addObject("formConfig", flowData.getFormInfo());
            //判断是否是提交人
            mv.addObject("isFirstNode", ShiroUtils.getUser().getUsername().equals(flowData.getStartUser()));
        } else {
            // TODO: 跳转其它页面
        }
        List<User> userList = identityService.createUserQuery().list();
        mv.addObject("userList", userList);
        return mv;
    }

    /**
     * 审批
     * @param flowVariables
     * @return
     */
    @PostMapping("/task/approve")
    public R confirmApprove(@RequestParam Map<String, Object> flowVariables) {
        Map<String, Object> currentUserTaskVariables = processEngine.getCurrentUserTaskVariables(String.valueOf(flowVariables.get("taskId")));
        if (currentUserTaskVariables != null) {
            FlowData flowData = ProcessKit.getFlowData(currentUserTaskVariables);
            Map map = JSONObject.parseObject(JSONObject.toJSONString(flowData), Map.class);
            map.putAll(flowVariables);
            flowVariables = map;
        }
        processEngine.submitTask(flowVariables);
        return R.success("审批完成");
    }

    /**
     * 驳回任务
     * @param taskId 任务ID
     * @return r
     */
    @PostMapping(value = "/task/back/{taskId}")
    public R backTask(@PathVariable String taskId, String comment) {
        processEngine.backToPreNode(taskId, comment);
        return R.success("驳回成功");
    }

    /**
     * 驳回指定环节
     * @param taskId 任务ID
     * @param targetNodeId 目标节点ID
     * @return r
     */
    @PostMapping(value = "/task/back2/{taskId}/{targetNodeId}")
    public R putBack2Task(@PathVariable String taskId, @PathVariable String targetNodeId) {
        processEngine.back2Node(taskId, targetNodeId);
        return R.success("驳回成功");
    }

    /**
     * 驳回至首环节
     * @param taskId 任务ID
     * @return r
     */
    @PostMapping(value = "/task/back/first/{taskId}")
    public R putBackFirstTask(@PathVariable String taskId) {
        processEngine.backToFirstNode(taskId);
        return R.success("驳回成功");
    }

    /**
     * 撤回任务
     * @param taskId 已办任务ID
     * @return r
     */
    @SneakyThrows
    @PostMapping(value = "/task/withdraw/{taskId}")
    public R withdrawTask(@PathVariable String taskId) {
        processEngine.withdrawApproval(taskId);
//        processEngine.withdraw(taskId);
        return R.success("撤回成功");
    }


    /**
     * 已办任务
     * @param processQueryVO
     * @return
     */
    @GetMapping("/task/doneList")
    public R doneList(PageInfo<ProcessQueryVO> pageInfo, ProcessQueryVO processQueryVO) {
        val user = ShiroUtils.getUser();
        processQueryVO.setUsername(user.getUsername());

        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();

        if (StrUtil.isNotBlank(processQueryVO.getUsername())) {
            historicTaskInstanceQuery.taskAssignee(processQueryVO.getUsername());
        }
        if (StrUtil.isNotBlank(processQueryVO.getTaskName())) {
            historicTaskInstanceQuery.taskNameLike(ProcessKit.jointLike(processQueryVO.getTaskName()));
        }
        if (StrUtil.isNotBlank(processQueryVO.getProcessName())) {
            historicTaskInstanceQuery.processDefinitionNameLike(ProcessKit.jointLike(processQueryVO.getProcessName()));
        }

        historicTaskInstanceQuery.finished().orderByHistoricTaskInstanceEndTime().desc();

        List<HistoricTaskInstance> historicTaskInstanceList = historicTaskInstanceQuery.orderByHistoricTaskInstanceStartTime().desc().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());

        List<MyDoneVO> myDoneVOS = new ArrayList<>();
        historicTaskInstanceList.forEach(historicTaskInstance -> {
            MyDoneVO myDoneVO = new MyDoneVO();
            BeanUtil.copyProperties(historicTaskInstance, myDoneVO);

            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();

            myDoneVO.setTaskId(historicTaskInstance.getId());
            myDoneVO.setProcessName(historicProcessInstance.getName());
            myDoneVO.setStartUser(historicProcessInstance.getStartUserId());
            myDoneVO.setProcessDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion());

            /*List<Comment> comments = taskService.getTaskComments(historicTaskInstance.getId(), "comment");
            if(comments != null && comments.size() > 0) {
                Comment comment = comments.get(0);
                if (comment.getFullMessage() != null && (comment.getFullMessage().startsWith("[驳回]") || comment.getFullMessage().startsWith("[回退]"))) {
                    myDoneVO.setApproveAction("驳回");
                }else if (comment.getFullMessage() != null && comment.getFullMessage().startsWith("撤回")) {
                    myDoneVO.setApproveAction("撤回");
                } else if (comment.getFullMessage() != null && comment.getFullMessage().startsWith("[同意]")) {
                    myDoneVO.setApproveAction("同意");
                }
            }*/
            // TODO: 审批操作
            String deleteReason = historicTaskInstance.getDeleteReason();
            if (deleteReason != null && (deleteReason.startsWith("驳回") || deleteReason.startsWith("回退"))) {
                myDoneVO.setApproveAction("驳回");
            } else if(deleteReason != null && deleteReason.startsWith("撤回")) {
                myDoneVO.setApproveAction("撤回");
            } else {
                myDoneVO.setApproveAction("同意");
            }

            if (historicProcessInstance.getEndTime() == null) {
                myDoneVO.setProcessStatus(ProcessKit.FLOW_STATUS_RUNNING);
            } else {
                myDoneVO.setProcessStatus(ProcessKit.FLOW_STATUS_END);
            }
            myDoneVOS.add(myDoneVO);
        });
        return R.successPage(historicTaskInstanceQuery.count(), myDoneVOS);
    }


}
