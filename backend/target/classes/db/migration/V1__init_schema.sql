-- =============================================
-- 凡人修仙文字游戏 - 数据库初始化脚本
-- Database: MySQL 8.x
-- Character Set: utf8mb4
-- =============================================

-- 创建数据库（如果不存在）
-- CREATE DATABASE IF NOT EXISTS xiuxian CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE xiuxian;

-- =============================================
-- 1. realm (境界配置表)
-- =============================================
CREATE TABLE IF NOT EXISTS `realm` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '境界ID',
    `realm_name` VARCHAR(50) NOT NULL COMMENT '境界名称',
    `realm_level` INT NOT NULL COMMENT '境界等级(排序)',
    `sub_levels` INT NOT NULL DEFAULT 9 COMMENT '子层数量',
    `required_exp` BIGINT NOT NULL DEFAULT 100 COMMENT '突破所需经验',
    `breakthrough_rate` INT NOT NULL DEFAULT 50 COMMENT '基础突破成功率(%)',
    `hp_bonus` INT NOT NULL DEFAULT 0 COMMENT '生命值加成',
    `sp_bonus` INT NOT NULL DEFAULT 0 COMMENT '灵力加成',
    `stamina_bonus` INT NOT NULL DEFAULT 0 COMMENT '体力上限加成',
    `attack_bonus` INT NOT NULL DEFAULT 0 COMMENT '攻击力加成',
    `defense_bonus` INT NOT NULL DEFAULT 0 COMMENT '防御力加成',
    `lifespan_bonus` INT NOT NULL DEFAULT 0 COMMENT '寿命加成(年)',
    `level_up_points` INT NOT NULL DEFAULT 5 COMMENT '每层提升获得属性点数',
    `realm_stage` VARCHAR(20) NOT NULL DEFAULT 'MORTAL' COMMENT '阶段(MORTAL/IMMORTAL)',
    `description` TEXT COMMENT '境界描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_realm_level` (`realm_level`),
    KEY `idx_realm_stage` (`realm_stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='境界配置表';

-- =============================================
-- 2. sect (宗门表)
-- =============================================
CREATE TABLE IF NOT EXISTS `sect` (
    `sect_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '宗门唯一标识',
    `sect_name` VARCHAR(50) NOT NULL COMMENT '宗门名称',
    `sect_type` VARCHAR(20) NOT NULL COMMENT '宗门类型',
    `description` TEXT COMMENT '宗门描述',
    `skill_focus` VARCHAR(50) NOT NULL COMMENT '特色技能类型',
    `join_requirement` VARCHAR(20) NOT NULL DEFAULT '炼气期' COMMENT '加入要求(境界)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`sect_id`),
    UNIQUE KEY `uk_sect_name` (`sect_name`),
    KEY `idx_sect_type` (`sect_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宗门表';

-- =============================================
-- 3. character (角色表)
-- =============================================
CREATE TABLE IF NOT EXISTS `character` (
    `character_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色唯一标识',
    `player_name` VARCHAR(20) NOT NULL COMMENT '角色姓名',
    `realm_id` INT NOT NULL DEFAULT 1 COMMENT '当前境界ID',
    `realm_level` TINYINT NOT NULL DEFAULT 1 COMMENT '当前境界层次(1-9)',
    `experience` BIGINT NOT NULL DEFAULT 0 COMMENT '当前经验值',
    `available_points` SMALLINT NOT NULL DEFAULT 0 COMMENT '可用加点点数',
    `spiritual_power` INT NOT NULL DEFAULT 0 COMMENT '当前灵气值',
    `spiritual_power_max` INT NOT NULL DEFAULT 100 COMMENT '灵气上限',
    `stamina` SMALLINT NOT NULL DEFAULT 100 COMMENT '当前体力',
    `stamina_max` SMALLINT NOT NULL DEFAULT 100 COMMENT '体力上限',
    `health` INT NOT NULL DEFAULT 100 COMMENT '当前气血值',
    `health_max` INT NOT NULL DEFAULT 100 COMMENT '气血上限',
    `mindset` SMALLINT NOT NULL DEFAULT 50 COMMENT '当前心境值',
    `constitution` INT NOT NULL DEFAULT 5 COMMENT '体质属性',
    `spirit` INT NOT NULL DEFAULT 5 COMMENT '精神属性',
    `comprehension` INT NOT NULL DEFAULT 5 COMMENT '悟性属性',
    `luck` INT NOT NULL DEFAULT 5 COMMENT '机缘属性',
    `fortune` INT NOT NULL DEFAULT 5 COMMENT '气运属性',
    `spirit_stones` BIGINT NOT NULL DEFAULT 0 COMMENT '灵石数量',
    `sect_id` BIGINT DEFAULT NULL COMMENT '所属宗门ID',
    `sect_position` VARCHAR(20) DEFAULT NULL COMMENT '宗门职位',
    `contribution` INT NOT NULL DEFAULT 0 COMMENT '宗门贡献度',
    `reputation` INT NOT NULL DEFAULT 0 COMMENT '宗门声望',
    `alchemy_level` TINYINT NOT NULL DEFAULT 1 COMMENT '炼丹等级',
    `alchemy_exp` INT NOT NULL DEFAULT 0 COMMENT '炼丹经验',
    `forging_level` TINYINT NOT NULL DEFAULT 1 COMMENT '炼器等级',
    `forging_exp` INT NOT NULL DEFAULT 0 COMMENT '炼器经验',
    `current_state` VARCHAR(20) NOT NULL DEFAULT '闲置' COMMENT '当前状态',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`character_id`),
    KEY `idx_player_name` (`player_name`),
    KEY `idx_realm_id` (`realm_id`),
    KEY `idx_sect_id` (`sect_id`),
    CONSTRAINT `fk_character_realm` FOREIGN KEY (`realm_id`) REFERENCES `realm` (`id`),
    CONSTRAINT `fk_character_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =============================================
-- 4. pill (丹药表)
-- =============================================
CREATE TABLE IF NOT EXISTS `pill` (
    `pill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '丹药唯一标识',
    `pill_name` VARCHAR(50) NOT NULL COMMENT '丹药名称',
    `pill_tier` TINYINT NOT NULL DEFAULT 1 COMMENT '丹药阶位(1-10)',
    `quality` VARCHAR(20) NOT NULL DEFAULT '普通' COMMENT '丹药品质',
    `effect_type` VARCHAR(20) NOT NULL COMMENT '效果类型',
    `effect_value` INT NOT NULL DEFAULT 0 COMMENT '效果数值',
    `duration` INT DEFAULT NULL COMMENT '持续时间(秒)',
    `stack_limit` INT NOT NULL DEFAULT 999 COMMENT '堆叠上限',
    `spirit_stones` INT NOT NULL DEFAULT 0 COMMENT '出售价格(灵石)',
    `description` TEXT COMMENT '丹药描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`pill_id`),
    KEY `idx_pill_tier` (`pill_tier`),
    KEY `idx_pill_quality` (`quality`),
    KEY `idx_effect_type` (`effect_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='丹药表';

-- =============================================
-- 5. material (材料表)
-- =============================================
CREATE TABLE IF NOT EXISTS `material` (
    `material_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '材料唯一标识',
    `material_name` VARCHAR(50) NOT NULL COMMENT '材料名称',
    `material_type` VARCHAR(20) NOT NULL COMMENT '材料类型',
    `material_tier` TINYINT NOT NULL DEFAULT 1 COMMENT '材料阶位(1-10)',
    `quality` VARCHAR(20) NOT NULL DEFAULT '普通' COMMENT '材料品质',
    `stack_limit` INT NOT NULL DEFAULT 999 COMMENT '堆叠上限',
    `spirit_stones` INT NOT NULL DEFAULT 0 COMMENT '出售价格(灵石)',
    `description` TEXT COMMENT '材料描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`material_id`),
    KEY `idx_material_type` (`material_type`),
    KEY `idx_material_tier` (`material_tier`),
    KEY `idx_material_quality` (`quality`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='材料表';

-- =============================================
-- 6. character_pill (角色丹药背包)
-- =============================================
CREATE TABLE IF NOT EXISTS `character_pill` (
    `character_pill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `pill_id` BIGINT NOT NULL COMMENT '丹药ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '拥有数量',
    `obtained_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    PRIMARY KEY (`character_pill_id`),
    UNIQUE KEY `uk_character_pill` (`character_id`, `pill_id`),
    KEY `idx_character_id` (`character_id`),
    CONSTRAINT `fk_character_pill_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_character_pill_pill` FOREIGN KEY (`pill_id`) REFERENCES `pill` (`pill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色丹药背包';

-- =============================================
-- 7. character_material (角色材料背包)
-- =============================================
CREATE TABLE IF NOT EXISTS `character_material` (
    `character_material_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `material_id` BIGINT NOT NULL COMMENT '材料ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '拥有数量',
    `obtained_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    PRIMARY KEY (`character_material_id`),
    UNIQUE KEY `uk_character_material` (`character_id`, `material_id`),
    KEY `idx_character_id` (`character_id`),
    CONSTRAINT `fk_character_material_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_character_material_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色材料背包';

-- =============================================
-- 8. pill_recipe (丹方表)
-- =============================================
CREATE TABLE IF NOT EXISTS `pill_recipe` (
    `recipe_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '丹方唯一标识',
    `recipe_name` VARCHAR(50) NOT NULL COMMENT '丹方名称',
    `pill_id` BIGINT NOT NULL COMMENT '产出丹药ID',
    `output_quantity` TINYINT NOT NULL DEFAULT 1 COMMENT '产出数量',
    `base_success_rate` INT NOT NULL DEFAULT 50 COMMENT '基础成功率(%)',
    `alchemy_level_required` TINYINT NOT NULL DEFAULT 1 COMMENT '所需炼丹等级',
    `spiritual_cost` INT NOT NULL DEFAULT 50 COMMENT '消耗灵力',
    `stamina_cost` TINYINT NOT NULL DEFAULT 10 COMMENT '消耗体力',
    `duration` INT NOT NULL DEFAULT 300 COMMENT '炼制时长(秒)',
    `recipe_tier` TINYINT NOT NULL DEFAULT 1 COMMENT '丹方阶位(1-10)',
    `unlock_method` VARCHAR(20) NOT NULL COMMENT '获得途径',
    `unlock_cost` INT NOT NULL DEFAULT 0 COMMENT '学习消耗',
    `description` TEXT COMMENT '丹方描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`recipe_id`),
    KEY `idx_pill_id` (`pill_id`),
    KEY `idx_recipe_tier` (`recipe_tier`),
    KEY `idx_alchemy_level` (`alchemy_level_required`),
    CONSTRAINT `fk_pill_recipe_pill` FOREIGN KEY (`pill_id`) REFERENCES `pill` (`pill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='丹方表';

-- =============================================
-- 9. pill_recipe_material (丹方材料)
-- =============================================
CREATE TABLE IF NOT EXISTS `pill_recipe_material` (
    `recipe_material_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
    `recipe_id` BIGINT NOT NULL COMMENT '丹方ID',
    `material_id` BIGINT NOT NULL COMMENT '材料ID',
    `quantity_required` INT NOT NULL DEFAULT 1 COMMENT '所需数量',
    `is_main_material` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主材料',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`recipe_material_id`),
    UNIQUE KEY `uk_recipe_material` (`recipe_id`, `material_id`),
    KEY `idx_recipe_id` (`recipe_id`),
    KEY `idx_material_id` (`material_id`),
    CONSTRAINT `fk_pill_recipe_material_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `pill_recipe` (`recipe_id`),
    CONSTRAINT `fk_pill_recipe_material_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='丹方材料关联表';

-- =============================================
-- 10. alchemy_record (炼丹记录)
-- =============================================
CREATE TABLE IF NOT EXISTS `alchemy_record` (
    `alchemy_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '炼丹记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `recipe_id` BIGINT NOT NULL COMMENT '丹方ID',
    `is_success` TINYINT NOT NULL DEFAULT 0 COMMENT '是否成功',
    `success_rate` INT NOT NULL DEFAULT 50 COMMENT '实际成功率(%)',
    `output_quantity` TINYINT NOT NULL DEFAULT 0 COMMENT '产出数量',
    `output_quality` VARCHAR(20) DEFAULT NULL COMMENT '产出品质',
    `spiritual_consumed` INT NOT NULL DEFAULT 0 COMMENT '消耗灵力',
    `stamina_consumed` TINYINT NOT NULL DEFAULT 0 COMMENT '消耗体力',
    `alchemy_level_before` TINYINT NOT NULL DEFAULT 1 COMMENT '炼丹前等级',
    `alchemy_level_after` TINYINT NOT NULL DEFAULT 1 COMMENT '炼丹后等级',
    `exp_gained` INT NOT NULL DEFAULT 0 COMMENT '获得炼丹经验',
    `alchemy_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '炼丹时间',
    PRIMARY KEY (`alchemy_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_recipe_id` (`recipe_id`),
    KEY `idx_alchemy_time` (`alchemy_time`),
    KEY `idx_is_success` (`is_success`),
    CONSTRAINT `fk_alchemy_record_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_alchemy_record_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `pill_recipe` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='炼丹记录表';

-- =============================================
-- 11. skill (技能表)
-- =============================================
CREATE TABLE IF NOT EXISTS `skill` (
    `skill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能唯一标识',
    `skill_name` VARCHAR(50) NOT NULL COMMENT '技能名称',
    `function_type` VARCHAR(20) NOT NULL COMMENT '功能分类',
    `element_type` VARCHAR(20) DEFAULT NULL COMMENT '元素分类',
    `base_damage` INT DEFAULT 0 COMMENT '技能基础伤害(1级)',
    `skill_multiplier` DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '技能倍率(1级)',
    `spiritual_cost` SMALLINT NOT NULL DEFAULT 10 COMMENT '灵气消耗(1级)',
    `damage_growth_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.10 COMMENT '伤害成长率',
    `multiplier_growth` DECIMAL(4,2) NOT NULL DEFAULT 0.05 COMMENT '倍率成长',
    `spiritual_cost_growth` TINYINT NOT NULL DEFAULT 2 COMMENT '灵气消耗成长',
    `description` TEXT COMMENT '技能描述',
    `tier` TINYINT NOT NULL DEFAULT 1 COMMENT '技能阶位(1-8)',
    `sect_id` BIGINT DEFAULT NULL COMMENT '宗门ID(专属技能)',
    `unlock_method` VARCHAR(20) NOT NULL COMMENT '学习途径',
    `cost` INT NOT NULL DEFAULT 0 COMMENT '学习消耗',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`skill_id`),
    KEY `idx_function_type` (`function_type`),
    KEY `idx_element_type` (`element_type`),
    KEY `idx_sect_id` (`sect_id`),
    CONSTRAINT `fk_skill_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能表';

-- =============================================
-- 12. character_skill (角色已学技能)
-- =============================================
CREATE TABLE IF NOT EXISTS `character_skill` (
    `character_skill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `skill_id` BIGINT NOT NULL COMMENT '技能ID',
    `proficiency` INT NOT NULL DEFAULT 0 COMMENT '技能熟练度',
    `skill_level` TINYINT NOT NULL DEFAULT 1 COMMENT '技能等级(1-99)',
    `current_damage` INT NOT NULL DEFAULT 0 COMMENT '当前等级实际伤害',
    `current_multiplier` DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '当前等级实际倍率',
    `current_spiritual_cost` SMALLINT NOT NULL DEFAULT 10 COMMENT '当前等级实际灵气消耗',
    `is_equipped` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已装备',
    `equipment_slot` VARCHAR(20) DEFAULT NULL COMMENT '装备槽位',
    `learned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '学习时间',
    PRIMARY KEY (`character_skill_id`),
    UNIQUE KEY `uk_character_skill` (`character_id`, `skill_id`),
    KEY `idx_character_id` (`character_id`),
    CONSTRAINT `fk_character_skill_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_character_skill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色已学技能表';

-- =============================================
-- 13. equipment (装备表)
-- =============================================
CREATE TABLE IF NOT EXISTS `equipment` (
    `equipment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '装备唯一标识',
    `equipment_name` VARCHAR(50) NOT NULL COMMENT '装备名称',
    `equipment_type` VARCHAR(20) NOT NULL COMMENT '装备类型',
    `quality` VARCHAR(20) NOT NULL DEFAULT '普通' COMMENT '装备品质',
    `base_score` INT NOT NULL COMMENT '品质基础分',
    `attack_power` INT DEFAULT NULL COMMENT '攻击力',
    `defense_power` INT DEFAULT NULL COMMENT '防御力',
    `health_bonus` INT DEFAULT NULL COMMENT '生命值加成',
    `critical_rate` TINYINT DEFAULT NULL COMMENT '暴击率(%)',
    `speed_bonus` TINYINT DEFAULT NULL COMMENT '速度加成',
    `physical_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '物理抗性(%)',
    `ice_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '冰系抗性(%)',
    `fire_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '火系抗性(%)',
    `lightning_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '雷系抗性(%)',
    `enhancement_level` TINYINT NOT NULL DEFAULT 0 COMMENT '强化等级',
    `gem_slot_count` TINYINT NOT NULL DEFAULT 0 COMMENT '宝石槽孔数',
    `special_effects` TEXT COMMENT '特殊效果(JSON格式)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`equipment_id`),
    KEY `idx_equipment_type` (`equipment_type`),
    KEY `idx_quality` (`quality`),
    KEY `idx_enhancement_level` (`enhancement_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装备表';

-- =============================================
-- 14. character_equipment (角色已装备装备)
-- =============================================
CREATE TABLE IF NOT EXISTS `character_equipment` (
    `character_equipment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `equipment_id` BIGINT NOT NULL COMMENT '装备ID',
    `equipment_slot` VARCHAR(20) NOT NULL COMMENT '装备槽位',
    `equipped_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '装备时间',
    PRIMARY KEY (`character_equipment_id`),
    UNIQUE KEY `uk_character_slot` (`character_id`, `equipment_slot`),
    KEY `idx_character_id` (`character_id`),
    CONSTRAINT `fk_character_equipment_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_character_equipment_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色已装备装备表';

-- =============================================
-- 15. equipment_recipe (装备图纸表)
-- =============================================
CREATE TABLE IF NOT EXISTS `equipment_recipe` (
    `recipe_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图纸唯一标识',
    `recipe_name` VARCHAR(50) NOT NULL COMMENT '图纸名称',
    `equipment_id` BIGINT NOT NULL COMMENT '产出装备ID',
    `base_success_rate` INT NOT NULL DEFAULT 50 COMMENT '基础成功率(%)',
    `forging_level_required` TINYINT NOT NULL DEFAULT 1 COMMENT '所需炼器等级',
    `spiritual_cost` INT NOT NULL DEFAULT 100 COMMENT '消耗灵力',
    `stamina_cost` TINYINT NOT NULL DEFAULT 20 COMMENT '消耗体力',
    `duration` INT NOT NULL DEFAULT 600 COMMENT '锻造时长(秒)',
    `recipe_tier` TINYINT NOT NULL DEFAULT 1 COMMENT '图纸阶位(1-10)',
    `unlock_method` VARCHAR(20) NOT NULL COMMENT '获得途径',
    `unlock_cost` INT NOT NULL DEFAULT 0 COMMENT '学习消耗',
    `description` TEXT COMMENT '图纸描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`recipe_id`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_recipe_tier` (`recipe_tier`),
    KEY `idx_forging_level` (`forging_level_required`),
    CONSTRAINT `fk_equipment_recipe_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装备图纸表';

-- =============================================
-- 16. equipment_recipe_material (装备图纸材料)
-- =============================================
CREATE TABLE IF NOT EXISTS `equipment_recipe_material` (
    `recipe_material_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
    `recipe_id` BIGINT NOT NULL COMMENT '图纸ID',
    `material_id` BIGINT NOT NULL COMMENT '材料ID',
    `quantity_required` INT NOT NULL DEFAULT 1 COMMENT '所需数量',
    `is_main_material` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主材料',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`recipe_material_id`),
    UNIQUE KEY `uk_equipment_recipe_material` (`recipe_id`, `material_id`),
    KEY `idx_recipe_id` (`recipe_id`),
    KEY `idx_material_id` (`material_id`),
    CONSTRAINT `fk_equipment_recipe_material_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `equipment_recipe` (`recipe_id`),
    CONSTRAINT `fk_equipment_recipe_material_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装备图纸材料关联表';

-- =============================================
-- 17. forge_record (锻造记录)
-- =============================================
CREATE TABLE IF NOT EXISTS `forge_record` (
    `forge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '锻造记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `recipe_id` BIGINT NOT NULL COMMENT '图纸ID',
    `is_success` TINYINT NOT NULL DEFAULT 0 COMMENT '是否成功',
    `success_rate` INT NOT NULL DEFAULT 50 COMMENT '实际成功率(%)',
    `output_quality` VARCHAR(20) DEFAULT NULL COMMENT '产出品质',
    `enhancement_level` TINYINT NOT NULL DEFAULT 0 COMMENT '产出强化等级',
    `spiritual_consumed` INT NOT NULL DEFAULT 0 COMMENT '消耗灵力',
    `stamina_consumed` TINYINT NOT NULL DEFAULT 0 COMMENT '消耗体力',
    `forging_level_before` TINYINT NOT NULL DEFAULT 1 COMMENT '锻造前等级',
    `forging_level_after` TINYINT NOT NULL DEFAULT 1 COMMENT '锻造后等级',
    `exp_gained` INT NOT NULL DEFAULT 0 COMMENT '获得炼器经验',
    `forge_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锻造时间',
    PRIMARY KEY (`forge_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_recipe_id` (`recipe_id`),
    KEY `idx_forge_time` (`forge_time`),
    KEY `idx_is_success` (`is_success`),
    CONSTRAINT `fk_forge_record_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_forge_record_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `equipment_recipe` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='锻造记录表';

-- =============================================
-- 18. monster (妖兽表)
-- =============================================
CREATE TABLE IF NOT EXISTS `monster` (
    `monster_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '妖兽唯一标识',
    `monster_name` VARCHAR(50) NOT NULL COMMENT '妖兽名称',
    `realm_id` INT NOT NULL DEFAULT 1 COMMENT '妖兽境界ID',
    `monster_type` VARCHAR(20) NOT NULL DEFAULT '普通' COMMENT '妖兽类型',
    `speed` INT NOT NULL DEFAULT 10 COMMENT '速度',
    `hp` INT NOT NULL DEFAULT 100 COMMENT '生命力',
    `attack_power` INT NOT NULL DEFAULT 10 COMMENT '攻击力',
    `defense_power` INT NOT NULL DEFAULT 5 COMMENT '防御力',
    `attack_element` VARCHAR(20) NOT NULL DEFAULT '物理' COMMENT '攻击元素类型',
    `physical_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '物理抗性(%)',
    `ice_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '冰系抗性(%)',
    `fire_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '火系抗性(%)',
    `lightning_resist` TINYINT NOT NULL DEFAULT 0 COMMENT '雷系抗性(%)',
    `stamina_cost` TINYINT NOT NULL DEFAULT 10 COMMENT '体力消耗',
    `exp_reward` INT NOT NULL DEFAULT 50 COMMENT '经验值奖励',
    `spirit_stones_reward` INT NOT NULL DEFAULT 10 COMMENT '灵石奖励',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`monster_id`),
    KEY `idx_realm_id` (`realm_id`),
    KEY `idx_monster_type` (`monster_type`),
    KEY `idx_attack_element` (`attack_element`),
    CONSTRAINT `fk_monster_realm` FOREIGN KEY (`realm_id`) REFERENCES `realm` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='妖兽表';

-- =============================================
-- 19. monster_drop (怪物装备掉落配置)
-- =============================================
CREATE TABLE IF NOT EXISTS `monster_drop` (
    `monster_drop_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '掉落配置唯一标识',
    `monster_id` BIGINT NOT NULL COMMENT '怪物ID',
    `equipment_id` BIGINT NOT NULL COMMENT '装备ID',
    `drop_rate` DECIMAL(5,2) NOT NULL DEFAULT 10.00 COMMENT '掉落率(%)',
    `drop_quantity` TINYINT NOT NULL DEFAULT 1 COMMENT '掉落数量',
    `min_quality` VARCHAR(20) DEFAULT NULL COMMENT '最低品质要求',
    `max_quality` VARCHAR(20) DEFAULT NULL COMMENT '最高品质限制',
    `is_guaranteed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否必掉',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`monster_drop_id`),
    UNIQUE KEY `uk_monster_equipment` (`monster_id`, `equipment_id`),
    KEY `idx_monster_id` (`monster_id`),
    KEY `idx_equipment_id` (`equipment_id`),
    CONSTRAINT `fk_monster_drop_monster` FOREIGN KEY (`monster_id`) REFERENCES `monster` (`monster_id`),
    CONSTRAINT `fk_monster_drop_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='怪物装备掉落配置表';

-- =============================================
-- 20. combat_record (战斗记录)
-- =============================================
CREATE TABLE IF NOT EXISTS `combat_record` (
    `combat_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '战斗记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `monster_id` BIGINT NOT NULL COMMENT '妖兽ID',
    `combat_mode` VARCHAR(20) NOT NULL DEFAULT '手动战斗' COMMENT '战斗模式',
    `is_victory` TINYINT NOT NULL DEFAULT 0 COMMENT '是否胜利',
    `turns` SMALLINT NOT NULL DEFAULT 0 COMMENT '战斗回合数',
    `damage_dealt` INT NOT NULL DEFAULT 0 COMMENT '造成伤害总和',
    `damage_taken` INT NOT NULL DEFAULT 0 COMMENT '受到伤害总和',
    `critical_hits` SMALLINT NOT NULL DEFAULT 0 COMMENT '暴击次数',
    `stamina_consumed` TINYINT NOT NULL DEFAULT 10 COMMENT '体力消耗',
    `exp_gained` INT NOT NULL DEFAULT 0 COMMENT '获得经验值',
    `items_dropped` TEXT COMMENT '掉落物品(JSON数组)',
    `combat_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '战斗时间',
    PRIMARY KEY (`combat_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_monster_id` (`monster_id`),
    KEY `idx_combat_time` (`combat_time`),
    KEY `idx_is_victory` (`is_victory`),
    CONSTRAINT `fk_combat_record_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
    CONSTRAINT `fk_combat_record_monster` FOREIGN KEY (`monster_id`) REFERENCES `monster` (`monster_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='战斗记录表';

-- =============================================
-- 21. cultivation_record (修炼记录)
-- =============================================
CREATE TABLE IF NOT EXISTS `cultivation_record` (
    `cultivation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '修炼记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `start_realm` VARCHAR(20) NOT NULL COMMENT '修炼前境界',
    `start_level` TINYINT NOT NULL COMMENT '修炼前层次',
    `end_realm` VARCHAR(20) NOT NULL COMMENT '修炼后境界',
    `end_level` TINYINT NOT NULL COMMENT '修炼后层次',
    `exp_gained` INT NOT NULL DEFAULT 0 COMMENT '获得经验值',
    `stamina_consumed` TINYINT NOT NULL DEFAULT 5 COMMENT '体力消耗',
    `is_breakthrough` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为突破记录',
    `breakthrough_success` TINYINT DEFAULT NULL COMMENT '突破是否成功',
    `cultivation_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修炼时间',
    PRIMARY KEY (`cultivation_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_cultivation_time` (`cultivation_time`),
    CONSTRAINT `fk_cultivation_record_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='修炼记录表';

-- =============================================
-- 22. exploration_event (探索事件)
-- =============================================
CREATE TABLE IF NOT EXISTS `exploration_event` (
    `event_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '事件记录唯一标识',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `event_type` VARCHAR(20) NOT NULL COMMENT '事件类型',
    `event_description` TEXT COMMENT '事件描述',
    `stamina_consumed` TINYINT NOT NULL DEFAULT 5 COMMENT '体力消耗',
    `exploration_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '探索时间',
    PRIMARY KEY (`event_id`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_exploration_time` (`exploration_time`),
    CONSTRAINT `fk_exploration_event_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索事件表';

-- =============================================
-- 23. exploration_reward_config (探索奖励配置)
-- =============================================
CREATE TABLE IF NOT EXISTS `exploration_reward_config` (
    `config_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置唯一标识',
    `event_type` VARCHAR(20) NOT NULL COMMENT '事件类型',
    `reward_type` VARCHAR(20) NOT NULL COMMENT '奖励类型',
    `item_id` BIGINT DEFAULT NULL COMMENT '物品ID',
    `quantity_min` INT NOT NULL DEFAULT 1 COMMENT '数量下限',
    `quantity_max` INT NOT NULL DEFAULT 1 COMMENT '数量上限',
    `drop_rate` DECIMAL(5,2) NOT NULL DEFAULT 10.00 COMMENT '掉落率(%)',
    `is_guaranteed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否必掉',
    `weight` INT NOT NULL DEFAULT 100 COMMENT '权重',
    `min_realm_level` INT DEFAULT NULL COMMENT '最低境界要求',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`config_id`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_reward_type` (`reward_type`),
    KEY `idx_drop_rate` (`drop_rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索奖励配置表';

-- =============================================
-- 24. exploration_event_reward (探索事件奖励记录)
-- =============================================
CREATE TABLE IF NOT EXISTS `exploration_event_reward` (
    `event_reward_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '奖励记录唯一标识',
    `event_id` BIGINT NOT NULL COMMENT '探索事件ID',
    `reward_type` VARCHAR(20) NOT NULL COMMENT '奖励类型',
    `item_id` BIGINT DEFAULT NULL COMMENT '物品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '获得数量',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`event_reward_id`),
    KEY `idx_event_id` (`event_id`),
    KEY `idx_reward_type` (`reward_type`),
    CONSTRAINT `fk_exploration_event_reward_event` FOREIGN KEY (`event_id`) REFERENCES `exploration_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索事件奖励记录表';
