package com.zhua.pro.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName ZhuaProWebAppConfigurer
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/15 15:49
 * @Version 1.0
 */
@Configuration
public class ZhuaProWebAppConfigurer implements WebMvcConfigurer {

    /**
     * 静态资源处理
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置虚拟路径为项目得static下面
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/diagram-viewer/**").addResourceLocations("classpath:/static/diagram-viewer/");
        registry.addResourceHandler("/editor-app/**").addResourceLocations("classpath:/static/editor-app/");
    }

}
