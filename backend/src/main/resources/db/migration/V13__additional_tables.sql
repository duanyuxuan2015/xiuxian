-- =============================================
-- 补充表结构 - Phase 7-12 新增表
-- =============================================

-- 角色表补充字段
ALTER TABLE `character` ADD COLUMN `character_name` VARCHAR(50) DEFAULT NULL COMMENT '角色名称' AFTER `player_name`;
ALTER TABLE `character` ADD COLUMN `current_health` INT DEFAULT NULL COMMENT '当前生命值' AFTER `health_max`;
ALTER TABLE `character` ADD COLUMN `current_spirit` INT DEFAULT NULL COMMENT '当前灵力' AFTER `current_health`;
ALTER TABLE `character` ADD COLUMN `forge_level` TINYINT NOT NULL DEFAULT 1 COMMENT '锻造等级' AFTER `forging_exp`;

-- =============================================
-- 通用背包表 (替代分散的背包表)
-- =============================================
CREATE TABLE IF NOT EXISTS `character_inventory` (
    `inventory_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '背包记录ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `item_type` VARCHAR(20) NOT NULL COMMENT '物品类型(material/pill/equipment)',
    `item_id` BIGINT NOT NULL COMMENT '物品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `acquired_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    PRIMARY KEY (`inventory_id`),
    UNIQUE KEY `uk_character_item` (`character_id`, `item_type`, `item_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_item_type` (`item_type`),
    CONSTRAINT `fk_inventory_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色通用背包表';

-- =============================================
-- 宗门成员表
-- =============================================
CREATE TABLE IF NOT EXISTS `sect_member` (
    `member_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员记录ID',
    `sect_id` BIGINT NOT NULL COMMENT '宗门ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `position` VARCHAR(20) NOT NULL DEFAULT '弟子' COMMENT '职位',
    `contribution` INT NOT NULL DEFAULT 0 COMMENT '总贡献值',
    `weekly_contribution` INT NOT NULL DEFAULT 0 COMMENT '本周贡献值',
    `joined_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `uk_character_sect` (`character_id`),
    KEY `idx_sect_id` (`sect_id`),
    KEY `idx_contribution` (`contribution` DESC),
    CONSTRAINT `fk_sect_member_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`),
    CONSTRAINT `fk_sect_member_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宗门成员表';

-- =============================================
-- 宗门商店物品表
-- =============================================
CREATE TABLE IF NOT EXISTS `sect_shop_item` (
    `item_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `sect_id` BIGINT NOT NULL COMMENT '宗门ID',
    `item_type` VARCHAR(20) NOT NULL COMMENT '物品类型',
    `ref_item_id` BIGINT NOT NULL COMMENT '引用物品ID',
    `item_name` VARCHAR(50) NOT NULL COMMENT '商品名称',
    `price` INT NOT NULL COMMENT '贡献值价格',
    `stock_limit` INT NOT NULL DEFAULT 999 COMMENT '库存上限',
    `current_stock` INT NOT NULL DEFAULT 999 COMMENT '当前库存',
    `required_position` INT NOT NULL DEFAULT 1 COMMENT '所需职位等级',
    PRIMARY KEY (`item_id`),
    KEY `idx_sect_id` (`sect_id`),
    KEY `idx_item_type` (`item_type`),
    CONSTRAINT `fk_shop_item_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宗门商店物品表';

-- =============================================
-- 探索区域表
-- =============================================
CREATE TABLE IF NOT EXISTS `exploration_area` (
    `area_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '区域ID',
    `area_name` VARCHAR(50) NOT NULL COMMENT '区域名称',
    `description` TEXT COMMENT '区域描述',
    `required_realm_level` INT NOT NULL DEFAULT 1 COMMENT '所需境界等级',
    `danger_level` INT NOT NULL DEFAULT 1 COMMENT '危险等级(1-10)',
    `spirit_cost` INT NOT NULL DEFAULT 10 COMMENT '灵力消耗',
    `base_explore_time` INT NOT NULL DEFAULT 60 COMMENT '基础探索时间(秒)',
    PRIMARY KEY (`area_id`),
    KEY `idx_required_realm` (`required_realm_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索区域表';

-- =============================================
-- 探索事件配置表 (覆盖原有的exploration_event)
-- =============================================
DROP TABLE IF EXISTS `exploration_event_reward`;
DROP TABLE IF EXISTS `exploration_event`;

CREATE TABLE IF NOT EXISTS `exploration_event` (
    `event_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '事件ID',
    `area_id` BIGINT NOT NULL COMMENT '区域ID',
    `event_type` VARCHAR(20) NOT NULL COMMENT '事件类型(采集/战斗/机缘/陷阱/无事)',
    `event_name` VARCHAR(50) NOT NULL COMMENT '事件名称',
    `description` TEXT COMMENT '事件描述',
    `probability` INT NOT NULL DEFAULT 10 COMMENT '触发概率',
    `reward_type` VARCHAR(20) DEFAULT NULL COMMENT '奖励类型',
    `reward_id` BIGINT DEFAULT NULL COMMENT '奖励物品ID',
    `reward_quantity_min` INT DEFAULT 1 COMMENT '奖励数量下限',
    `reward_quantity_max` INT DEFAULT 1 COMMENT '奖励数量上限',
    `monster_id` BIGINT DEFAULT NULL COMMENT '战斗事件对应怪物ID',
    PRIMARY KEY (`event_id`),
    KEY `idx_area_id` (`area_id`),
    KEY `idx_event_type` (`event_type`),
    CONSTRAINT `fk_event_area` FOREIGN KEY (`area_id`) REFERENCES `exploration_area` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索事件配置表';

-- =============================================
-- 探索记录表
-- =============================================
CREATE TABLE IF NOT EXISTS `exploration_record` (
    `record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `area_id` BIGINT NOT NULL COMMENT '区域ID',
    `event_id` BIGINT DEFAULT NULL COMMENT '触发事件ID',
    `event_type` VARCHAR(20) NOT NULL COMMENT '事件类型',
    `result` VARCHAR(200) DEFAULT NULL COMMENT '结果描述',
    `rewards` VARCHAR(200) DEFAULT NULL COMMENT '获得奖励',
    `experience_gained` INT NOT NULL DEFAULT 0 COMMENT '获得经验',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '探索时间',
    PRIMARY KEY (`record_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_area_id` (`area_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_explore_record_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_explore_record_area` FOREIGN KEY (`area_id`) REFERENCES `exploration_area` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索记录表';

-- =============================================
-- 宗门表补充字段
-- =============================================
ALTER TABLE `sect` ADD COLUMN `specialty` VARCHAR(50) DEFAULT NULL COMMENT '宗门特长' AFTER `description`;
ALTER TABLE `sect` ADD COLUMN  `required_realm_level` INT NOT NULL DEFAULT 1 COMMENT '加入所需境界' AFTER `specialty`;
