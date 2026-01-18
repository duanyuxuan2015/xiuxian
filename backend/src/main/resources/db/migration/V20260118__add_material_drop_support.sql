-- 怪物掉落材料支持 - 数据库迁移脚本
-- 日期: 2026-01-18
-- 描述: 扩展 monster_drop 表以支持材料掉落

-- 步骤1: 删除旧的外键约束（如果存在）
ALTER TABLE `monster_drop` DROP FOREIGN KEY IF EXISTS `fk_monster_drop_equipment`;

-- 步骤2: 删除旧的唯一索引（如果存在）
ALTER TABLE `monster_drop` DROP INDEX IF EXISTS `uk_monster_equipment`;

-- 步骤3: 添加新字段
ALTER TABLE `monster_drop`
ADD COLUMN `item_type` VARCHAR(20) NOT NULL DEFAULT 'equipment' COMMENT '物品类型(equipment/material/pill)' AFTER `monster_id`,
ADD COLUMN `item_id` BIGINT NOT NULL COMMENT '物品ID' AFTER `item_type`;

-- 步骤4: 数据迁移（将 equipment_id 复制到 item_id）
UPDATE `monster_drop` SET `item_id` = `equipment_id`, `item_type` = 'equipment' WHERE `equipment_id` IS NOT NULL;

-- 步骤5: 删除旧字段
ALTER TABLE `monster_drop` DROP COLUMN `equipment_id`;

-- 步骤6: 创建新的复合唯一索引
CREATE UNIQUE INDEX `uk_monster_item` ON `monster_drop` (`monster_id`, `item_type`, `item_id`);
