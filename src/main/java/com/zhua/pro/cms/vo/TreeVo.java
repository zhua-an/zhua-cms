package com.zhua.pro.cms.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TreeVo
 * @Description TODO
 * @Author zhua
 * @Date 2020/8/2 15:44
 * @Version 1.0
 */
@Data
public class TreeVo {

    private Integer id;

    private Integer parentId;

    private String title;

    private String label;

    private String field;

    private String iconClass;

    private String href;

    private boolean spread;

    private boolean checked;

    private boolean disabled;

    List<TreeVo> children;

}
