package com.zhua.pro.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhua.pro.cms.entity.SysUserRole;

import java.util.List;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 查询角色代码
     *
     * @param uid
     * @return
     */
    List<String> queryUserRole(Integer uid);

    /**
     * 查询用户权限
     *
     * @param uid
     * @return
     */
    List<String> queryUserPermission(Integer uid);
}
