package com.zhua.pro.cms;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(
//        exclude = {
//                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//                org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
//        }
//)
@SpringBootApplication(exclude = {
        org.activiti.spring.boot.SecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})
public class ZhuaCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhuaCmsApplication.class, args);
    }

}
