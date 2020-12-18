package com.zhua.pro.cms.activiti.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.Date;

/**
 *
 */
public class KeyGenKit {

    public static String getKey(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        return prefix + DateUtil.format(new Date(), "yyyyMMdd") + RandomUtil.randomNumbers(4);
    }
}
