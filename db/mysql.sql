/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.123
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : 192.168.1.123:3306
 Source Schema         : geoxus

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 09/02/2020 17:18:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `config_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `core_model_id` int(45) NOT NULL COMMENT '核心模型ID',
  `param_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'key值',
  `param_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实际数据',
  `ext` json NULL COMMENT '其他额外数据',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` int(11) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`) USING BTREE
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
  `module_id` int(11) NULL DEFAULT 0 COMMENT '模块ID(商品,订单...)',
  `model_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型类型,用于统一JAVA、PHP命名',
  `model_show` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型显示名字',
  `model_identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型标识',
  `search_condition` json NULL COMMENT '搜索条件',
  `model_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '命名空间',
  `created_at` int(255) NULL DEFAULT 0 COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`model_id`) USING BTREE,
  UNIQUE INDEX `unique_model_identification`(`model_identification`) USING BTREE,
  INDEX `fk_module_id`(`module_id`) USING BTREE,
  CONSTRAINT `fk_module_id` FOREIGN KEY (`module_id`) REFERENCES `core_modules` (`module_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_model_attributes
-- ----------------------------
DROP TABLE IF EXISTS `core_model_attributes`;
CREATE TABLE `core_model_attributes`  (
  `model_attributes_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_attribute_field` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'ext' COMMENT '模型内的字段名字(如 : 项目信息....)',
  `parent_id` int(11) NULL DEFAULT 0 COMMENT '父级ID',
  `model_id` int(11) NULL DEFAULT NULL COMMENT '模型ID',
  `attribute_id` int(11) NULL DEFAULT NULL COMMENT '属性ID',
  `required` smallint(2) NULL DEFAULT 1 COMMENT '是否必须  0、不是  1、是',
  `show_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示名字',
  `validation_expression` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正则表达式',
  `force_validation` smallint(2) NULL DEFAULT 0 COMMENT '是否强制使用rule进行验证',
  `field_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段别名',
  `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段默认值',
  `created_at` int(11) NULL DEFAULT 0 COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`model_attributes_id`) USING BTREE,
  UNIQUE INDEX `unique_model_id_attribute_id`(`model_id`, `attribute_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for core_model_attributes_permission
-- ----------------------------
DROP TABLE IF EXISTS `core_model_attributes_permission`;
CREATE TABLE `core_model_attributes_permission`  (
  `attribute_permission_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_attributes_id` int(11) NULL DEFAULT NULL COMMENT '模型组的ID 比如: goods、order、contents',
  `core_model_id` int(11) NULL DEFAULT NULL COMMENT '模型ID',
  `attribute_id` int(11) NULL DEFAULT NULL COMMENT '属性ID',
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
  `parent_id` int(255) NULL DEFAULT NULL COMMENT '父级ID',
  `module_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块名字',
  `created_at` int(255) NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` int(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`module_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
