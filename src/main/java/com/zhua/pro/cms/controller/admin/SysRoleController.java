package com.zhua.pro.cms.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhua.pro.cms.constants.CommonConstants;
import com.zhua.pro.cms.core.PageInfo;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.entity.SysRole;
import com.zhua.pro.cms.service.ISysMenuService;
import com.zhua.pro.cms.service.ISysRoleService;
import com.zhua.pro.cms.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * 系统角色表 前端控制器
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sysRole")
public class SysRoleController {

    private ISysRoleService sysRoleService;
    private ISysMenuService sysMenuService;

    /**
     * 列表
     * @return
     */
    @ApiOperation(value="角色管理列表",notes="角色管理列表")
    @GetMapping("/init")
    public ModelAndView init() {
        return new ModelAndView("page/admin/role/role-list");
    }

    /**
     * 角色列表查询
     * @param pageInfo
     * @param form
     * @return
     */
    @ApiOperation(value="角色列表查询",notes="角色管理查询")
    @GetMapping("/page")
    public R page(PageInfo<SysRole> pageInfo, SysRole form) {
        R r = new R();
        IPage<SysRole> page = new Page<>(pageInfo.getPage(),pageInfo.getLimit());
        //条件构造器
        QueryWrapper<SysRole> wrapper = new QueryWrapper();
        wrapper.like(StringUtils.isNoneBlank(form.getRoleName()),"role_name", form.getRoleName());
        wrapper.like(StringUtils.isNoneBlank(form.getRoleCode()),"role_code", form.getRoleCode());
        wrapper.orderByDesc("create_time");
        sysRoleService.page(page, wrapper);
        return r.successPage(page.getTotal(), page.getRecords());
    }

    /**
     * 新增角色页面
     * @return
     */
    @ApiOperation(value="新增角色页面",notes="新增角色页面")
    @GetMapping("/info")
    public ModelAndView add(Integer id) {
        ModelAndView modelAndView = new ModelAndView("page/admin/role/role-info");
        if(id != null) {
            SysRole sysRole = sysRoleService.getById(id);
            modelAndView.addObject(sysRole);
        }

        return modelAndView;
    }

    /**
     * 保存
     * @param sysRole
     * @return
     */
    @ApiOperation(value="新增角色",notes="新增角色")
    @PostMapping("/add")
    public R add(SysRole sysRole, String menuIds) {
        if(sysRole == null) {
            return R.error("获取角色信息失败!");
        }
        sysRole.setDelFlag(CommonConstants.STATUS_NORMAL);
        sysRole.setCreateTime(LocalDateTime.now());
        sysRole.setUpdateTime(LocalDateTime.now());
        sysRoleService.save(sysRole, menuIds);
        return R.success();
    }

    /**
     * 更新
     * @param sysRole
     * @return
     */
    @ApiOperation(value="修改角色",notes="修改角色")
    @PostMapping("/update")
    public R update(SysRole sysRole, String menuIds) {
        if(sysRole == null || sysRole.getId() == null) {
            return R.error("获取角色信息失败!");
        }
        SysRole info = sysRoleService.getById(sysRole.getId());
        if(info == null) {
            return R.error("本系统不存在该角色信息!");
        }
        info.setRoleName(sysRole.getRoleName());
        info.setRoleCode(sysRole.getRoleCode());
        info.setRoleDesc(sysRole.getRoleDesc());
        info.setUpdateTime(LocalDateTime.now());
        sysRoleService.updateById(info, menuIds);
        return R.success();
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @ApiOperation(value="删除角色",notes="删除角色")
    @PostMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        SysRole info = sysRoleService.getById(id);
        if(info == null) {
            return R.error("本系统不存在该角色信息!");
        }
        info.setDelFlag(CommonConstants.STATUS_DEL);
        sysRoleService.deleteById(info);
        return R.success();
    }

    /**
     * 批量删除角色
     * @param ids
     * @return
     */
    @ApiOperation(value="批量删除角色",notes="批量删除角色")
    @PostMapping("/delete")
    public R delete(String ids) {
        if(StringUtils.isBlank(ids)) {
            return R.error("角色信息不能为空");
        }
        sysRoleService.batchDelete(ids);
        return R.success();
    }

}
