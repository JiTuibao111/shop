-- ============================================
-- 数据库表创建/修复脚本
-- 用于修复或创建购物车、订单相关表
-- ============================================

-- 1. 创建或修复购物车表
DROP TABLE IF EXISTS `wx_cart`;
CREATE TABLE `wx_cart` (
  `cart_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `goods_id` INT(11) NOT NULL COMMENT '商品ID',
  `quantity` INT(11) NOT NULL DEFAULT 1 COMMENT '商品数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`cart_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 2. 创建或修复订单表
DROP TABLE IF EXISTS `wx_orders`;
CREATE TABLE `wx_orders` (
  `order_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（唯一）',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status` INT(11) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 3. 创建或修复订单项表
DROP TABLE IF EXISTS `wx_order_item`;
CREATE TABLE `wx_order_item` (
  `item_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` INT(11) NOT NULL COMMENT '订单ID',
  `goods_id` INT(11) NOT NULL COMMENT '商品ID',
  `quantity` INT(11) NOT NULL COMMENT '商品数量',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价（下单时的价格）',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  PRIMARY KEY (`item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

