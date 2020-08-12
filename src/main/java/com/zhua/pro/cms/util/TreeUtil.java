package com.zhua.pro.cms.util;

import com.zhua.pro.cms.vo.MenuVo;
import com.zhua.pro.cms.vo.TreeVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TreeUtil
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/12 16:42
 * @Version 1.0
 */
public class TreeUtil {

    public static List<MenuVo> toMenuTree(List<MenuVo> treeList, Integer pid) {
        List<MenuVo> retList = new ArrayList<MenuVo>();
        for (MenuVo parent : treeList) {
            if (pid.intValue() == parent.getPid().intValue()) {
                retList.add(findChildren(parent, treeList));
            }
        }
        return retList;
    }

    private static MenuVo findChildren(MenuVo parent, List<MenuVo> treeList) {
        for (MenuVo child : treeList) {
            if (parent.getId().equals(child.getPid())) {
                if (parent.getChild() == null) {
                    parent.setChild(new ArrayList<>());
                }
                parent.getChild().add(findChildren(child, treeList));
            }
        }
        return parent;
    }


    public static List<TreeVo> toTree(List<TreeVo> treeList, Integer pid) {
        List<TreeVo> retList = new ArrayList<TreeVo>();
        for (TreeVo parent : treeList) {
            if (pid.intValue() == parent.getParentId().intValue()) {
                retList.add(findChildren(parent, treeList));
            }
        }
        return retList;
    }

    private static TreeVo findChildren(TreeVo parent, List<TreeVo> treeList) {
        for (TreeVo child : treeList) {
            if (parent.getId().equals(child.getParentId())) {
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(findChildren(child, treeList));
            }
        }
        return parent;
    }
}
