-- ============================================
-- 仅添加缺失字段（不删除现有数据）
-- 如果表已存在且有数据，使用此脚本
-- ============================================

-- 修复 wx_orders 表 - 添加 order_no 字段
-- 注意：如果表中有数据，需要先为现有数据生成订单号
ALTER TABLE `wx_orders` 
  ADD COLUMN `order_no` VARCHAR(32) NULL COMMENT '订单号（唯一）' AFTER `order_id`;

-- 为现有数据生成订单号（如果有数据）
UPDATE `wx_orders` 
SET `order_no` = CONCAT(DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(order_id, 6, '0'))
WHERE `order_no` IS NULL;

-- 设置 order_no 为 NOT NULL 并添加唯一索引
ALTER TABLE `wx_orders` 
  MODIFY COLUMN `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（唯一）',
  ADD UNIQUE KEY `uk_order_no` (`order_no`);

-- 添加其他可能缺失的字段（如果不存在会报错，可以忽略）
-- 注意：MySQL 5.7 不支持 IF NOT EXISTS，需要手动检查

-- 检查并添加 user_id（如果不存在）
-- ALTER TABLE `wx_orders` ADD COLUMN `user_id` INT(11) NOT NULL COMMENT '用户ID';

-- 检查并添加 total_amount（如果不存在）
-- ALTER TABLE `wx_orders` ADD COLUMN `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额';

-- 检查并添加 status（如果不存在）
-- ALTER TABLE `wx_orders` ADD COLUMN `status` INT(11) NOT NULL DEFAULT 0 COMMENT '订单状态';

-- 检查并添加收货信息字段（如果不存在）
-- ALTER TABLE `wx_orders` 
--   ADD COLUMN `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
--   ADD COLUMN `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
--   ADD COLUMN `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
--   ADD COLUMN `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注';

-- 检查并添加时间字段（如果不存在）
-- ALTER TABLE `wx_orders` 
--   ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

