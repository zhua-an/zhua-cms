package com.zhua.pro.cms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhua.pro.cms.constants.CommonConstants;
import com.zhua.pro.cms.core.PageInfo;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.entity.SysMenu;
import com.zhua.pro.cms.service.ISysMenuService;
import com.zhua.pro.cms.shiro.util.ShiroUtils;
import com.zhua.pro.cms.util.StringUtils;
import com.zhua.pro.cms.util.TreeUtil;
import com.zhua.pro.cms.vo.SysMenuVo;
import com.zhua.pro.cms.vo.TreeVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 获取菜单
     *
     * @return
     */
    @GetMapping("/menu")
    public Map<String, Object> menu() {
        return sysMenuService.menu();
    }

    /**
     * 权限管理列表
     *
     * @return
     */
    @ApiOperation(value = "权限管理列表", notes = "权限管理列表")
    @GetMapping("/init")
    public ModelAndView init() {
        return new ModelAndView("page/admin/menu/menu-list");
    }

    /**
     * 权限列表查询
     *
     * @param pageInfo
     * @param form
     * @return
     */
    @ApiOperation(value = "权限列表查询", notes = "权限管理查询")
    @GetMapping("/page")
    public R page(PageInfo<SysMenu> pageInfo, SysMenu form) {
        R r = new R();
//        IPage<SysMenu> page = new Page<>(pageInfo.getPage(),pageInfo.getLimit());
        //条件构造器
        QueryWrapper<SysMenu> wrapper = new QueryWrapper();
        wrapper.like(StringUtils.isNoneBlank(form.getMenuName()), "menu_name", form.getMenuName());
        wrapper.orderByDesc("create_time");
//        sysMenuService.page(page, wrapper);
        List<SysMenu> list = sysMenuService.list(wrapper);
        int total = sysMenuService.count(wrapper);
        return r.successPage(total, list);
    }

    /**
     * 获取当前登录人权限树
     *
     * @return
     */
    @ApiOperation(value = "获取当前登录人权限树", notes = "获取当前登录人权限树")
    @GetMapping("/getMenuTree")
    public R getMenuTree(@RequestParam(required = false) String perm) {
        R r = new R();
        //查询权限树
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstants.STATUS_NORMAL);
        queryWrapper.orderByAsc("id");
        List<SysMenu> sysMenuList = sysMenuService.list(queryWrapper);
        if (sysMenuList != null && sysMenuList.size() > 0) {
            List<TreeVo> treeList = new ArrayList<>();
            sysMenuList.stream().forEach(sysMenu -> {
                TreeVo treeVo = new TreeVo();
                BeanUtils.copyProperties(sysMenu, treeVo);
                treeVo.setTitle(sysMenu.getMenuName());
                treeVo.setLabel(sysMenu.getMenuName());
                treeList.add(treeVo);
            });
            if ("1".equals(perm)) {
                //根据当前角色获取选择权限
                List<SysMenu> sysMenus = sysMenuService.getCurrentMenu(ShiroUtils.getUserId());
                sysMenus.stream().forEach(sysMenu -> {
                    for (TreeVo treeVo : treeList) {
                        if (treeVo.getId().intValue() == sysMenu.getId().intValue()) {
                            treeVo.setChecked(true);
                        }
                    }
                });
            }

            r.put("code", 0);
            r.put("data", TreeUtil.toTree(treeList, -1));
        } else {
        }
        return r;
    }

    /**
     * 新增权限页面
     *
     * @return
     */
    @ApiOperation(value = "新增权限页面", notes = "新增权限页面")
    @GetMapping("/info")
    public ModelAndView info(Integer id) {
        ModelAndView modelAndView = new ModelAndView("page/admin/menu/menu-info");
        if (id != null) {
            SysMenu sysMenu = sysMenuService.getById(id);
            if (sysMenu != null) {
                SysMenuVo sysMenuVo = new SysMenuVo();
                BeanUtils.copyProperties(sysMenu, sysMenuVo);
                //查找父节点
                if (!CommonConstants.TOP_TREE_ID.equals(String.valueOf(sysMenu.getParentId()))) {
                    SysMenu parentMenu = sysMenuService.getById(sysMenu.getParentId());
                    sysMenuVo.setParentMenuName(parentMenu.getMenuName());
                }
                modelAndView.addObject("sysMenu", sysMenuVo);

            }

        }
        return modelAndView;
    }

    /**
     * 保存
     *
     * @param sysMenu
     * @return
     */
    @ApiOperation(value = "新增权限", notes = "新增权限")
    @PostMapping("/add")
    public R add(SysMenu sysMenu) {
        if (sysMenu == null) {
            return R.error("获取权限信息失败!");
        }
        if (sysMenu.getParentId() == null) {
            sysMenu.setParentId(Integer.parseInt(CommonConstants.TOP_TREE_ID));
        }
        sysMenu.setDelFlag(CommonConstants.STATUS_NORMAL);
        sysMenu.setCreateTime(LocalDateTime.now());
        sysMenu.setUpdateTime(LocalDateTime.now());
        sysMenuService.save(sysMenu);
        return R.success();
    }

    /**
     * 更新
     *
     * @param sysMenu
     * @return
     */
    @ApiOperation(value = "修改权限", notes = "修改权限")
    @PostMapping("/update")
    public R update(SysMenu sysMenu) {
        if (sysMenu == null || sysMenu.getId() == null) {
            return R.error("获取权限信息失败!");
        }
        SysMenu info = sysMenuService.getById(sysMenu.getId());
        if (info == null) {
            return R.error("本系统不存在该权限信息!");
        }
        BeanUtils.copyProperties(sysMenu, info);
        if (info.getParentId() == null) {
            info.setParentId(Integer.parseInt(CommonConstants.TOP_TREE_ID));
        }
        info.setUpdateTime(LocalDateTime.now());
        sysMenuService.updateById(info);
        return R.success();
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除权限", notes = "删除权限")
    @PostMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        SysMenu info = sysMenuService.getById(id);
        if (info == null) {
            return R.error("本系统不存在该权限信息!");
        }
        info.setDelFlag(CommonConstants.STATUS_DEL);
        sysMenuService.updateById(info);
        return R.success();
    }

}
