package com.zhua.pro.cms.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhua.pro.cms.constants.CommonConstants;
import com.zhua.pro.cms.core.PageInfo;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.entity.SysDict;
import com.zhua.pro.cms.service.ISysDictService;
import com.zhua.pro.cms.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @author zhua
 * @since 2020-07-09
 */
@RestController
@RequestMapping("/sysDict")
@AllArgsConstructor
public class SysDictController {

    private ISysDictService sysDictService;

    /**
     * 字典管理列表
     *
     * @return
     */
    @ApiOperation(value = "字典管理列表", notes = "字典管理列表")
    @GetMapping("/init")
    public ModelAndView init() {
        return new ModelAndView("page/admin/dict/dict-list");
    }

    /**
     * 字典列表查询
     *
     * @param pageInfo
     * @param form
     * @return
     */
    @ApiOperation(value = "字典列表查询", notes = "字典管理查询")
    @GetMapping("/page")
    public R page(PageInfo<SysDict> pageInfo, SysDict form) {
        R r = new R();
        IPage<SysDict> page = new Page<>(pageInfo.getPage(), pageInfo.getLimit());
        //条件构造器
        QueryWrapper<SysDict> wrapper = new QueryWrapper();
        wrapper.like(StringUtils.isNoneBlank(form.getLabel()), "label", form.getLabel());
        wrapper.orderByDesc("create_time");
        sysDictService.page(page, wrapper);
        return r.successPage(page.getTotal(), page.getRecords());
    }

    /**
     * 新增字典页面
     *
     * @return
     */
    @ApiOperation(value = "新增字典页面", notes = "新增字典页面")
    @GetMapping("/info")
    public ModelAndView add(Integer id) {
        ModelAndView modelAndView = new ModelAndView("page/admin/dict/dict-info");
        if (id != null) {
            SysDict sysDict = sysDictService.getById(id);
            modelAndView.addObject(sysDict);
        }
        return modelAndView;
    }

    /**
     * 保存
     *
     * @param sysDict
     * @return
     */
    @ApiOperation(value = "新增字典", notes = "新增字典")
    @PostMapping("/add")
    public R add(SysDict sysDict) {
        if (sysDict == null) {
            return R.error("获取字典信息失败!");
        }
        sysDict.setDelFlag(CommonConstants.STATUS_NORMAL);
        sysDict.setCreateTime(LocalDateTime.now());
        sysDict.setUpdateTime(LocalDateTime.now());
        sysDictService.save(sysDict);
        return R.success();
    }

    /**
     * 更新
     *
     * @param sysDict
     * @return
     */
    @ApiOperation(value = "修改字典", notes = "修改字典")
    @PostMapping("/update")
    public R update(SysDict sysDict) {
        if (sysDict == null || sysDict.getId() == null) {
            return R.error("获取字典信息失败!");
        }
        SysDict info = sysDictService.getById(sysDict.getId());
        if (info == null) {
            return R.error("本系统不存在该字典信息!");
        }
        BeanUtils.copyProperties(sysDict, info);
        info.setUpdateTime(LocalDateTime.now());
        sysDictService.updateById(info);
        return R.success();
    }

    /**
     * 删除字典
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除字典", notes = "删除字典")
    @PostMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        SysDict info = sysDictService.getById(id);
        if (info == null) {
            return R.error("本系统不存在该字典信息!");
        }
        info.setDelFlag(CommonConstants.STATUS_DEL);
        sysDictService.updateById(info);
        return R.success();
    }

}
