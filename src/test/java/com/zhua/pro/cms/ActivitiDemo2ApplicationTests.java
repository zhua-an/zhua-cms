//package com.zhua.pro.cms;
//
//import org.activiti.api.process.runtime.ProcessRuntime;
//import org.activiti.api.runtime.shared.query.Page;
//import org.activiti.api.runtime.shared.query.Pageable;
//import org.activiti.engine.HistoryService;
//import org.activiti.engine.RepositoryService;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.history.HistoricTaskInstance;
//import org.activiti.engine.repository.Deployment;
//import org.activiti.engine.repository.DeploymentBuilder;
//import org.activiti.engine.repository.ProcessDefinition;
//import org.activiti.engine.runtime.ProcessInstance;
//import org.activiti.engine.task.Task;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @ClassName ActivitiDemo2ApplicationTests
// * activiti7
// * 主要是对以前项目做了一些封装
// * ProcessRuntime taskRuntime:本质还是以前的各种service;
// * 版本模糊，个版本都有一定的bug。团队实力有待考察
// * @Description TODO
// * @Author zhua
// * @Date 2020/11/29 16:36
// * @Version 1.0
// */
//@SpringBootTest
//public class ActivitiDemo2ApplicationTests {
//
//    @Resource
//    RepositoryService repositoryService;
//
//    @Resource
//    RuntimeService runtimeService;
//
//    @Resource
//    private ProcessRuntime processRuntime;
//
//    @Resource
//    TaskService taskService;
//
//    @Resource
//    HistoryService historyService;
//
//    @Test
//    void contextLoads() {
//        System.out.println("Number of process definitions : "
//                + repositoryService.createProcessDefinitionQuery().count());
//        System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
//        runtimeService.startProcessInstanceByKey("oneTaskProcess");
//        System.out.println("Number of tasks after process start: " + taskService.createTaskQuery().count());
//    }
//
//    @Test // 查看流程
//    public void contextLoads2() {
////        securityUtil.logInAs("salaboy");
//        Page processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
//        System.err.println("已部署的流程个数：" + processDefinitionPage.getTotalItems());
//        for (Object obj : processDefinitionPage.getContent()) {
//            System.err.println("流程定义：" + obj);
//        }
//    }
//
//    @Test // 部署流程
//    public void deploy() {
////        securityUtil.logInAs("salaboy");
//        String bpmnName = "MyProcess";
//        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("请假流程");
//        Deployment deployment = null;
//        try {
//            deployment = deploymentBuilder.addClasspathResource("processes/" + bpmnName + ".bpmn")
//                    .addClasspathResource("processes/" + bpmnName + ".png").deploy();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    void testProcess(){
//        //张三开启一个请假流程
//        String user = "张三";
//        String approve = "领导李四";
////        startLeaveProcess(user,"leave");
//
//
//        //张三查询自己流程
////        queryLeaveProcessING(user);
//
//
////        提交给领导李四审核
////        String taskId = "34f8958d-0d07-11ea-b319-9c5c8e7034f6";
////        completeTask(approve,taskId);
//
//
//
//        //领导李四查询自己的流程
////        queryLeaveProcessING(approve);
//
//
//        //李四提交自己的流程
//        completeTask(approve,"e60702be-0d08-11ea-8a0a-9c5c8e7034f6",1);
//
//
//        //张三查询自己的历史流程
////        queryHistoryTask(userKey);
//    }
//
//
//    /**
//     * 开启一个请假流程
//     * @param user 用户key
//     * @param processDefinitionKey 流程图key 每一个流程有对应的一个key这个是某一个流程内固定的写在bpmn内的
//     */
//    void startLeaveProcess(String user,String processDefinitionKey){
//        System.out.println(user+"开启一个请假流程："+ processDefinitionKey);
//        HashMap<String, Object> variables=new HashMap<>();
//        variables.put("user", user);//userKey在上文的流程变量中指定了
//
//        ProcessInstance instance = runtimeService
//                .startProcessInstanceByKey(processDefinitionKey,variables);
//        System.out.println("流程实例ID:"+instance.getId());
//        System.out.println("流程定义ID:"+instance.getProcessDefinitionId());
//        System.out.println("==================================================================");
//    }
//
//
//    /**
//     * 查询当前任务流程
//     */
//    void queryLeaveProcessING(String assignee){
//        System.out.println(assignee+"查询自己当前的流程：");
//        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
//                .taskAssignee(assignee)//指定个人任务查询
//                .list();
//        if(list!=null && list.size()>0){
//            for(Task task:list){
//                System.out.println("任务ID:"+task.getId());
//                System.out.println("任务名称:"+task.getName());
//                System.out.println("任务的创建时间:"+task.getCreateTime());
//                System.out.println("任务的办理人:"+task.getAssignee());
//                System.out.println("流程实例ID："+task.getProcessInstanceId());
//                System.out.println("执行对象ID:"+task.getExecutionId());
//                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
//                Map<String, Object> map = task.getProcessVariables();
//                for (Map.Entry<String, Object> m : map.entrySet()) {
//                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
//                }
//                for (Map.Entry<String, Object> m : task.getTaskLocalVariables().entrySet()) {
//                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
//                }
//
//            }
//        }
//        System.out.println("==================================================================");
//    }
//
//
//
//    @Test
//    void completeTask(String approve,String taskId){
//        System.out.println(approve+"：提交自己的流程："+taskId);
//        //任务ID
//        HashMap<String, Object> variables=new HashMap<>();
//        variables.put("approve", approve);//userKey在上文的流程变量中指定了
//        taskService.complete(taskId,variables);
//
//        System.out.println("完成任务：任务ID："+taskId);
//        System.out.println("==================================================================");
//    }
//
//
//    @Test
//    void completeTask(String user,String taskId,int audit){
//        System.out.println(user+"：提交自己的流程："+taskId+" ;是否通过："+audit);
//        //任务ID
//        HashMap<String, Object> variables=new HashMap<>();
//        variables.put("audit", audit);//userKey在上文的流程变量中指定了
//        taskService.complete(taskId,variables);
//
//        System.out.println("完成任务：任务ID："+taskId);
//        System.out.println("==================================================================");
//    }
//
//    /**
//     * 查询指定流程的所有实例
//     */
//    @Test
//    public void processInstanceQuery() {
//        String processDefinitionKey = "myProcess_1";
//        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
//        for (ProcessInstance instance : list) {
//            System.out.println("流程实例id： " + instance.getProcessInstanceId());
//            System.out.println("所属流程定义id： " + instance.getProcessDefinitionId());
//            System.out.println("是否执行完成： " + instance.isEnded());
//            System.out.println("是否暂停： " + instance.isSuspended());
//            System.out.println(" 当 前 活 动 标 识 ： " + instance.getActivityId());
//        }
//    }
//
//    /**
//     * 删除已经部署的流程定义
//     * delete from ACT_RE_DEPLOYMENT 流程部署信息表;
//     * ACT_RE_PROCDEF 流程定义数据表;
//     * ACT_GE_BYTEARRAY 二进制数据表;
//     */
//    @Test
//    public void deleteProcessDefinition(){
//        //执行删除流程定义  参数代表流程部署的id
//        repositoryService.deleteDeployment("b10a151b-3366-11ea-bc18-30b49ec7161f");
//    }
//
//    /**
//     * 删除流程实例
//     */
//    @Test
//    public void deleteProcessInstance(){
//        String processInstanceId = "80a5703b-35c0-11ea-aa1a-30b49ec7161f";
//
//        //当前流程实例没有完全结束的时候，删除流程实例就会失败
//        runtimeService.deleteProcessInstance(processInstanceId,"删除原因");
//    }
//
//    /**
//     * 操作流程的挂起、激活
//     */
//    @Test
//    public void activateProcessDefinitionById(){
//        //查询流程定义对象
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("myProcess_1").singleResult();
//        //当前流程定义的实例是否都为暂停状态
//        boolean suspended = processDefinition.isSuspended();
//
//        String processDefinitionId = processDefinition.getId();
//
//        if(suspended){
//            //挂起状态则激活
//            repositoryService.activateProcessDefinitionById(processDefinitionId,true,new Date());
//            System.out.println("流程定义："+processDefinitionId+"激活");
//        }else{
//            //激活状态则挂起
//            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,new Date());
//            System.out.println("流程定义："+processDefinitionId+"挂起");
//        }
//    }
//
//    /**
//     * 单个流程实例的挂起，激活
//     */
//    @Test
//    public void activateProcessInstanceById(){
//        //查询流程实例对象
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("80a5703b-35c0-11ea-aa1a-30b49ec7161f").singleResult();
//
//        //当前流程定义的实例是否都为暂停状态
//        boolean suspended = processInstance.isSuspended();
//
//        String processInstanceId = processInstance.getId();
//        if(suspended){
//            //激活
//            runtimeService.activateProcessInstanceById(processInstanceId);
//            System.out.println("流程："+processInstanceId+"激活");
//        }else{
//            //挂起
//            runtimeService.suspendProcessInstanceById(processInstanceId);
//            System.out.println("流程："+processInstanceId+"挂起");
//        }
//    }
//
//    @Test
//    void queryHistoryTask(){
//        List<HistoricTaskInstance> list=historyService // 历史相关Service
//                .createHistoricTaskInstanceQuery() // 创建历史活动实例查询
//                .processInstanceId("34f2f038-0d07-11ea-b319-9c5c8e7034f6") // 执行流程实例id
//                .orderByTaskCreateTime()
//                .asc()
//                .list();
//        for(HistoricTaskInstance hai:list){
//            System.out.println("活动ID:"+hai.getId());
//            System.out.println("流程实例ID:"+hai.getProcessInstanceId());
//            System.out.println("活动名称："+hai.getName());
//            System.out.println("办理人："+hai.getAssignee());
//            System.out.println("开始时间："+hai.getStartTime());
//            System.out.println("结束时间："+hai.getEndTime());
//            System.out.println("==================================================================");
//        }
////        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("张三").orderByTaskCreateTime().asc().list();
////        for(HistoricTaskInstance hai:list){
////            System.out.println("活动ID:"+hai.getId());
////            System.out.println("流程实例ID:"+hai.getProcessInstanceId());
////            System.out.println("活动名称："+hai.getName());
////            System.out.println("办理人："+hai.getAssignee());
////            System.out.println("开始时间："+hai.getStartTime());
////            System.out.println("结束时间："+hai.getEndTime());
////            System.out.println("==================================================================");
////        }
//
//
//    }
//}
