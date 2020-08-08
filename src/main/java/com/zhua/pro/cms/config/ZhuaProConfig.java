package com.zhua.pro.cms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 * @author zhua
 */
@Component
@ConfigurationProperties(prefix = "zhua.pro")
public class ZhuaProConfig {
    /** 项目名称 */
    private String name;
    /** 正标题 */
	private String title;
	/** 是都添加欢迎语 默认为false */
	private boolean isMsg;
	/** 欢迎语内容 默认内容为"" */
	private String msgHtml;
    /** 版本 */
    private String version;
    /** 版权年份 */
    private String copyrightYear;
    /** 默认上传的地址 */
    private static String defaultBaseDir;
    /** 是否开启 上传static **/
    private static String isstatic;
    /** 开启存放静态文件夹后目录 **/
    private static String isroot_dir;
    /** 邮箱发送smtp */
    private static String email_smtp;
    /** 发送邮箱端口 */
    private static String email_port;
    /** 发送邮箱登录账号 */
    private static String email_account;
    /** 发送邮箱登录密码 */
    private static String email_password;
    /** 演示模式 **/
    private static String demoEnabled;
    /** 滚动验证码 **/
    private static Boolean rollVerification;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMsg() {
        return isMsg;
    }

    public void setMsg(boolean msg) {
        isMsg = msg;
    }

    public String getMsgHtml() {
        return msgHtml;
    }

    public void setMsgHtml(String msgHtml) {
        this.msgHtml = msgHtml;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyrightYear() {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear) {
        this.copyrightYear = copyrightYear;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    public  void setDefaultBaseDir(String defaultBaseDir) {
        ZhuaProConfig.defaultBaseDir = defaultBaseDir;
    }

    public static String getIsstatic() {
        return isstatic;
    }

    public  void setIsstatic(String isstatic) {
        ZhuaProConfig.isstatic = isstatic;
    }

    public static String getIsroot_dir() {
        return isroot_dir;
    }

    public  void setIsroot_dir(String isroot_dir) {
        ZhuaProConfig.isroot_dir = isroot_dir;
    }

    public static String getEmail_smtp() {
        return email_smtp;
    }

    public  void setEmail_smtp(String email_smtp) {
        ZhuaProConfig.email_smtp = email_smtp;
    }

    public static String getEmail_port() {
        return email_port;
    }

    public  void setEmail_port(String email_port) {
        ZhuaProConfig.email_port = email_port;
    }

    public static String getEmail_account() {
        return email_account;
    }

    public  void setEmail_account(String email_account) {
        ZhuaProConfig.email_account = email_account;
    }

    public static String getEmail_password() {
        return email_password;
    }

    public  void setEmail_password(String email_password) {
        ZhuaProConfig.email_password = email_password;
    }

    public static String getDemoEnabled() {
        return demoEnabled;
    }

    public  void setDemoEnabled(String demoEnabled) {
        ZhuaProConfig.demoEnabled = demoEnabled;
    }

    public static Boolean getRollVerification() {
        return rollVerification;
    }

    public void setRollVerification(Boolean rollVerification) {
        ZhuaProConfig.rollVerification = rollVerification;
    }
}
