package com.zhua.pro.cms.controller;

import com.zhua.pro.cms.vo.TitleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ClassName SwaggerController
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/15 14:19
 * @Version 1.0
 */
@Api(value = "Api接口文档")
@RestController
@RequestMapping("/swagger")
public class SwaggerController extends BaseController {

    //跳转页面参数
    private String prefix = "/page/admin/swagger";

    /**
     * 展示页面
     *
     * @param model
     * @return
     */
    @ApiOperation(value = "展示页面", notes = "展示页面")
    @GetMapping("/view")
    @RequiresPermissions("sys_swagger_view")
    public ModelAndView view(ModelMap model) {
        String str = "API文档";
        setTitle(model, new TitleVo("列表", str + "管理", true, "欢迎进入" + str + "页面", true, false));
        return new ModelAndView(prefix + "/list");
    }

}
