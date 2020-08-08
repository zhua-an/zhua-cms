package com.zhua.pro.cms.vo;

import com.zhua.pro.cms.entity.SysMenu;
import lombok.Data;

/**
 * @ClassName SysMenuVo
 * @Description TODO
 * @Author zhua
 * @Date 2020/8/6 17:30
 * @Version 1.0
 */
@Data
public class SysMenuVo extends SysMenu {

    private String parentMenuName;

    private SysMenu parentMenu;

}
