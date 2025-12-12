-- 修复 wx_orders 表结构
-- 如果表已存在但缺少字段，使用以下SQL添加缺失字段

-- 检查并添加 order_no 字段（如果不存在）
ALTER TABLE `wx_orders` 
  ADD COLUMN `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（唯一）' AFTER `order_id`;

-- 如果 order_no 字段已存在但需要添加唯一索引
ALTER TABLE `wx_orders` 
  ADD UNIQUE KEY `uk_order_no` (`order_no`);

-- 检查并添加其他可能缺失的字段
ALTER TABLE `wx_orders` 
  ADD COLUMN IF NOT EXISTS `user_id` INT(11) NOT NULL COMMENT '用户ID',
  ADD COLUMN IF NOT EXISTS `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  ADD COLUMN IF NOT EXISTS `status` INT(11) NOT NULL DEFAULT 0 COMMENT '订单状态',
  ADD COLUMN IF NOT EXISTS `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  ADD COLUMN IF NOT EXISTS `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  ADD COLUMN IF NOT EXISTS `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
  ADD COLUMN IF NOT EXISTS `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  ADD COLUMN IF NOT EXISTS `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  ADD COLUMN IF NOT EXISTS `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 添加索引（如果不存在）
ALTER TABLE `wx_orders` 
  ADD INDEX IF NOT EXISTS `idx_user_id` (`user_id`),
  ADD INDEX IF NOT EXISTS `idx_status` (`status`),
  ADD INDEX IF NOT EXISTS `idx_create_time` (`create_time`);

