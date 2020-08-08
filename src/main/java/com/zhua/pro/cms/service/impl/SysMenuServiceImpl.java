package com.zhua.pro.cms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhua.pro.cms.constants.CommonConstants;
import com.zhua.pro.cms.entity.SysMenu;
import com.zhua.pro.cms.mapper.SysMenuMapper;
import com.zhua.pro.cms.mapper.SysUserRoleMapper;
import com.zhua.pro.cms.service.ISysMenuService;
import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.util.TreeUtil;
import com.zhua.pro.cms.vo.MenuVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private SysMenuMapper sysMenuMapper;
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Map<String, Object> menu() {
        Map<String, Object> map = new HashMap<>(16);
        Map<String,Object> home = new HashMap<>(16);
        Map<String,Object> logo = new HashMap<>(16);

        List<SysMenu> menuList = new ArrayList<>();
        List<String> roleList = sysUserRoleMapper.queryUserRole(Integer.parseInt(ShiroUtils.getUserId()));
        if(roleList != null && roleList.size() > 0) {
            roleList.stream().forEach(roleCode -> menuList.addAll(sysMenuMapper.getMenuByRoleCode(roleCode)));
        }
//        QueryWrapper<SysMenu> sysMenuQueryWrapper = new QueryWrapper<>();
//        sysMenuQueryWrapper.eq("del_flag", CommonConstants.STATUS_NORMAL);
//        sysMenuQueryWrapper.orderByAsc("sort");
//        List<SysMenu> menuList = sysMenuMapper.selectList(sysMenuQueryWrapper);
        List<MenuVo> menuInfo = new ArrayList<>();
        for (SysMenu e : menuList) {
            if(CommonConstants.MENU.equals(e.getType())) {
                MenuVo menuVO = new MenuVo();
                menuVO.setId(e.getId());
                menuVO.setPid(e.getParentId());
                menuVO.setHref(e.getHref());
                menuVO.setTitle(e.getMenuName());
                menuVO.setIcon(e.getIcon());
                menuVO.setTarget(e.getTarget());
                menuInfo.add(menuVO);
            }
        }
        map.put("menuInfo", TreeUtil.toMenuTree(menuInfo, -1));
        home.put("title", CommonConstants.HOME_TITLE);
        home.put("href", CommonConstants.HOME_HREF);//控制器路由,自行定义
        logo.put("title", CommonConstants.LOGO_TITLE);
        logo.put("image", CommonConstants.LOGO_IMAGE_LOCAL);//静态资源文件路径,可使用默认的logo.png
        map.put("homeInfo", home);
        map.put("logoInfo", logo);
        return map;
    }

    @Override
    public List<SysMenu> getCurrentMenu(String userId) {
        return sysMenuMapper.getCurrentMenu(userId);
    }
}
