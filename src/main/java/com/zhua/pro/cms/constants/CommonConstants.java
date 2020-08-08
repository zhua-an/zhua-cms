package com.zhua.pro.cms.constants;

/**
 * @ClassName CommonConstants
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/12 16:55
 * @Version 1.0
 */
public interface CommonConstants {

    /**
     * 顶级树id
     */
    String TOP_TREE_ID = "-1";
    /**
     * 首页标题
     */
    public static String HOME_TITLE = "首页";
    /**
     * 首页地址
     */
    public static String HOME_HREF = "/admin/welcome";

    /**
     * LOGO标题
     */
    public static String LOGO_TITLE = "ZHUA_PRO";
    /**
     * LOGO图片本地地址
     */
    public static String LOGO_IMAGE_LOCAL = "/static/images/logo.png";
    /**
     * LOGO图片远程地址
     */
    public static String LOGO_IMAGE_HREF = "";

    /**
     * 有效状态
     */
    public static String STATUS_NORMAL = "0";
    /**
     * 删除状态
     */
    public static String STATUS_DEL = "1";

    /**
     * 菜单
     */
    String MENU = "0";

    /**
     * 按钮
     */
    String BUTTON = "1";

    /**
     * 默认密码
     */
    String DUFAULT_PASSWORD = "e10adc3949ba59abbe56e057f20f883e";
}
