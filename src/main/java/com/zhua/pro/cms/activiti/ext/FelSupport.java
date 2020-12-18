package com.zhua.pro.cms.activiti.ext;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;

import java.util.Map;

/**
 * EL表达式解析
 */
public class FelSupport {

    public static Object result(Map<String,Object> map,String expression){
        FelEngine fel = new FelEngineImpl();
        FelContext ctx = fel.getContext();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ctx.set(entry.getKey(),entry.getValue());
        }
        return fel.eval(expression);
    }
}
