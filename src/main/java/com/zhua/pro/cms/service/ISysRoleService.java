package com.zhua.pro.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhua.pro.cms.entity.SysRole;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 保存角色
     *
     * @param sysRole
     * @param menuIds
     */
    void save(SysRole sysRole, String menuIds);

    /**
     * 更新角色
     *
     * @param info
     * @param menuIds
     */
    void updateById(SysRole info, String menuIds);

    /**
     * 删除角色
     *
     * @param info
     */
    void deleteById(SysRole info);

    /**
     * 批量删除
     *
     * @param ids
     */
    void batchDelete(String ids);
}
