package com.zhua.pro.cms.core;

import java.util.HashMap;
import java.util.List;

/**
* @ClassName: AjaxResult
* @Description: TODO(ajax操作消息提醒)
* @author zhua
* @date
*
 */
public class R extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /**
     * 初始化一个新创建的 Message 对象
     */
    public R()
    {
    }

    /**
     * 返回错误消息
     * 
     * @return 错误消息
     */
    public static R error()
    {
        return error(1, "操作失败");
    }

    /**
     * 返回错误消息
     * 
     * @param msg 内容
     * @return 错误消息
     */
    public static R error(String msg)
    {
        return error(500, msg);
    }

    /**
     * 返回错误消息
     * 
     * @param code 错误码
     * @param msg 内容
     * @return 错误消息
     */
    public static R error(int code, String msg)
    {
        R json = new R();
        json.put("success", false);
        json.put("code", code);
        json.put("msg", msg);
        return json;
    }

    /**
     * 返回成功消息
     * 
     * @param msg 内容
     * @return 成功消息
     */
    public static R success(String msg)
    {
        R json = new R();
        json.put("success", true);
        json.put("msg", msg);
        json.put("code", 200);
        return json;
    }
    
    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static R success()
    {
        return R.success("操作成功");
    }
    
    public static R successData(int code, Object value){
    	 R json = new R();
    	 json.put("success", true);
    	 json.put("code", code);
         json.put("data", value);
         return json;
    }

    public static R successPage(long count, List rows) {
        R json = new R();
        json.put("code", 0);
        json.put("msg", "");
        json.put("count", count);
        json.put("data", rows);
        return json;
    }
    
    /**
     * 返回成功消息
     * 
     * @param key 键值
     * @param value 内容
     * @return 成功消息
     */
    @Override
    public R put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }
}
