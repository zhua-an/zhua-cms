package com.zhua.pro.cms.controller;

import com.zhua.pro.cms.config.ZhuaProConfig;
import com.zhua.pro.cms.vo.TitleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName BaseController
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/12 17:53
 * @Version 1.0
 */
@Controller
public class BaseController {

    @Autowired
    private ZhuaProConfig zhuaProConfig;

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 设置标题通用方法
     * @param map
     */
    public void setTitle(ModelMap map){
        //标题
        map.put("title",zhuaProConfig.getTitle());
        //是否打开欢迎语
        map.put("isMsg",zhuaProConfig.isMsg());
        //欢迎语
        map.put("msgHTML",zhuaProConfig.getMsgHtml());
    }

    /**
     * 设置标题通用方法
     * @param map
     * @param titleVo
     */
    public void setTitle(ModelMap map, TitleVo titleVo){
        //标题
        map.put("title",titleVo.getTitle());
        map.put("parenttitle",titleVo.getParenttitle());
        //是否打开欢迎语
        map.put("isMsg",titleVo.isMsg());
        //欢迎语
        map.put("msgHTML",titleVo.getMsgHtml());
    }
}
