package com.zhua.pro.cms.listener;

import com.zhua.pro.cms.config.SpringContextConfig;
import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.util.ServletUtils;
import com.zhua.pro.cms.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ActExecutionListener
 * @Description 执行监听器
 * @Author zhua
 * @Date 2020/12/21 10:11
 * @Version 1.0
 */
@Slf4j
public class ActExecutionListener  implements ExecutionListener {

    private final static TaskService taskService = SpringContextConfig.getBean(TaskService.class);

    @Override
    public void notify(DelegateExecution delegateExecution) {
        log.info("执行监听器开始执行");
        String eventName = delegateExecution.getEventName();
        log.info("==========eventName："+eventName);
        log.info("==========eventId："+delegateExecution.getId());
//        WebApplicationContext springContext = WebApplicationContextUtils
//                .getWebApplicationContext(ServletUtils.getServletContext());
        String processInstanceId = delegateExecution.getProcessInstanceId();
        log.info("==========processInstanceId：" + processInstanceId);

        FlowElement currentFlowElement = delegateExecution.getCurrentFlowElement();
        if(currentFlowElement instanceof UserTask){
            UserTask userTask = (UserTask) currentFlowElement;
            // 得到当前用户
            LoginUser loginUser = ShiroUtils.getUser();
            log.info("==========当前登录人："+loginUser.getUsername());
            List<String> list = new ArrayList<>();
            //查找上级经理,设置任务受理人
            if("admin".equals(loginUser.getUsername())) {
                list.add("user2");
                userTask.setAssignee("user2");
            }else if("user1".equals(loginUser.getUsername())) {
                list.add("user3");
                userTask.setAssignee("user3");
            }

            List<String> candidateUsers = userTask.getCandidateUsers();
            log.debug("candidateUsers={}", candidateUsers.size());
            // 设为本地变量(节点所有)
//            delegateExecution.setVariable("userList", list);
//            delegateTask.setVariableLocal("userList",candidateUsers);
            for (String u : list) {
//                taskService.addCandidateUser( , u);
            }

        }

    }
}
