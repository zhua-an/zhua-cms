package com.zhua.pro.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhua.pro.cms.entity.SysMenu;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单
     * @return
     */
    Map<String, Object> menu();

    /**
     * 获取当前登录用户所有菜单id
     * @param userId
     * @return
     */
    List<SysMenu> getCurrentMenu(String userId);
}
