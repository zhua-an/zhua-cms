package com.zhua.pro.cms.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName MenuVo
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/12 16:49
 * @Version 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuVo extends TreeVo {

    private Integer pid;

    private String icon;

    private String target;

    private List<MenuVo> child;
}
