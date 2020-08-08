package com.zhua.pro.cms.controller;

import com.zhua.pro.cms.core.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestController
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/23 16:13
 * @Version 1.0
 */
@RestController
public class TestController {

    @RequestMapping("test")
    public R test() {
        Map m = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        m.put("data", localDateTime);
        return R.successData(200, m);
    }

}
