-- 为探索区域表添加体力消耗字段
-- 执行时间: 2026-01-18

-- 添加体力消耗字段
ALTER TABLE `exploration_area`
ADD COLUMN `stamina_cost` INT NOT NULL DEFAULT 10 COMMENT '探索消耗体力' AFTER `spirit_cost`;

-- 更新现有数据的体力消耗（根据危险等级设置）
-- 危险等级1-3：消耗10体力
-- 危险等级4-6：消耗20体力
-- 危险等级7-9：消耗30体力
-- 危险等级10：消耗50体力
UPDATE `exploration_area` SET `stamina_cost` = 10 WHERE `danger_level` BETWEEN 1 AND 3;
UPDATE `exploration_area` SET `stamina_cost` = 20 WHERE `danger_level` BETWEEN 4 AND 6;
UPDATE `exploration_area` SET `stamina_cost` = 30 WHERE `danger_level` BETWEEN 7 AND 9;
UPDATE `exploration_area` SET `stamina_cost` = 50 WHERE `danger_level` >= 10;
