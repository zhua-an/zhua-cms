/*
 Navicat Premium Data Transfer

 Source Server         : 49.232.172.159
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 49.232.172.159:3306
 Source Schema         : template

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 10/08/2020 11:22:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` int(64) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据值',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述',
  `sort` int(10) NOT NULL COMMENT '排序（升序）',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `del_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sys_dict_value`(`value`) USING BTREE,
  INDEX `sys_dict_label`(`label`) USING BTREE,
  INDEX `sys_dict_del_flag`(`del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, '9', '异常', 'log_type', '日志异常', 1, '2018-07-09 06:16:14', '2018-11-24 07:25:11', '日志异常', '0');
INSERT INTO `sys_dict` VALUES (2, '0', '正常', 'log_type', '正常', 0, '2018-07-09 06:15:40', '2018-11-24 07:25:14', '正常', '0');
INSERT INTO `sys_dict` VALUES (3, '0', '正常', 'lock_type', '锁定类型', 1, '2020-02-06 10:49:27', '2020-02-06 10:49:27', '锁定类型', '0');
INSERT INTO `sys_dict` VALUES (4, '9', '锁定', 'lock_type', '锁定类型', 2, '2020-02-06 10:50:10', '2020-02-06 10:50:10', '锁定类型', '0');
INSERT INTO `sys_dict` VALUES (5, '1', '博文', 'article_type', '文章类型', 1, '2020-02-06 10:52:30', '2020-02-06 10:52:30', '文章类型', '0');
INSERT INTO `sys_dict` VALUES (6, '2', '美文', 'article_type', '文章类型', 2, '2020-02-06 10:53:00', '2020-02-06 10:53:00', '文章类型', '0');
INSERT INTO `sys_dict` VALUES (7, '1', 'VUE插件', 'res_type', '资源类型', 1, '2020-03-30 17:32:47', '2020-07-31 17:57:34', 'VUE插件', '0');
INSERT INTO `sys_dict` VALUES (8, '2', 'JQuery插件', 'res_type', '资源类型', 2, '2020-03-30 17:33:48', '2020-04-11 11:54:35', 'JQuery插件', '0');
INSERT INTO `sys_dict` VALUES (9, '3', '壁纸', 'res_type', '资源类型', 3, '2020-03-30 17:35:49', '2020-04-11 11:54:39', '壁纸', '0');
INSERT INTO `sys_dict` VALUES (10, '67', 'hg2', 'hk2', 'hhj', 2, '2020-08-02 09:46:13', '2020-08-02 09:46:32', NULL, '1');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `menu_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `menu_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单权限标识',
  `href` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '前端URL',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父菜单ID',
  `icon` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `target` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接打开方式',
  `sort` int(11) NULL DEFAULT 1 COMMENT '排序值',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单类型 （0菜单 1按钮）',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '逻辑删除标记(0--正常 1--删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (4, '基础管理', NULL, '', -1, 'icon-quanxianguanli', NULL, 1, '0', '2019-07-03 11:31:24', '2020-07-31 15:56:52', '0');
INSERT INTO `sys_menu` VALUES (5, '用户管理', NULL, '/sysUser/init', 4, 'icon-yonghuguanli', NULL, 1, '0', '2017-11-02 22:24:37', '2020-07-28 17:07:09', '0');
INSERT INTO `sys_menu` VALUES (6, '权限管理', NULL, '/sysMenu/init', 4, 'icon-iconfontcaidan', NULL, 2, '0', '2017-11-08 09:57:27', '2020-07-28 17:30:12', '0');
INSERT INTO `sys_menu` VALUES (7, '角色管理', NULL, '/sysRole/init', 4, 'icon-jueseguanli', NULL, 3, '0', '2017-11-08 10:13:37', '2020-07-23 09:53:18', '0');
INSERT INTO `sys_menu` VALUES (9, '用户新增', 'sys_user_add', NULL, 5, NULL, NULL, 1, '1', '2017-11-08 09:52:09', '2020-03-26 14:12:08', '0');
INSERT INTO `sys_menu` VALUES (10, '用户修改', 'sys_user_edit', NULL, 5, NULL, NULL, 2, '1', '2017-11-08 09:52:48', '2020-03-26 14:12:10', '0');
INSERT INTO `sys_menu` VALUES (11, '用户删除', 'sys_user_del', NULL, 5, NULL, NULL, 3, '1', '2017-11-08 09:54:01', '2020-03-26 14:12:13', '0');
INSERT INTO `sys_menu` VALUES (12, '权限新增', 'sys_menu_add', NULL, 6, NULL, NULL, 1, '1', '2017-11-08 10:15:53', '2020-07-31 15:57:46', '0');
INSERT INTO `sys_menu` VALUES (13, '权限修改', 'sys_menu_edit', NULL, 6, NULL, NULL, 2, '1', '2017-11-08 10:16:23', '2020-07-31 15:57:49', '0');
INSERT INTO `sys_menu` VALUES (14, '权限删除', 'sys_menu_del', NULL, 6, NULL, NULL, 3, '1', '2017-11-08 10:16:43', '2020-07-31 15:57:54', '0');
INSERT INTO `sys_menu` VALUES (15, '角色新增', 'sys_role_add', NULL, 7, NULL, NULL, 1, '1', '2017-11-08 10:14:18', '2020-03-26 14:12:30', '0');
INSERT INTO `sys_menu` VALUES (16, '角色修改', 'sys_role_edit', NULL, 7, NULL, NULL, 2, '1', '2017-11-08 10:14:41', '2020-03-26 14:12:32', '0');
INSERT INTO `sys_menu` VALUES (17, '角色删除', 'sys_role_del', NULL, 7, NULL, NULL, 3, '1', '2017-11-08 10:14:59', '2020-03-26 14:12:35', '0');
INSERT INTO `sys_menu` VALUES (18, '分配权限', 'sys_role_menu', NULL, 7, NULL, NULL, 4, '1', '2018-04-20 07:22:55', '2020-07-31 15:58:03', '0');
INSERT INTO `sys_menu` VALUES (19, '用户解锁', 'sys_user_lock', NULL, 5, NULL, NULL, 4, '1', '2019-08-31 06:41:00', '2020-03-26 14:12:44', '0');
INSERT INTO `sys_menu` VALUES (20, '系统管理', NULL, '', -1, 'icon-shezhi', NULL, 2, '0', '2019-09-03 08:54:50', '2020-07-31 15:56:59', '0');
INSERT INTO `sys_menu` VALUES (25, '字典管理', NULL, '/sysDict/init', 20, 'icon-zidianpeizhi', NULL, 2, '0', '2019-11-13 07:53:53', '2020-07-28 17:07:16', '0');
INSERT INTO `sys_menu` VALUES (26, '新增', 'sys_dict_add', NULL, 25, NULL, NULL, 1, '1', '2019-11-13 07:57:10', '2020-03-26 17:17:13', '0');
INSERT INTO `sys_menu` VALUES (27, '修改', 'sys_dict_edit', NULL, 25, NULL, NULL, 2, '1', '2019-11-13 07:58:07', '2020-03-26 17:17:15', '0');
INSERT INTO `sys_menu` VALUES (28, '删除', 'sys_dict_del', NULL, 25, NULL, NULL, 3, '1', '2019-11-13 07:58:56', '2020-03-26 17:17:18', '0');
INSERT INTO `sys_menu` VALUES (58, 'ceshi ', '', 'asf', -1, 'saf', NULL, 3, '0', '2020-08-05 17:35:15', '2020-08-05 17:35:15', '1');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色代码',
  `role_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标识（0-正常,1-删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_idx1_role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', 'ROLE_ADMIN', '管理员', '2017-10-29 15:45:51', '2018-12-26 14:09:11', '0');
INSERT INTO `sys_role` VALUES (2, 'ROLE_CQQ', 'ROLE_CQQ', '', '2018-11-11 19:42:26', '2019-08-24 21:45:18', '1');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'ROLE_USER', '普通用户角色', '2019-06-26 14:27:26', '2019-07-03 14:55:18', '0');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(11) NULL DEFAULT NULL COMMENT '角色ID',
  `menu_id` int(11) NULL DEFAULT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_id_menu_id`(`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1);
INSERT INTO `sys_role_menu` VALUES (2, 1, 2);
INSERT INTO `sys_role_menu` VALUES (3, 1, 3);
INSERT INTO `sys_role_menu` VALUES (4, 1, 4);
INSERT INTO `sys_role_menu` VALUES (5, 1, 5);
INSERT INTO `sys_role_menu` VALUES (6, 1, 6);
INSERT INTO `sys_role_menu` VALUES (7, 1, 7);
INSERT INTO `sys_role_menu` VALUES (8, 1, 8);
INSERT INTO `sys_role_menu` VALUES (9, 1, 9);
INSERT INTO `sys_role_menu` VALUES (10, 1, 10);
INSERT INTO `sys_role_menu` VALUES (11, 1, 11);
INSERT INTO `sys_role_menu` VALUES (12, 1, 12);
INSERT INTO `sys_role_menu` VALUES (13, 1, 13);
INSERT INTO `sys_role_menu` VALUES (14, 1, 14);
INSERT INTO `sys_role_menu` VALUES (15, 1, 15);
INSERT INTO `sys_role_menu` VALUES (16, 1, 16);
INSERT INTO `sys_role_menu` VALUES (17, 1, 17);
INSERT INTO `sys_role_menu` VALUES (18, 1, 18);
INSERT INTO `sys_role_menu` VALUES (19, 1, 19);
INSERT INTO `sys_role_menu` VALUES (20, 1, 20);
INSERT INTO `sys_role_menu` VALUES (21, 1, 21);
INSERT INTO `sys_role_menu` VALUES (22, 1, 22);
INSERT INTO `sys_role_menu` VALUES (23, 1, 23);
INSERT INTO `sys_role_menu` VALUES (24, 1, 24);
INSERT INTO `sys_role_menu` VALUES (25, 1, 25);
INSERT INTO `sys_role_menu` VALUES (26, 1, 26);
INSERT INTO `sys_role_menu` VALUES (27, 1, 27);
INSERT INTO `sys_role_menu` VALUES (28, 1, 28);
INSERT INTO `sys_role_menu` VALUES (29, 1, 29);
INSERT INTO `sys_role_menu` VALUES (30, 1, 30);
INSERT INTO `sys_role_menu` VALUES (31, 1, 31);
INSERT INTO `sys_role_menu` VALUES (32, 1, 32);
INSERT INTO `sys_role_menu` VALUES (35, 1, 33);
INSERT INTO `sys_role_menu` VALUES (36, 1, 34);
INSERT INTO `sys_role_menu` VALUES (37, 1, 35);
INSERT INTO `sys_role_menu` VALUES (38, 1, 36);
INSERT INTO `sys_role_menu` VALUES (39, 1, 37);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `qq` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'qq',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '随机盐',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '部门ID',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `lock_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '0-正常，9-锁定',
  `del_flag` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '0-正常，1-删除',
  `social_id` int(11) NULL DEFAULT NULL COMMENT '第三方社交id',
  `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'open_id',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人介绍',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_idx1_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'zhua.an', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '342961677', NULL, '', 'zhua_an@163.com', '山东省青岛市', 'http://qiniu.zhua91.com/avatar/5393354f6a654e5aa2a9081052ab08c1', 1, '2018-04-20 07:15:18', '2020-07-15 18:47:09', '0', '0', NULL, NULL, '心存感激💓，所遇皆温柔👣');
INSERT INTO `sys_user` VALUES (2, 'test', 'test', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '', '123@qq.com', '山东省青岛市', '', NULL, '2020-07-29 18:37:18', '2020-08-10 11:21:15', '0', '0', NULL, NULL, '测试账号');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_role`(`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
