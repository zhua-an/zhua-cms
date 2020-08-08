package com.zhua.pro.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhua.pro.cms.constants.CommonConstants;
import com.zhua.pro.cms.entity.SysRole;
import com.zhua.pro.cms.entity.SysRoleMenu;
import com.zhua.pro.cms.mapper.SysRoleMapper;
import com.zhua.pro.cms.mapper.SysRoleMenuMapper;
import com.zhua.pro.cms.service.ISysRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private SysRoleMapper sysRoleMapper;
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Transactional
    @Override
    public void save(SysRole sysRole, String menuIds) {
        sysRoleMapper.insert(sysRole);

        //插入
        String[] menuIdStr = menuIds.split(",");
        for(String menuId:menuIdStr) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(sysRole.getId());
            sysRoleMenu.setMenuId(Integer.parseInt(menuId));
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }

    @Transactional
    @Override
    public void updateById(SysRole info, String menuIds) {
        sysRoleMapper.updateById(info);

        UpdateWrapper<SysRoleMenu> wrapper = new UpdateWrapper<>();
        wrapper.eq("role_id", info.getId());
        sysRoleMenuMapper.delete(wrapper);

        //插入
        String[] menuIdStr = menuIds.split(",");
        for(String menuId:menuIdStr) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(info.getId());
            sysRoleMenu.setMenuId(Integer.parseInt(menuId));
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }

    @Transactional
    @Override
    public void deleteById(SysRole info) {

        UpdateWrapper<SysRoleMenu> wrapper = new UpdateWrapper<>();
        wrapper.eq("role_id", info.getId());
        sysRoleMenuMapper.delete(wrapper);

        sysRoleMapper.updateById(info);
    }


    @Transactional
    @Override
    public void batchDelete(String ids) {

        UpdateWrapper<SysRoleMenu> wrapper = new UpdateWrapper<>();
        wrapper.in("role_id", Arrays.asList(ids.split(",")));
        sysRoleMenuMapper.delete(wrapper);

        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", Arrays.asList(ids.split(",")));

        SysRole sysRole = new SysRole();
        sysRole.setDelFlag(CommonConstants.STATUS_DEL);
        sysRoleMapper.update(sysRole, queryWrapper);
    }
}
