/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.109
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : 192.168.1.109:3306
 Source Schema         : geoxus

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 03/02/2020 20:54:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE,
  INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME`, `INSTANCE_NAME`, `REQUESTS_RECOVERY`) USING BTREE,
  INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME`, `REQUESTS_RECOVERY`) USING BTREE,
  INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `INT_PROP_1` int(11) NULL DEFAULT NULL,
  `INT_PROP_2` int(11) NULL DEFAULT NULL,
  `LONG_PROP_1` bigint(20) NULL DEFAULT NULL,
  `LONG_PROP_2` bigint(20) NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
  `PRIORITY` int(11) NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_J`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_JG`(`SCHED_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_C`(`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE,
  INDEX `IDX_QRTZ_T_G`(`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_STATE`(`SCHED_NAME`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_N_STATE`(`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_N_G_STATE`(`SCHED_NAME`, `TRIGGER_GROUP`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_NEXT_FIRE_TIME`(`SCHED_NAME`, `NEXT_FIRE_TIME`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST`(`SCHED_NAME`, `TRIGGER_STATE`, `NEXT_FIRE_TIME`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_MISFIRE`(`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE`(`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`, `TRIGGER_STATE`) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP`(`SCHED_NAME`, `MISFIRE_INSTR`, `NEXT_FIRE_TIME`, `TRIGGER_GROUP`, `TRIGGER_STATE`) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_attributes
-- ----------------------------
DROP TABLE IF EXISTS `core_attributes`;
CREATE TABLE `core_attributes`  (
  `attribute_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性ID',
  `category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性分类',
  `field_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性名字',
  `show_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示名字',
  `validation_desc` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '验证表达式说明',
  `validation_expression` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证表达式,可以位正则表达式',
  `ext` json NULL COMMENT '附加信息',
  `is_core` smallint(255) NULL DEFAULT 1 COMMENT '是否为框架内置字段',
  `data_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性的数据类型',
  `column_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性的列类型',
  `front_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端类型',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`attribute_id`) USING BTREE,
  UNIQUE INDEX `unique_field_name`(`field_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_attributes_enums
-- ----------------------------
DROP TABLE IF EXISTS `core_attributes_enums`;
CREATE TABLE `core_attributes_enums`  (
  `attribute_enum_id` int(11) NOT NULL AUTO_INCREMENT,
  `attribute_id` int(11) NOT NULL,
  `core_model_id` smallint(6) NULL DEFAULT 0,
  `value_enum` int(11) NULL DEFAULT NULL,
  `show_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT 0,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`attribute_enum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_config
-- ----------------------------
DROP TABLE IF EXISTS `core_config`;
CREATE TABLE `core_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `core_model_id` int(45) NOT NULL COMMENT '核心模型ID',
  `ext` json NULL COMMENT '其他额外数据',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_media_library
-- ----------------------------
DROP TABLE IF EXISTS `core_media_library`;
CREATE TABLE `core_media_library`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `core_model_id` int(11) NULL DEFAULT NULL COMMENT '系统模型ID',
  `model_type` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型类型',
  `model_id` int(11) NULL DEFAULT NULL COMMENT '业务模型ID',
  `collection_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '集合名字',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名字',
  `file_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '带后缀的文件名字',
  `mime_type` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件mime',
  `disk` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储方式',
  `size` int(11) NOT NULL COMMENT '文件大小',
  `manipulations` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '维护者',
  `custom_properties` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '自定义属性',
  `responsive_images` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '响应式图片',
  `order_column` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '排序',
  `resource_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资源类型',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资源集合表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_model
-- ----------------------------
DROP TABLE IF EXISTS `core_model`;
CREATE TABLE `core_model`  (
  `model_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module_id` int(11) NULL DEFAULT NULL COMMENT '模块ID(商品,订单...)',
  `model_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型类型,用于统一JAVA、PHP命名',
  `model_show` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型显示名字',
  `model_identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型标识',
  `search_condition` json NULL COMMENT '搜索条件',
  `ext` json NULL COMMENT '额外信息',
  `model_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '命名空间',
  `created_at` int(255) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`model_id`) USING BTREE,
  UNIQUE INDEX `unique_model_identification`(`model_identification`) USING BTREE,
  INDEX `fk_module_id`(`module_id`) USING BTREE,
  CONSTRAINT `fk_module_id` FOREIGN KEY (`module_id`) REFERENCES `core_modules` (`module_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_model_attribute_group
-- ----------------------------
DROP TABLE IF EXISTS `core_model_attribute_group`;
CREATE TABLE `core_model_attribute_group`  (
  `model_attribute_group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_attribute_group_inner_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'ext' COMMENT '模型内的分组(如 : 项目信息....)',
  `model_id` int(11) NULL DEFAULT NULL COMMENT '模型ID',
  `attribute_id` int(11) NULL DEFAULT NULL COMMENT '属性ID',
  `required` smallint(2) NULL DEFAULT 1 COMMENT '是否必须  0、不是  1、是',
  `ext` json NULL COMMENT '扩展信息',
  `show_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示名字',
  `validation_expression` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正则表达式',
  `force_validation` smallint(2) NULL DEFAULT 0 COMMENT '是否强制使用rule进行验证',
  `field_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段别名',
  `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段默认值',
  `created_at` int(11) NULL DEFAULT 0 COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`model_attribute_group_id`) USING BTREE,
  UNIQUE INDEX `unique_model_id_attribute_id`(`model_id`, `attribute_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_model_attribute_permission
-- ----------------------------
DROP TABLE IF EXISTS `core_model_attribute_permission`;
CREATE TABLE `core_model_attribute_permission`  (
  `attribute_permission_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性ID',
  `model_attribute_group_id` int(11) NULL DEFAULT NULL COMMENT '模型组的ID 比如: goods、order、contents',
  `core_model_id` int(11) NULL DEFAULT NULL COMMENT '模型ID',
  `attribute_id` int(11) NULL DEFAULT NULL COMMENT '属性ID',
  `ext` json NULL COMMENT '扩展信息',
  `allow` json NULL COMMENT '允许的人员或者角色({\"roles\":[],\"users\":[]})',
  `deny` json NULL COMMENT '拒绝的人员或者角色({\"roles\":[],\"users\":[]})',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`attribute_permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_modules
-- ----------------------------
DROP TABLE IF EXISTS `core_modules`;
CREATE TABLE `core_modules`  (
  `module_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块名字',
  `ext` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '额外信息',
  `created_at` int(255) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`module_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for o_goods
-- ----------------------------
DROP TABLE IF EXISTS `o_goods`;
CREATE TABLE `o_goods`  (
  `goods_id` int(11) NOT NULL AUTO_INCREMENT,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `goods_price` decimal(10, 2) NULL DEFAULT NULL,
  `sku` int(255) NULL DEFAULT NULL,
  `ext` json NULL,
  `status` int(11) NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`goods_id`) USING BTREE,
  INDEX `idx__created_at_promotion_price`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for o_order_item
-- ----------------------------
DROP TABLE IF EXISTS `o_order_item`;
CREATE TABLE `o_order_item`  (
  `order_item_id` int(11) NOT NULL AUTO_INCREMENT,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `order_sn` bigint(30) NOT NULL,
  `goods_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `quantity` int(255) NULL DEFAULT NULL,
  `goods_price` decimal(10, 2) NULL DEFAULT NULL,
  `purchasing_price` decimal(10, 2) NULL DEFAULT NULL,
  `ext` json NULL,
  `status` int(11) NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for o_orders
-- ----------------------------
DROP TABLE IF EXISTS `o_orders`;
CREATE TABLE `o_orders`  (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_sn` bigint(30) NULL DEFAULT NULL,
  `order_type` tinyint(3) NULL DEFAULT NULL,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `order_price` decimal(12, 2) NULL DEFAULT NULL,
  `ext` json NULL,
  `pay_info` json NULL,
  `status` int(11) NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  `cancel_at` int(11) NULL DEFAULT NULL,
  `completed_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_comment
-- ----------------------------
DROP TABLE IF EXISTS `p_comment`;
CREATE TABLE `p_comment`  (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '父级ID',
  `model_id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型ID',
  `core_model_id` int(11) NULL DEFAULT NULL COMMENT '核心模型ID',
  `model_type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型类型',
  `ext` json NULL COMMENT '额外数据',
  `comments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评论内容',
  `user_id` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '层级',
  `status` smallint(6) NULL DEFAULT NULL COMMENT '状态',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_content
-- ----------------------------
DROP TABLE IF EXISTS `p_content`;
CREATE TABLE `p_content`  (
  `content_id` int(11) NOT NULL,
  `category_id` int(11) NULL DEFAULT NULL,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contents` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `keywords` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ext` json NULL,
  `status` smallint(6) NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`content_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for p_feedback
-- ----------------------------
DROP TABLE IF EXISTS `p_feedback`;
CREATE TABLE `p_feedback`  (
  `feedback_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NULL DEFAULT 0,
  `feedback_type` smallint(6) NULL DEFAULT NULL,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `model_id` int(11) NULL DEFAULT NULL,
  `ext` json NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `status` smallint(6) NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`feedback_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '留言与意见反馈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_admin
-- ----------------------------
DROP TABLE IF EXISTS `s_admin`;
CREATE TABLE `s_admin`  (
  `admin_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父级ID(用于特殊场景)',
  `nick_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员昵称',
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `salt` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐(用于加密)',
  `password` varchar(70) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '加密后的密码',
  `telephone` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` smallint(2) NULL DEFAULT NULL COMMENT '状态',
  `remark` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `ext` json NULL COMMENT '额外数据',
  `is_super_admin` smallint(4) NULL DEFAULT 1 COMMENT '是否超级管理员',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`admin_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_admin_has_permissions
-- ----------------------------
DROP TABLE IF EXISTS `s_admin_has_permissions`;
CREATE TABLE `s_admin_has_permissions`  (
  `permission_id` int(10) UNSIGNED NOT NULL COMMENT '权限ID',
  `admin_id` int(10) UNSIGNED NOT NULL COMMENT '管理员ID',
  `model_type` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'App\\System\\Entities\\SAdminModel' COMMENT '模型类型(用户获取用户权限,主要用于PHP)',
  PRIMARY KEY (`permission_id`, `admin_id`) USING BTREE,
  INDEX `fk_s_acl_has_s_admin_s_admin1_idx`(`admin_id`) USING BTREE,
  INDEX `fk_s_acl_has_s_admin_s_acl1_idx`(`permission_id`) USING BTREE,
  CONSTRAINT `s_admin_has_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `s_permissions` (`permission_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `s_admin_has_permissions_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `s_admin` (`admin_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员拥有的权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_admin_has_roles
-- ----------------------------
DROP TABLE IF EXISTS `s_admin_has_roles`;
CREATE TABLE `s_admin_has_roles`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` int(10) UNSIGNED NOT NULL COMMENT '角色ID',
  `admin_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '管理员ID',
  `model_type` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'App\\System\\Entities\\SAdminModel' COMMENT '模型类型(使用的模型,主要用于PHP)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_s_role_has_s_admin_s_admin1_idx`(`admin_id`) USING BTREE,
  INDEX `fk_s_role_has_s_admin_s_role1_idx`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色与管理员对应表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_banner
-- ----------------------------
DROP TABLE IF EXISTS `s_banner`;
CREATE TABLE `s_banner`  (
  `banner_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `intro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.intro'))) STORED COMMENT '简介' NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.url'))) STORED COMMENT '跳转url' NULL,
  `ext` json NULL COMMENT '额外数据',
  `core_model_id` int(11) NULL DEFAULT NULL COMMENT '核心模型ID',
  `sort` tinyint(255) NULL DEFAULT NULL COMMENT '排序',
  `type` smallint(6) NULL DEFAULT NULL COMMENT '类型',
  `status` tinyint(255) NULL DEFAULT 0 COMMENT '状态',
  `position` int(255) NULL DEFAULT 0 COMMENT '位置',
  `province_id` int(11) GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.province_id'))) STORED COMMENT '省ID' NULL,
  `city_id` int(11) GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.city_id'))) STORED COMMENT '市ID' NULL,
  `created_at` int(255) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`banner_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_category
-- ----------------------------
DROP TABLE IF EXISTS `s_category`;
CREATE TABLE `s_category`  (
  `category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '上级ID',
  `sort` tinyint(3) NULL DEFAULT NULL COMMENT '排序',
  `ext` json NULL COMMENT '自定义数据字段(JSON串存储)',
  `status` int(11) NULL DEFAULT NULL COMMENT '是否禁用',
  `core_model_id` int(11) NULL DEFAULT NULL,
  `model_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '内容分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_common_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `s_common_operation_log`;
CREATE TABLE `s_common_operation_log`  (
  `common_log_id` int(11) NOT NULL AUTO_INCREMENT,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `operation` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `params` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `exec_time` varchar(0) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` int(255) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`common_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_log
-- ----------------------------
DROP TABLE IF EXISTS `s_log`;
CREATE TABLE `s_log`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `core_model_id` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联核心模型ID',
  `model_id` int(11) NULL DEFAULT NULL COMMENT '具体模型对象ID',
  `ext` json NULL COMMENT '详细数据',
  `source` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源',
  `user_id` bigint(25) NULL DEFAULT NULL COMMENT '用户ID',
  `created_at` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统日志信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_message
-- ----------------------------
DROP TABLE IF EXISTS `s_message`;
CREATE TABLE `s_message`  (
  `message_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `core_model_id` int(11) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `type` smallint(4) NULL DEFAULT 0 COMMENT '消息类型',
  `contents` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息类容',
  `template_id` int(11) NULL DEFAULT 0 COMMENT '消息模板',
  `ext` json NULL COMMENT '额外信息',
  `status` smallint(6) NULL DEFAULT NULL COMMENT '状态',
  `created_at` int(11) NULL DEFAULT 0 COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_permissions
-- ----------------------------
DROP TABLE IF EXISTS `s_permissions`;
CREATE TABLE `s_permissions`  (
  `permission_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_code` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限码',
  `guard_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'api' COMMENT '权限保护类型(web,php，主要用于PHP)',
  `show_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '父级ID',
  `path` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前层级(1-2-3)',
  `url` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求的url, 可以填正则表达式',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型，1：菜单，2：按钮，3：其他',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态，1：正常，0：冻结',
  `sort` int(11) NULL DEFAULT NULL COMMENT '权限在当前模块下的顺序，由小到大',
  `remark` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` timestamp(0) NULL DEFAULT NULL,
  `updated_at` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '权限列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_region
-- ----------------------------
DROP TABLE IF EXISTS `s_region`;
CREATE TABLE `s_region`  (
  `id` int(11) NOT NULL,
  `parent_id` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pinyin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `first_letter` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `i_region_parent`(`parent_id`) USING BTREE,
  INDEX `idx_city_name`(`name`(3)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_role_has_permissions
-- ----------------------------
DROP TABLE IF EXISTS `s_role_has_permissions`;
CREATE TABLE `s_role_has_permissions`  (
  `permission_id` int(10) UNSIGNED NOT NULL COMMENT '权限ID',
  `role_id` int(10) UNSIGNED NOT NULL COMMENT '角色ID',
  `model_type` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型类型(用户获取用户权限,主要用于PHP)',
  PRIMARY KEY (`permission_id`, `role_id`) USING BTREE,
  INDEX `fk_s_acl_has_s_role_s_role1_idx`(`role_id`) USING BTREE,
  INDEX `fk_s_acl_has_s_role_s_acl1_idx`(`permission_id`) USING BTREE,
  CONSTRAINT `s_role_has_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `s_permissions` (`permission_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `s_role_has_permissions_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `s_roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色与权限的对应表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_roles
-- ----------------------------
DROP TABLE IF EXISTS `s_roles`;
CREATE TABLE `s_roles`  (
  `role_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名字',
  `show_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示名字',
  `guard_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'api' COMMENT '保护的类型(web,api,主要用于PHP)',
  `type` int(11) NULL DEFAULT NULL COMMENT '角色的类型，1：管理员角色，2：其他',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态，1：可用，0：冻结',
  `remark` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '层级(0,1,2)(用于特殊场合),比如:用户自己又有自己的分配权限',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父级ID(用于特殊场合),比如:用户自己又有自己的分配权限',
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_user_token
-- ----------------------------
DROP TABLE IF EXISTS `s_user_token`;
CREATE TABLE `s_user_token`  (
  `user_id` int(11) NOT NULL COMMENT '主键ID',
  `token` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户Token',
  `type` smallint(3) NULL DEFAULT 2 COMMENT '类型(1、admin  2、user)',
  `expired_at` int(11) NULL DEFAULT NULL COMMENT '过期时间',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户Token' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schedule_job_log
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_log`;
CREATE TABLE `schedule_job_log`  (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint(20) NOT NULL COMMENT '任务id',
  `bean_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
  `status` tinyint(4) NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '失败信息',
  `times` int(11) NOT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `job_id`(`job_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时任务日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for u_balance
-- ----------------------------
DROP TABLE IF EXISTS `u_balance`;
CREATE TABLE `u_balance`  (
  `balance_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `balance` decimal(20, 2) UNSIGNED NULL DEFAULT NULL COMMENT '账户余额',
  `type` smallint(3) NULL DEFAULT NULL COMMENT '余额类型:\\r\\n\\n1、可用余额\\r\\n2、USDT(不可用)\\r\\n\\n3、SE\\n\\r\\n4、YE\\r\\n\\n5、ETH\\r\\n6、USDT(可用)\\r\\n7、SYE',
  `version` int(255) NULL DEFAULT 0,
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`balance_id`) USING BTREE,
  UNIQUE INDEX `unique_user_id_type_idx`(`user_id`, `type`) USING BTREE,
  INDEX `fk_balance_u_user1_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for u_transaction_log
-- ----------------------------
DROP TABLE IF EXISTS `u_transaction_log`;
CREATE TABLE `u_transaction_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `block_number` int(11) NULL DEFAULT NULL COMMENT '区块号',
  `timestamp` int(11) NULL DEFAULT NULL COMMENT 'transaction时间戳',
  `transaction_hash` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'transaction识别号',
  `eth_from` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送以太坊地址',
  `eth_to` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收款以太坊地址',
  `token_amount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代币转账量',
  `eth_amount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ETH转账量',
  `status` smallint(4) NULL DEFAULT NULL COMMENT '状态\r\n0: pending\r\n1: confirmed\r\n2: failed',
  `type` smallint(4) NULL DEFAULT 0 COMMENT '转账类型:\n1、内部转账(转入)  \n2、外部转账(转出)',
  `tx_data` json NULL COMMENT '交易数据',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_u_transaction_log_u_user1_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户交易记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for u_user
-- ----------------------------
DROP TABLE IF EXISTS `u_user`;
CREATE TABLE `u_user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '推荐人ID或上级ID',
  `username` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '密码MD5+重叠混淆',
  `salt` char(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '加密盐',
  `wx_open_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '微信OPENID，用于快捷登录',
  `qq_open_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'QQ OPENID,用于快捷登录',
  `ali_pay_open_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '支付宝开放ID',
  `pay_password` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '支付密码',
  `pay_salt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nick_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '用户路径,用于实现层级关系(比如,无限级分销)',
  `hierarchy` smallint(255) NULL DEFAULT 1 COMMENT '用户所属层级',
  `ext` json NULL COMMENT '额外数据',
  `status` smallint(6) GENERATED ALWAYS AS (json_extract(`ext`,'$.status')) VIRTUAL COMMENT '状态' NULL,
  `core_model_id` int(11) NULL DEFAULT 14 COMMENT '核心模型ID',
  `user_type` int(6) GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.type'))) STORED COMMENT '用户类型' NULL,
  `last_login_at` int(11) GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.last_login_time'))) STORED COMMENT '最后登录时间' NULL,
  `invite_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邀请码',
  `created_at` int(11) NULL DEFAULT 0 COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `path_index`(`hierarchy`, `path`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '帐户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for u_user_favorites
-- ----------------------------
DROP TABLE IF EXISTS `u_user_favorites`;
CREATE TABLE `u_user_favorites`  (
  `collect_id` int(11) NOT NULL AUTO_INCREMENT,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `model_id` int(11) NULL DEFAULT NULL,
  `ext` json NULL,
  `created_at` int(255) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  `association_core_model_id` int(11) NULL DEFAULT NULL,
  `user_id` int(11) GENERATED ALWAYS AS (json_extract(`ext`,'$.user_id')) VIRTUAL NULL,
  `status` smallint(6) GENERATED ALWAYS AS (json_extract(`ext`,'$.status')) VIRTUAL NULL,
  PRIMARY KEY (`collect_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for u_wallet
-- ----------------------------
DROP TABLE IF EXISTS `u_wallet`;
CREATE TABLE `u_wallet`  (
  `wallet_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `password` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '钱包密码 (加密存储)',
  `dynamic_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密钥salt',
  `wallet_file` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '钱包文件名字',
  `address` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '钱包地址',
  `is_platform` tinyint(3) NULL DEFAULT 0 COMMENT '是否平台 (0、否  1、是)',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态 (1、正常 2、失效)',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`wallet_id`) USING BTREE,
  INDEX `fk_u_eth_wallet_u_user1_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户以太坊钱包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for u_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `u_withdraw`;
CREATE TABLE `u_withdraw`  (
  `withdraw_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `core_model_id` int(11) NULL DEFAULT NULL COMMENT '核心模型ID',
  `ext` json NULL COMMENT '扩展数据',
  `status` smallint(6) GENERATED ALWAYS AS (json_unquote(json_extract(`ext`,'$.status'))) STORED COMMENT '状态' NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`withdraw_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户提现' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_has_message
-- ----------------------------
DROP TABLE IF EXISTS `user_has_message`;
CREATE TABLE `user_has_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `core_model_id` int(11) NULL DEFAULT 0,
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `message_id` int(11) NULL DEFAULT NULL COMMENT '消息ID',
  `ext` json NULL COMMENT '额外消息',
  `status` smallint(6) NULL DEFAULT NULL COMMENT '状态',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_log
-- ----------------------------
DROP TABLE IF EXISTS `user_log`;
CREATE TABLE `user_log`  (
  `user_log_id` int(11) NOT NULL AUTO_INCREMENT,
  `core_model_id` int(11) NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `ext` json NULL,
  `event_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` int(11) NULL DEFAULT NULL,
  `updated_at` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`user_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Function structure for getChildList
-- ----------------------------
DROP FUNCTION IF EXISTS `getChildList`;
delimiter ;;
CREATE FUNCTION `getChildList`()

;;
delimiter ;

-- ----------------------------
-- Function structure for getParentList
-- ----------------------------
DROP FUNCTION IF EXISTS `getParentList`;
delimiter ;;
CREATE FUNCTION `getParentList`()

;;
delimiter ;

-- ----------------------------
-- Procedure structure for splitString
-- ----------------------------
DROP PROCEDURE IF EXISTS `splitString`;
delimiter ;;
CREATE PROCEDURE `splitString`()

;;
delimiter ;

-- ----------------------------
-- Triggers structure for table u_balance
-- ----------------------------
DROP TRIGGER IF EXISTS `check_balance_match`;
delimiter ;;
CREATE TRIGGER `check_balance_match` BEFORE UPDATE ON `u_balance` FOR EACH ROW begin
    DECLARE s_log_new_balance DOUBLE default 0.00;
	select new_balance into s_log_new_balance from s_log where user_id=NEW.user_id and core_model_id=200000 and balance_type=NEW.type and new_balance=NEW.balance order by id desc limit 1;
	if s_log_new_balance != NEW.balance THEN
		signal SQLSTATE '80000' SET MESSAGE_TEXT = "账户余额不匹配";
	end if;
end
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
