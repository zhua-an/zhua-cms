package com.zhua.pro.cms.controller;

import com.google.code.kaptcha.Constants;
import com.zhua.pro.cms.config.ZhuaProConfig;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.entity.SysUser;
import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @ClassName IndexController
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/7 17:10
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping()
@AllArgsConstructor
public class IndexController extends BaseController {

    /**
     * 首页
     *
     * @return
     */
    @ApiOperation(value = "首页", notes = "首页")
    @GetMapping({"/", "/index"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/index");
        return modelAndView;
    }

    /**
     * 首页
     *
     * @return
     */
    @ApiOperation(value = "首页", notes = "首页")
    @GetMapping({"/admin", "/admin/index"})
    public ModelAndView adminIndex() {
        ModelAndView modelAndView = new ModelAndView("page/admin/index");
        setTitle(modelAndView.getModelMap());
        return modelAndView;
    }

    /**
     * 首页
     *
     * @return
     */
    @ApiOperation(value = "首页", notes = "首页")
    @GetMapping({"/admin/welcome"})
    public ModelAndView adminWelcome() {
        ModelAndView modelAndView = new ModelAndView("page/admin/welcome");
        setTitle(modelAndView.getModelMap());
        return modelAndView;
    }

    /**
     * 登录页面
     *
     * @return
     */
    @ApiOperation(value = "请求到登陆界面", notes = "请求到登陆界面")
    @GetMapping("/admin/login")
    public ModelAndView adminLogin() {
        ModelAndView modelAndView = new ModelAndView();
        try {
            if ((null != SecurityUtils.getSubject() && SecurityUtils.getSubject().isAuthenticated()) || SecurityUtils.getSubject().isRemembered()) {
                modelAndView.setViewName("redirect:/admin/index");
            } else {
                System.out.println("--进行登录验证..验证开始");
                modelAndView.addObject("RollVerification", ZhuaProConfig.getRollVerification());
                System.out.println("ZhuaProConfig.getRollVerification()>>>" + ZhuaProConfig.getRollVerification());
                modelAndView.setViewName("login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelAndView;
    }

    /**
     * 用户登陆验证
     *
     * @param user
     * @param code
     * @param redirectAttributes
     * @param rememberMe
     * @param request
     * @return
     */
    @ApiOperation(value = "用户登陆验证", notes = "用户登陆验证")
    @PostMapping("/admin/login")
    public R adminLogin(SysUser user, String code, RedirectAttributes redirectAttributes, boolean rememberMe, HttpServletRequest request) {
        log.info("用户[" + user.getUsername() + "]进行登录验证...");
        Boolean yz = false;
        if (ZhuaProConfig.getRollVerification()) {//滚动验证
            yz = true;
        } else {//图片验证
            String scode = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            yz = StringUtils.isNotEmpty(scode) && StringUtils.isNotEmpty(code) && scode.equals(code);
        }
        //判断验证码
        if (yz) {
            String userName = user.getUsername();
            Subject currentUser = SecurityUtils.getSubject();
            //是否验证通过
            if (!currentUser.isAuthenticated()) {
                UsernamePasswordToken token = new UsernamePasswordToken(userName, user.getPassword());
                try {
                    if (rememberMe) {
                        token.setRememberMe(true);
                    }
                    //存入用户
                    currentUser.login(token);
                    if (StringUtils.isNotNull(ShiroUtils.getUser())) {
                        //跳转到 get请求的登陆方法
                        //view.setViewName("redirect:/"+prefix+"/index");
                        return R.success();
                    } else {
                        return R.error(500, "未知账户");
                    }
                } catch (UnknownAccountException uae) {
                    log.info("对用户[" + userName + "]进行登录验证..验证未通过,未知账户");
                    return R.error(500, "未知账户");
                } catch (IncorrectCredentialsException ice) {
                    log.info("对用户[" + userName + "]进行登录验证..验证未通过,错误的凭证");
                    return R.error(500, "用户名或密码不正确");
                } catch (LockedAccountException lae) {
                    log.info("对用户[" + userName + "]进行登录验证..验证未通过,账户已锁定");
                    return R.error(500, "账户已锁定");
                } catch (ExcessiveAttemptsException eae) {
                    log.info("对用户[" + userName + "]进行登录验证..验证未通过,错误次数过多");
                    return R.error(500, "用户名或密码错误次数过多");
                } catch (AuthenticationException ae) {
                    //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
                    log.info("对用户[" + userName + "]进行登录验证..验证未通过,堆栈轨迹如下");
                    ae.printStackTrace();
                    return R.error(500, "用户名或密码不正确");
                }
            } else {
                if (StringUtils.isNotNull(ShiroUtils.getUser())) {
                    //跳转到 get请求的登陆方法
                    //view.setViewName("redirect:/"+prefix+"/index");
                    return R.success();
                } else {
                    return R.error(500, "未知账户");
                }
            }
        } else {
            return R.error(500, "验证码不正确!");
        }
    }

    /**
     * 退出登陆
     *
     * @return
     */
    @ApiOperation(value = "退出登陆", notes = "退出登陆")
    @GetMapping("/admin/logout")
    public ModelAndView adminLogout(HttpServletRequest request, HttpServletResponse response) {
        //在这里执行退出系统前需要清空的数据
        Subject subject = SecurityUtils.getSubject();
        //注销
        subject.logout();
        return new ModelAndView("redirect:/admin/login");
    }

}
