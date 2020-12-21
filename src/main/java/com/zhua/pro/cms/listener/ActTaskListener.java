package com.zhua.pro.cms.listener;

import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @ClassName ActTaskListener
 * @Description 任务监听器
 * @Author zhua
 * @Date 2020/12/7 15:37
 * @Version 1.0
 */
@Slf4j
public class ActTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        // 得到IOC容器
        // 得到当前用户
        LoginUser loginUser = ShiroUtils.getUser();
        // 设置下一个任务的办理人
//        delegateTask.setAssignee(leaderUser.getName());
        log.info("==========任务监听：========", delegateTask.getName());
    }

}
