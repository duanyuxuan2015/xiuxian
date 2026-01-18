-- 为探索事件表添加奖励级别字段并移除probability字段
-- 执行时间: 2026-01-18

-- 添加级别字段
ALTER TABLE `exploration_event`
ADD COLUMN `level` INT NOT NULL DEFAULT 1 COMMENT '事件奖励级别(1-4)' AFTER `probability`;

-- 更新现有数据的级别
-- 采集事件设为1级（常见）
UPDATE `exploration_event` SET `level` = 1 WHERE `event_type` = '采集';

-- 战斗事件设为2级（普通）
UPDATE `exploration_event` SET `level` = 2 WHERE `event_type` = '战斗';

-- 机缘事件设为4级（史诗）
UPDATE `exploration_event` SET `level` = 4 WHERE `event_type` = '机缘';

-- 陷阱事件设为3级（稀有）
UPDATE `exploration_event` SET `level` = 3 WHERE `event_type` = '陷阱';

-- 无事事件设为1级（常见）
UPDATE `exploration_event` SET `level` = 1 WHERE `event_type` = '无事';

-- 删除probability字段（现在由级别决定触发概率）
ALTER TABLE `exploration_event` DROP COLUMN `probability`;
