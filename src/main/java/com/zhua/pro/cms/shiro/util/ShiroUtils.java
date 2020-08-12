package com.zhua.pro.cms.shiro.util;

import com.zhua.pro.cms.shiro.service.MyShiroRealm;
import com.zhua.pro.cms.util.StringUtils;
import com.zhua.pro.cms.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;

import java.util.List;


/**
 * shiro 工具类
 *
 * @author zhua
 */
public class ShiroUtils {

    private ShiroUtils() {
    }

    /**
     * 获取shiro subject
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午10:00:55
     */
    public static Subject getSubjct() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取登录session
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午10:00:41
     */
    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 退出登录
     *
     * @author zhua
     * @Date 2019年11月21日 上午10:00:24
     */
    public static void logout() {
        getSubjct().logout();
    }

    /**
     * 获取登录用户model
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午10:00:10
     */
    public static LoginUser getUser() {
        LoginUser user = null;
        Object obj = getSubjct().getPrincipal();
        if (StringUtils.isNotNull(obj)) {
            user = new LoginUser();
            BeanUtils.copyProperties(obj, user);
        }
        return user;
    }

    /**
     * set用户
     *
     * @param user
     * @author zhua
     * @Date 2019年11月21日 上午9:59:52
     */
    public static void setUser(LoginUser user) {
        Subject subject = getSubjct();
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
        // 重新加载Principal
        subject.runAs(newPrincipalCollection);
    }

    /**
     * 清除授权信息
     *
     * @author zhua
     * @Date 2019年11月21日 上午9:59:37
     */
    public static void clearCachedAuthorizationInfo() {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        MyShiroRealm realm = (MyShiroRealm) rsm.getRealms().iterator().next();
        realm.clearCachedAuthorizationInfo();
    }

    /**
     * 获取登录用户id
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午9:58:55
     */
    public static String getUserId() {
        LoginUser tsysUser = getUser();
        if (tsysUser == null || tsysUser.getId() == null) {
            throw new RuntimeException("用户不存在！");
        }
        return String.valueOf(tsysUser.getId());
    }

    /**
     * 获取登录用户name
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午9:58:48
     */
    public static String getLoginName() {
        LoginUser tsysUser = getUser();
        if (tsysUser == null) {
            throw new RuntimeException("用户不存在！");
        }
        return tsysUser.getUsername();
    }

    /**
     * 获取登录用户ip
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午9:58:26
     */
    public static String getIp() {
        return getSubjct().getSession().getHost();
    }

    /**
     * 获取登录用户sessionid
     *
     * @return
     * @author zhua
     * @Date 2019年11月21日 上午9:58:37
     */
    public static String getSessionId() {
        return String.valueOf(getSubjct().getSession().getId());
    }

    /**
     * 获取角色列表
     *
     * @return
     */
    public static List<String> getRoles() {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        MyShiroRealm realm = (MyShiroRealm) rsm.getRealms().iterator().next();
        AuthorizationInfo authorizationInfo = realm.getAuthorizationInfo(getSubjct().getPrincipals());
        return (List<String>) authorizationInfo.getRoles();
    }


}
