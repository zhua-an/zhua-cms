package com.zhua.pro.cms.activiti.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Describe: 流程模型实体
 * */
@Data
public class ProModel implements Serializable {

        /**
         * 编号
         * */
        private String id;

        /**
         * 名称
         * */
        private String name;

        /**
         * 标识
         * */
        private String key;

        /**
         * 分类
         */
        private String category;

        /**
         * 版本
         * */
        private Integer version;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 最后更新时间
         */
        private Date lastUpdateTime;

}
