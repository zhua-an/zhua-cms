package com.zhua.pro.cms.activiti.vo;

import lombok.Data;

/**
 * Describe: 模型创建实体
 * */
@Data
public class ActivitiModelVo {

   /**
    * 模板名称
    * */
   private String name;

   /**
    * 模板标识
    * */
   private String key;

   /**
    * 分类
    */
   private String category;

   /**
    * 模板标识
    * */
   private String description;

   /**
    * 模板版本
    * */
   private String version;

}
