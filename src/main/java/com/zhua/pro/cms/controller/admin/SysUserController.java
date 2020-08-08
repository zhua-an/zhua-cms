package com.zhua.pro.cms.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhua.pro.cms.constants.CommonConstants;
import com.zhua.pro.cms.core.PageInfo;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.entity.SysUser;
import com.zhua.pro.cms.service.ISysUserService;
import com.zhua.pro.cms.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
@RestController
@RequestMapping("/sysUser")
@AllArgsConstructor
public class SysUserController {
    
    private ISysUserService sysUserService;

    /**
     * 列表
     * @return
     */
    @ApiOperation(value="用户管理列表",notes="用户管理列表")
    @GetMapping("/init")
    public ModelAndView init() {
        return new ModelAndView("page/admin/user/user-list");
    }

    /**
     * 用户列表查询
     * @param pageInfo
     * @param form
     * @return
     */
    @ApiOperation(value="用户列表查询",notes="用户管理查询")
    @GetMapping("/page")
    public R page(PageInfo<SysUser> pageInfo, SysUser form) {
        R r = new R();
        IPage<SysUser> page = new Page<>(pageInfo.getPage(),pageInfo.getLimit());
        //条件构造器
        QueryWrapper<SysUser> wrapper = new QueryWrapper();
        wrapper.like(StringUtils.isNoneBlank(form.getUsername()),"username", form.getUsername());
        wrapper.like(StringUtils.isNoneBlank(form.getName()),"name", form.getName());
        wrapper.orderByDesc("create_time");
        sysUserService.page(page, wrapper);
        return r.successPage(page.getTotal(), page.getRecords());
    }

    /**
     * 新增用户页面
     * @return
     */
    @ApiOperation(value="新增用户页面",notes="新增用户页面")
    @GetMapping("/info")
    public ModelAndView add(Integer id) {
        ModelAndView modelAndView = new ModelAndView("page/admin/user/user-info");
        if(id != null) {
            SysUser sysUser = sysUserService.getById(id);
            modelAndView.addObject(sysUser);
        }
        return modelAndView;
    }

    /**
     * 保存
     * @param sysUser
     * @return
     */
    @ApiOperation(value="新增用户",notes="新增用户")
    @PostMapping("/add")
    public R add(SysUser sysUser) {
        if(sysUser == null) {
            return R.error("获取用户信息失败!");
        }
        sysUser.setPassword(CommonConstants.DUFAULT_PASSWORD);
        sysUser.setLockFlag(CommonConstants.STATUS_NORMAL);
        sysUser.setDelFlag(CommonConstants.STATUS_NORMAL);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUserService.save(sysUser);
        return R.success();
    }

    /**
     * 更新
     * @param sysUser
     * @return
     */
    @ApiOperation(value="修改用户",notes="修改用户")
    @PostMapping("/update")
    public R update(SysUser sysUser) {
        if(sysUser == null || sysUser.getId() == null) {
            return R.error("获取用户信息失败!");
        }
        SysUser info = sysUserService.getById(sysUser.getId());
        if(info == null) {
            return R.error("本系统不存在该用户信息!");
        }
        BeanUtils.copyProperties(sysUser, info);
        info.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(info);
        return R.success();
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @ApiOperation(value="删除用户",notes="删除用户")
    @PostMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        SysUser info = sysUserService.getById(id);
        if(info == null) {
            return R.error("本系统不存在该用户信息!");
        }
        info.setDelFlag(CommonConstants.STATUS_DEL);
        sysUserService.updateById(info);
        return R.success();
    }

    /**
     * 基本资料设置
     * @return
     */
    @GetMapping("/userSetting")
    public ModelAndView setting() {
        ModelAndView modelAndView = new ModelAndView("page/admin/system/user/user-setting");
        return modelAndView;
    }
}
