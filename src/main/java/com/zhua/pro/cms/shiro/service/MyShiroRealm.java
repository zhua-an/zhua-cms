package com.zhua.pro.cms.shiro.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhua.pro.cms.entity.SysUser;
import com.zhua.pro.cms.mapper.SysUserMapper;
import com.zhua.pro.cms.mapper.SysUserRoleMapper;
import com.zhua.pro.cms.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;


/**
 * @ClassName MyShiroRealm
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/14 18:14
 * @Version 1.0
 */
@Service
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;


    /**
     * 认证登陆
     */
    @SuppressWarnings("unused")
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {

        //加这一步的目的是在Post请求的时候会先进认证，然后在到请求
        if (token.getPrincipal() == null) {
            return null;
        }
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        // 通过username从数据库中查找 User对象，如果找到，没找到.
        // 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        SysUser userInfo = sysUserMapper.selectOne(Wrappers.<SysUser>query()
                .lambda().eq(SysUser::getUsername, username));
//		System.out.println(userInfo);
//		System.out.println("----->>userInfo=" + userInfo.getUsername() + "---"+ userInfo.getPassword());
        if (userInfo == null)
            return null;
        else{
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(userInfo, loginUser);
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    loginUser, // 用户对象
                    loginUser.getPassword(), // 密码
                    getName() // realm name
            );
            return authenticationInfo;
        }

    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        if(principals == null){
            throw new AuthorizationException("principals should not be null");
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        LoginUser userinfo  = (LoginUser)principals.getPrimaryPrincipal();
        Integer uid=userinfo.getId();
        List<String> roleList = sysUserRoleMapper.queryUserRole(uid);
        if(roleList != null && roleList.size() > 0) {
            //添加角色代码
            authorizationInfo.setRoles(new HashSet<>(roleList));
        }
        List<String> permissionList = sysUserRoleMapper.queryUserPermission(uid);
        if(permissionList != null && permissionList.size() > 0) {
            //添加权限代码
            authorizationInfo.setStringPermissions(new HashSet<>(permissionList));
        }

        return authorizationInfo;
    }

    /**
     * 清理缓存权限
     */
    public void clearCachedAuthorizationInfo() {
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }

    public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        AuthorizationInfo authorizationInfo = super.getAuthorizationInfo(principals);
        return  authorizationInfo;
    }

}
