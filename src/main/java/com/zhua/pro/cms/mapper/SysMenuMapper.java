package com.zhua.pro.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhua.pro.cms.entity.SysMenu;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getMenuByRoleCode(String roleCode);

    List<SysMenu> getCurrentMenu(String userId);
}
