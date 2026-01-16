-- =============================================
-- 宗门任务系统表
-- =============================================

-- ---------------------------------------------
-- 1. 宗门任务模板表
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS `sect_task_template` (
    `template_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务模板ID',
    `sect_id` BIGINT NOT NULL COMMENT '宗门ID',
    `task_type` VARCHAR(20) NOT NULL COMMENT '任务类型: combat(战斗)/meditation(修炼)/collection(收集)/crafting(制作)',
    `task_name` VARCHAR(50) NOT NULL COMMENT '任务名称',
    `description` TEXT COMMENT '任务描述',
    `target_type` VARCHAR(50) COMMENT '目标类型: monster_level(妖兽境界)/item_name(物品名称)/meditation_count(打坐次数)',
    `target_value` VARCHAR(100) COMMENT '目标值: 妖兽境界等级/材料名称等',
    `target_count` INT NOT NULL DEFAULT 1 COMMENT '目标数量',
    `required_position` INT NOT NULL DEFAULT 1 COMMENT '所需职位等级(1-5)',
    `contribution_reward` INT NOT NULL COMMENT '贡献值奖励',
    `reputation_reward` INT NOT NULL DEFAULT 0 COMMENT '声望奖励',
    `daily_limit` INT NOT NULL DEFAULT 999 COMMENT '每日可完成次数限制',
    `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用(0-禁用 1-启用)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`template_id`),
    KEY `idx_sect_id` (`sect_id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_required_position` (`required_position`),
    CONSTRAINT `fk_task_template_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宗门任务模板表';

-- ---------------------------------------------
-- 2. 宗门任务进度表
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS `sect_task_progress` (
    `progress_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '进度记录ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `template_id` BIGINT NOT NULL COMMENT '任务模板ID',
    `current_progress` INT NOT NULL DEFAULT 0 COMMENT '当前进度',
    `status` VARCHAR(20) NOT NULL DEFAULT 'accepted' COMMENT '状态: accepted(进行中)/completed(已完成)/claimed(已领奖)',
    `accepted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接取时间',
    `completed_at` TIMESTAMP NULL COMMENT '完成时间',
    `claimed_at` TIMESTAMP NULL COMMENT '领奖时间',
    PRIMARY KEY (`progress_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_task_progress_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_task_progress_template` FOREIGN KEY (`template_id`) REFERENCES `sect_task_template` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宗门任务进度表';

-- ---------------------------------------------
-- 3. 角色宗门声望表
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS `character_sect_reputation` (
    `reputation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '声望记录ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `sect_id` BIGINT NOT NULL COMMENT '宗门ID',
    `reputation` INT NOT NULL DEFAULT 0 COMMENT '声望值',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`reputation_id`),
    UNIQUE KEY `uk_character_sect` (`character_id`, `sect_id`),
    KEY `idx_sect_id` (`sect_id`),
    KEY `idx_reputation` (`reputation` DESC),
    CONSTRAINT `fk_reputation_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_reputation_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色宗门声望表';

-- ---------------------------------------------
-- 初始任务数据
-- ---------------------------------------------

-- 青云门任务 (sect_id = 1)
INSERT INTO `sect_task_template`
    (`sect_id`, `task_type`, `task_name`, `description`, `target_type`, `target_value`, `target_count`, `required_position`, `contribution_reward`, `reputation_reward`)
VALUES
    -- 战斗任务（改为匹配新手怪物 realm_id=1）
    (1, 'combat', '击杀妖兽', '宗门附近出现了一批凡人境界妖兽，前往击杀它们。', 'monster_level', '1', 5, 1, 50, 10),
    -- 修炼任务
    (1, 'meditation', '打坐修炼', '每日坚持打坐修炼，稳固自身境界。', 'meditation_count', '1', 3, 1, 30, 5),
    -- 收集任务
    (1, 'collection', '采集雪莲', '为宗门收集一些珍贵的雪莲。', 'item_name', '雪莲', 10, 1, 30, 5),
    -- 制作任务
    (1, 'crafting', '炼制筑基丹', '炼制筑基丹供奉宗门。', 'item_name', '筑基丹', 3, 1, 50, 10);

-- 炼器阁任务 (sect_id = 2)
INSERT INTO `sect_task_template`
    (`sect_id`, `task_type`, `task_name`, `description`, `target_type`, `target_value`, `target_count`, `required_position`, `contribution_reward`, `reputation_reward`)
VALUES
    (2, 'combat', '击杀妖兽', '保护炼器阁，击杀来袭的凡人境界妖兽。', 'monster_level', '1', 5, 1, 50, 10),
    (2, 'meditation', '打坐修炼', '在炼器阁中静心修炼。', 'meditation_count', '1', 3, 1, 30, 5),
    (2, 'collection', '采集铁矿', '为炼器阁收集铁矿石。', 'item_name', '铁矿石', 15, 1, 30, 5),
    (2, 'crafting', '锻造玄铁剑', '为宗门锻造玄铁剑。', 'item_name', '玄铁剑', 2, 1, 60, 12);

-- 丹药堂任务 (sect_id = 3)
INSERT INTO `sect_task_template`
    (`sect_id`, `task_type`, `task_name`, `description`, `target_type`, `target_value`, `target_count`, `required_position`, `contribution_reward`, `reputation_reward`)
VALUES
    (3, 'combat', '击杀妖兽', '保护丹药堂，击杀来袭的凡人境界妖兽。', 'monster_level', '1', 5, 1, 50, 10),
    (3, 'meditation', '打坐修炼', '在丹药堂中修炼丹道。', 'meditation_count', '1', 3, 1, 30, 5),
    (3, 'collection', '采集灵草', '为丹药堂收集灵草。', 'item_name', '灵草', 12, 1, 30, 5),
    (3, 'crafting', '炼制回春丹', '为宗门炼制回春丹。', 'item_name', '回春丹', 5, 1, 50, 10);

-- 御兽宗任务 (sect_id = 4)
INSERT INTO `sect_task_template`
    (`sect_id`, `task_type`, `task_name`, `description`, `target_type`, `target_value`, `target_count`, `required_position`, `contribution_reward`, `reputation_reward`)
VALUES
    (4, 'combat', '驯服妖兽', '驯服一些凡人境界野生妖兽为宗门所用。', 'monster_level', '1', 5, 1, 50, 10),
    (4, 'meditation', '打坐修炼', '在御兽宗中修炼御兽之术。', 'meditation_count', '1', 3, 1, 30, 5),
    (4, 'collection', '采集兽粮', '为宗门的灵兽收集食物。', 'item_name', '灵兽粮', 20, 1, 30, 5),
    (4, 'crafting', '制作灵兽粮', '为宗门制作灵兽粮。', 'item_name', '高级灵兽粮', 3, 1, 40, 8);

-- 万法阁任务 (sect_id = 5)
INSERT INTO `sect_task_template`
    (`sect_id`, `task_type`, `task_name`, `description`, `target_type`, `target_value`, `target_count`, `required_position`, `contribution_reward`, `reputation_reward`)
VALUES
    (5, 'combat', '击杀妖兽', '保护万法阁，击杀来袭的凡人境界妖兽。', 'monster_level', '1', 5, 1, 50, 10),
    (5, 'meditation', '打坐修炼', '在万法阁中研习功法。', 'meditation_count', '1', 3, 1, 30, 5),
    (5, 'collection', '采集灵石', '为万法阁收集灵石。', 'item_name', '灵石', 10, 1, 30, 5),
    (5, 'crafting', '刻绘符箓', '为宗门刻绘符箓。', 'item_name', '火球符', 8, 1, 40, 8);
