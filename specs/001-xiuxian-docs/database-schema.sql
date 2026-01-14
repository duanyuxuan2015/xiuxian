-- ====================================================================
-- 凡人修仙文字游戏 - 数据库Schema DDL
-- Database: MySQL 8.x
-- Character Set: utf8mb4
-- Collation: utf8mb4_unicode_ci
-- Engine: InnoDB
-- Date: 2026-01-13
-- ====================================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================
-- 1. 境界配置表 (Realm)
-- ====================================================================

DROP TABLE IF EXISTS `realm`;
CREATE TABLE `realm` (
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

-- ====================================================================
-- 2. 宗门表 (Sect)
-- ====================================================================

DROP TABLE IF EXISTS `sect`;
CREATE TABLE `sect` (
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

-- ====================================================================
-- 3. 角色表 (Character)
-- ====================================================================

DROP TABLE IF EXISTS `character`;
CREATE TABLE `character` (
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
  `sect_id` BIGINT COMMENT '所属宗门ID',
  `sect_position` VARCHAR(20) COMMENT '宗门职位',
  `contribution` INT NOT NULL DEFAULT 0 COMMENT '宗门贡献度',
  `reputation` INT NOT NULL DEFAULT 0 COMMENT '宗门声望',
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

-- ====================================================================
-- 4. 丹药表 (Pill)
-- ====================================================================

DROP TABLE IF EXISTS `pill`;
CREATE TABLE `pill` (
  `pill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '丹药唯一标识',
  `pill_name` VARCHAR(50) NOT NULL COMMENT '丹药名称',
  `pill_tier` TINYINT NOT NULL DEFAULT 1 COMMENT '丹药阶位(1-10)',
  `quality` VARCHAR(20) NOT NULL DEFAULT '普通' COMMENT '丹药品质',
  `effect_type` VARCHAR(20) NOT NULL COMMENT '效果类型',
  `effect_value` INT NOT NULL DEFAULT 0 COMMENT '效果数值',
  `duration` INT COMMENT '持续时间(秒)',
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

-- ====================================================================
-- 5. 材料表 (Material)
-- ====================================================================

DROP TABLE IF EXISTS `material`;
CREATE TABLE `material` (
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

-- ====================================================================
-- 6. 角色丹药背包表 (CharacterPill)
-- ====================================================================

DROP TABLE IF EXISTS `character_pill`;
CREATE TABLE `character_pill` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色丹药背包表';

-- ====================================================================
-- 7. 角色材料背包表 (CharacterMaterial)
-- ====================================================================

DROP TABLE IF EXISTS `character_material`;
CREATE TABLE `character_material` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色材料背包表';

-- ====================================================================
-- 8. 丹方表 (PillRecipe)
-- ====================================================================

DROP TABLE IF EXISTS `pill_recipe`;
CREATE TABLE `pill_recipe` (
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
  `unlock_cost` INT NOT NULL DEFAULT 0 COMMENT '学习消耗(灵石或贡献度)',
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

-- ====================================================================
-- 9. 丹方材料表 (PillRecipeMaterial)
-- ====================================================================

DROP TABLE IF EXISTS `pill_recipe_material`;
CREATE TABLE `pill_recipe_material` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='丹方材料表';

-- ====================================================================
-- 10. 炼丹记录表 (AlchemyRecord)
-- ====================================================================

DROP TABLE IF EXISTS `alchemy_record`;
CREATE TABLE `alchemy_record` (
  `alchemy_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '炼丹记录唯一标识',
  `character_id` BIGINT NOT NULL COMMENT '角色ID',
  `recipe_id` BIGINT NOT NULL COMMENT '丹方ID',
  `is_success` TINYINT NOT NULL DEFAULT 0 COMMENT '是否成功',
  `success_rate` INT NOT NULL DEFAULT 50 COMMENT '实际成功率(%)',
  `output_quantity` TINYINT NOT NULL DEFAULT 0 COMMENT '产出数量(成功时)',
  `output_quality` VARCHAR(20) COMMENT '产出品质',
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

-- ====================================================================
-- 11. 技能表 (Skill)
-- ====================================================================

DROP TABLE IF EXISTS `skill`;
CREATE TABLE `skill` (
  `skill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能唯一标识',
  `skill_name` VARCHAR(50) NOT NULL COMMENT '技能名称',
  `function_type` VARCHAR(20) NOT NULL COMMENT '功能分类',
  `element_type` VARCHAR(20) COMMENT '元素分类',
  `base_damage` INT DEFAULT 0 COMMENT '技能基础伤害(1级)',
  `skill_multiplier` DECIMAL(5,2) NOT NULL DEFAULT 1.0 COMMENT '技能倍率(1级)',
  `spiritual_cost` SMALLINT NOT NULL DEFAULT 10 COMMENT '灵气消耗(1级)',
  `damage_growth_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.10 COMMENT '伤害成长率(每级增加百分比)',
  `multiplier_growth` DECIMAL(4,2) NOT NULL DEFAULT 0.05 COMMENT '倍率成长(每级增加)',
  `spiritual_cost_growth` TINYINT NOT NULL DEFAULT 2 COMMENT '灵气消耗成长(每级增加)',
  `description` TEXT COMMENT '技能描述',
  `tier` TINYINT NOT NULL DEFAULT 1 COMMENT '技能阶位(1-8)',
  `sect_id` BIGINT COMMENT '宗门ID(专属技能)',
  `unlock_method` VARCHAR(20) NOT NULL COMMENT '学习途径',
  `cost` INT NOT NULL DEFAULT 0 COMMENT '学习消耗(灵石或贡献度)',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`skill_id`),
  KEY `idx_function_type` (`function_type`),
  KEY `idx_element_type` (`element_type`),
  KEY `idx_sect_id` (`sect_id`),
  CONSTRAINT `fk_skill_sect` FOREIGN KEY (`sect_id`) REFERENCES `sect` (`sect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能表';

-- ====================================================================
-- 12. 角色已学技能表 (CharacterSkill)
-- ====================================================================

DROP TABLE IF EXISTS `character_skill`;
CREATE TABLE `character_skill` (
  `character_skill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
  `character_id` BIGINT NOT NULL COMMENT '角色ID',
  `skill_id` BIGINT NOT NULL COMMENT '技能ID',
  `proficiency` INT NOT NULL DEFAULT 0 COMMENT '技能熟练度',
  `skill_level` TINYINT NOT NULL DEFAULT 1 COMMENT '技能等级(1-99)',
  `current_damage` INT NOT NULL DEFAULT 0 COMMENT '当前等级实际伤害',
  `current_multiplier` DECIMAL(5,2) NOT NULL DEFAULT 1.0 COMMENT '当前等级实际倍率',
  `current_spiritual_cost` SMALLINT NOT NULL DEFAULT 10 COMMENT '当前等级实际灵气消耗',
  `is_equipped` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已装备',
  `equipment_slot` VARCHAR(20) COMMENT '装备槽位',
  `learned_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '学习时间',
  PRIMARY KEY (`character_skill_id`),
  UNIQUE KEY `uk_character_skill` (`character_id`, `skill_id`),
  KEY `idx_character_id` (`character_id`),
  CONSTRAINT `fk_character_skill_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`),
  CONSTRAINT `fk_character_skill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色已学技能表';

-- ====================================================================
-- 13. 装备表 (Equipment)
-- ====================================================================

DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
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

-- ====================================================================
-- 14. 角色已装备装备表 (CharacterEquipment)
-- ====================================================================

DROP TABLE IF EXISTS `character_equipment`;
CREATE TABLE `character_equipment` (
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

-- ====================================================================
-- 15. 装备图纸表 (EquipmentRecipe)
-- ====================================================================

DROP TABLE IF EXISTS `equipment_recipe`;
CREATE TABLE `equipment_recipe` (
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
  `unlock_cost` INT NOT NULL DEFAULT 0 COMMENT '学习消耗(灵石或贡献度)',
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

-- ====================================================================
-- 16. 装备图纸材料表 (EquipmentRecipeMaterial)
-- ====================================================================

DROP TABLE IF EXISTS `equipment_recipe_material`;
CREATE TABLE `equipment_recipe_material` (
  `recipe_material_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
  `recipe_id` BIGINT NOT NULL COMMENT '图纸ID',
  `material_id` BIGINT NOT NULL COMMENT '材料ID',
  `quantity_required` INT NOT NULL DEFAULT 1 COMMENT '所需数量',
  `is_main_material` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主材料',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`recipe_material_id`),
  UNIQUE KEY `uk_recipe_material` (`recipe_id`, `material_id`),
  KEY `idx_recipe_id` (`recipe_id`),
  KEY `idx_material_id` (`material_id`),
  CONSTRAINT `fk_equipment_recipe_material_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `equipment_recipe` (`recipe_id`),
  CONSTRAINT `fk_equipment_recipe_material_material` FOREIGN KEY (`material_id`) REFERENCES `material` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='装备图纸材料表';

-- ====================================================================
-- 17. 锻造记录表 (ForgeRecord)
-- ====================================================================

DROP TABLE IF EXISTS `forge_record`;
CREATE TABLE `forge_record` (
  `forge_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '锻造记录唯一标识',
  `character_id` BIGINT NOT NULL COMMENT '角色ID',
  `recipe_id` BIGINT NOT NULL COMMENT '图纸ID',
  `is_success` TINYINT NOT NULL DEFAULT 0 COMMENT '是否成功',
  `success_rate` INT NOT NULL DEFAULT 50 COMMENT '实际成功率(%)',
  `output_quality` VARCHAR(20) COMMENT '产出品质',
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

-- ====================================================================
-- 18. 妖兽表 (Monster)
-- ====================================================================

DROP TABLE IF EXISTS `monster`;
CREATE TABLE `monster` (
  `monster_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '妖兽唯一标识',
  `monster_name` VARCHAR(50) NOT NULL COMMENT '妖兽名称',
  `realm_id` INT NOT NULL DEFAULT 1 COMMENT '妖兽境界ID',
  `monster_type` VARCHAR(20) NOT NULL DEFAULT '普通' COMMENT '妖兽类型',
  `speed` INT NOT NULL DEFAULT 10 COMMENT '速度(决定行动顺序)',
  `hp` INT NOT NULL DEFAULT 100 COMMENT '生命力(生命值)',
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

-- ====================================================================
-- 19. 怪物装备掉落配置表 (MonsterDrop)
-- ====================================================================

DROP TABLE IF EXISTS `monster_drop`;
CREATE TABLE `monster_drop` (
  `monster_drop_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '掉落配置唯一标识',
  `monster_id` BIGINT NOT NULL COMMENT '怪物ID',
  `equipment_id` BIGINT NOT NULL COMMENT '装备ID',
  `drop_rate` DECIMAL(5,2) NOT NULL DEFAULT 10.0 COMMENT '掉落率(%)',
  `drop_quantity` TINYINT NOT NULL DEFAULT 1 COMMENT '掉落数量',
  `min_quality` VARCHAR(20) COMMENT '最低品质要求',
  `max_quality` VARCHAR(20) COMMENT '最高品质限制',
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

-- ====================================================================
-- 20. 战斗记录表 (CombatRecord)
-- ====================================================================

DROP TABLE IF EXISTS `combat_record`;
CREATE TABLE `combat_record` (
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

-- ====================================================================
-- 21. 修炼记录表 (CultivationRecord)
-- ====================================================================

DROP TABLE IF EXISTS `cultivation_record`;
CREATE TABLE `cultivation_record` (
  `cultivation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '修炼记录唯一标识',
  `character_id` BIGINT NOT NULL COMMENT '角色ID',
  `start_realm` VARCHAR(20) NOT NULL COMMENT '修炼前境界',
  `start_level` TINYINT NOT NULL COMMENT '修炼前层次',
  `end_realm` VARCHAR(20) NOT NULL COMMENT '修炼后境界',
  `end_level` TINYINT NOT NULL COMMENT '修炼后层次',
  `exp_gained` INT NOT NULL DEFAULT 0 COMMENT '获得经验值',
  `stamina_consumed` TINYINT NOT NULL DEFAULT 5 COMMENT '体力消耗',
  `cultivation_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修炼时间',
  PRIMARY KEY (`cultivation_id`),
  KEY `idx_character_id` (`character_id`),
  KEY `idx_cultivation_time` (`cultivation_time`),
  CONSTRAINT `fk_cultivation_record_character` FOREIGN KEY (`character_id`) REFERENCES `character` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='修炼记录表';

-- ====================================================================
-- 22. 探索事件表 (ExplorationEvent)
-- ====================================================================

DROP TABLE IF EXISTS `exploration_event`;
CREATE TABLE `exploration_event` (
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

-- ====================================================================
-- 23. 探索奖励配置表 (ExplorationRewardConfig)
-- ====================================================================

DROP TABLE IF EXISTS `exploration_reward_config`;
CREATE TABLE `exploration_reward_config` (
  `config_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置唯一标识',
  `event_type` VARCHAR(20) NOT NULL COMMENT '事件类型',
  `reward_type` VARCHAR(20) NOT NULL COMMENT '奖励类型',
  `item_id` BIGINT COMMENT '物品ID',
  `quantity_min` INT NOT NULL DEFAULT 1 COMMENT '数量下限',
  `quantity_max` INT NOT NULL DEFAULT 1 COMMENT '数量上限',
  `drop_rate` DECIMAL(5,2) NOT NULL DEFAULT 10.0 COMMENT '掉落率(%)',
  `is_guaranteed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否必掉',
  `weight` INT NOT NULL DEFAULT 100 COMMENT '权重(抽奖用)',
  `min_realm_level` INT COMMENT '最低境界要求',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`config_id`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_reward_type` (`reward_type`),
  KEY `idx_drop_rate` (`drop_rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索奖励配置表';

-- ====================================================================
-- 24. 探索事件奖励记录表 (ExplorationEventReward)
-- ====================================================================

DROP TABLE IF EXISTS `exploration_event_reward`;
CREATE TABLE `exploration_event_reward` (
  `event_reward_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '奖励记录唯一标识',
  `event_id` BIGINT NOT NULL COMMENT '探索事件ID',
  `reward_type` VARCHAR(20) NOT NULL COMMENT '奖励类型',
  `item_id` BIGINT COMMENT '物品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '获得数量',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`event_reward_id`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_reward_type` (`reward_type`),
  CONSTRAINT `fk_exploration_event_reward_event` FOREIGN KEY (`event_id`) REFERENCES `exploration_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探索事件奖励记录表';

-- ====================================================================
-- 种子数据初始化
-- ====================================================================

-- 1. 境界配置初始化数据
INSERT INTO `realm` (`id`, `realm_name`, `realm_level`, `sub_levels`, `required_exp`, `breakthrough_rate`, `hp_bonus`, `sp_bonus`, `stamina_bonus`, `attack_bonus`, `defense_bonus`, `lifespan_bonus`, `level_up_points`, `realm_stage`, `description`) VALUES
(1, '凡人', 1, 9, 0, 100, 0, 0, 0, 0, 0, 0, 5, 'MORTAL', '未踏入修仙之路的普通凡人'),
(2, '炼气期', 2, 9, 100, 90, 10, 10, 10, 5, 5, 10, 5, 'MORTAL', '初入修仙之门,吐纳天地灵气,淬炼肉身'),
(3, '筑基期', 3, 9, 1000, 70, 50, 20, 20, 10, 10, 30, 8, 'MORTAL', '筑就修仙根基,凝聚灵力基础'),
(4, '结丹期', 4, 9, 10000, 50, 200, 100, 30, 30, 20, 100, 10, 'MORTAL', '灵力凝聚金丹,开始脱离凡俗'),
(5, '元婴期', 5, 9, 100000, 30, 1000, 500, 50, 100, 50, 300, 15, 'MORTAL', '金丹破碎,元婴初成,拥有不死之身'),
(6, '化神期', 6, 9, 500000, 20, 5000, 2000, 80, 300, 150, 1000, 20, 'IMMORTAL', '元神化生,可离体而出,法力通天'),
(7, '炼虚期', 7, 9, 2000000, 15, 20000, 8000, 120, 1000, 500, 3000, 30, 'IMMORTAL', '炼化虚空,遨游星域'),
(8, '合体期', 8, 9, 8000000, 10, 80000, 30000, 180, 3000, 1500, 10000, 40, 'IMMORTAL', '天人合一,返璞归真'),
(9, '大乘期', 9, 9, 30000000, 5, 300000, 120000, 250, 10000, 5000, 50000, 60, 'IMMORTAL', '大道已成,可飞升仙界'),
(10, '渡劫期', 10, 9, 100000000, 3, 1000000, 500000, 400, 30000, 15000, 99999, 80, 'IMMORTAL', '渡九九天劫,飞升在即'),
(11, '地仙', 11, 9, 500000000, 2, 5000000, 2000000, 600, 100000, 50000, 99999, 100, 'IMMORTAL', '已成仙体,长生不老'),
(12, '天仙', 12, 9, 2000000000, 1, 20000000, 8000000, 900, 300000, 150000, 99999, 150, 'IMMORTAL', '遨游三界,执掌天权'),
(13, '金仙', 13, 9, 8000000000, 1, 80000000, 30000000, 1200, 1000000, 500000, 99999, 200, 'IMMORTAL', '跳出三界外,不在五行中'),
(14, '道祖之境', 14, 9, 99999999999, 1, 999999999, 999999999, 9999, 99999999, 99999999, 99999, 500, 'IMMORTAL', '大道至高,无上境界');

-- 2. 宗门初始化数据
INSERT INTO `sect` (`sect_id`, `sect_name`, `sect_type`, `description`, `skill_focus`, `join_requirement`) VALUES
(1, '青云剑宗', '青云剑宗', '以剑道闻名天下的正派宗门,历代高手辈出,剑气纵横九万里', '剑法系技能', '炼气期'),
(2, '丹霞谷', '丹霞谷', '精通炼丹之术的中立宗门,掌握无数丹方,丹香弥漫千里', '炼丹辅助技能', '炼气期'),
(3, '天机阁', '天机阁', '擅长法术和阵法的神秘宗门,推演天机,窥探大道', '法术系技能', '筑基期'),
(4, '无极魔宗', '无极魔宗', '修炼魔功的邪派宗门,实力强大但有副作用,魔威滔天', '魔功技能', '筑基期'),
(5, '万兽山庄', '万兽山庄', '驯养灵兽的特殊宗门,拥有强大的妖兽军团', '驯兽技能', '炼气期');

-- 3. 材料初始化数据
INSERT INTO `material` (`material_id`, `material_name`, `material_type`, `material_tier`, `quality`, `stack_limit`, `spirit_stones`, `description`) VALUES
(1, '血灵草', '草药', 1, '普通', 999, 5, '炼制小还丹的主材料,生长于灵气充足之地'),
(2, '凝血草', '草药', 1, '普通', 999, 3, '辅助炼制小还丹,有凝血止血之效'),
(3, '清心草', '草药', 1, '普通', 999, 3, '辅助炼制小还丹,可清心凝神'),
(4, '聚灵花', '草药', 2, '稀有', 999, 20, '炼制聚灵丹的主材料,聚集天地灵气'),
(5, '灵露草', '草药', 2, '稀有', 999, 15, '辅助炼制聚灵丹,晨露凝聚而成'),
(6, '天心石', '矿石', 2, '稀有', 999, 25, '辅助炼制聚灵丹,蕴含天地之心'),
(7, '筑基草', '草药', 3, '史诗', 999, 100, '炼制筑基丹的主材料,突破筑基期必备'),
(8, '凝神花', '草药', 3, '史诗', 999, 80, '辅助炼制筑基丹,凝聚神识'),
(9, '百年灵芝', '草药', 3, '史诗', 999, 150, '辅助炼制筑基丹,百年灵物'),
(10, '五行石', '矿石', 3, '史诗', 999, 120, '辅助炼制筑基丹,蕴含五行之力'),
(11, '寒铁矿', '矿石', 2, '普通', 999, 30, '炼制武器的基础材料,坚硬无比'),
(12, '精铁', '矿石', 2, '稀有', 999, 50, '炼制武器的优质材料,需提炼寒铁获得'),
(13, '冰晶石', '矿石', 3, '稀有', 999, 80, '炼制冰系装备的珍贵材料'),
(14, '赤焰石', '矿石', 3, '史诗', 999, 200, '炼制火系装备的主材料,内含赤焰'),
(15, '火焰宝石', '矿石', 4, '史诗', 999, 500, '炼制高级火系装备的珍贵宝石'),
(16, '龙鳞', '兽骨', 4, '传说', 999, 1000, '真龙脱落的鳞片,炼器至宝'),
(17, '妖兽内丹', '妖丹', 4, '史诗', 999, 800, '高级妖兽的内丹,蕴含强大妖力'),
(18, '铁木', '灵木', 1, '普通', 999, 10, '制作武器手柄的基础材料'),
(19, '麻绳', '其他', 1, '普通', 999, 2, '捆绑固定用的材料');

-- 4. 丹药初始化数据
INSERT INTO `pill` (`pill_id`, `pill_name`, `pill_tier`, `quality`, `effect_type`, `effect_value`, `duration`, `stack_limit`, `spirit_stones`, `description`) VALUES
(1, '小还丹', 1, '普通', '恢复气血', 100, NULL, 999, 50, '初级疗伤丹药,恢复100点气血'),
(2, '聚灵丹', 2, '稀有', '恢复灵力', 200, NULL, 999, 150, '恢复200点灵力,修炼必备'),
(3, '筑基丹', 3, '史诗', '突破辅助', 50, NULL, 99, 5000, '辅助筑基期突破,增加50%成功率'),
(4, '洗髓丹', 5, '传说', '属性提升', 10, NULL, 10, 50000, '洗髓伐骨,永久提升10点体质'),
(5, '悟道丹', 6, '传说', '属性提升', 15, NULL, 10, 100000, '顿悟大道,永久提升15点悟性'),
(6, '解毒丹', 2, '普通', '解毒', 100, NULL, 999, 80, '解除常见毒素'),
(7, '疗伤丹', 3, '稀有', '疗伤', 500, NULL, 999, 300, '疗伤圣药,恢复重伤'),
(8, '增气丹', 1, '普通', '增加经验', 50, NULL, 999, 30, '增加50点修炼经验');

-- 5. 技能初始化数据（部分示例）
INSERT INTO `skill` (`skill_id`, `skill_name`, `function_type`, `element_type`, `base_damage`, `skill_multiplier`, `spiritual_cost`, `damage_growth_rate`, `multiplier_growth`, `spiritual_cost_growth`, `tier`, `sect_id`, `unlock_method`, `cost`, `description`) VALUES
(1, '普通攻击', '攻击', '物理系', 50, 1.0, 0, 0.05, 0.02, 0, 1, NULL, '宗门学习', 0, '基础物理攻击'),
(2, '火球术', '攻击', '火系', 100, 2.0, 20, 0.10, 0.05, 2, 2, NULL, '商店购买', 1000, '凝聚火焰攻击敌人'),
(3, '寒冰箭', '攻击', '冰系', 95, 1.8, 18, 0.10, 0.05, 2, 2, NULL, '商店购买', 1000, '凝聚寒冰攻击并减速'),
(4, '雷电术', '攻击', '雷系', 110, 2.2, 25, 0.12, 0.06, 3, 3, NULL, '商店购买', 3000, '召唤雷电打击敌人'),
(5, '青云剑诀', '攻击', '物理系', 150, 2.5, 30, 0.15, 0.08, 3, 4, 1, '宗门学习', 500, '青云剑宗不传之秘,剑气纵横'),
(6, '护体罡气', '防御', NULL, 0, 0.0, 15, 0.00, 0.00, 2, 2, NULL, '宗门学习', 800, '形成防御罡气,减少30%伤害'),
(7, '治愈术', '治疗', NULL, 80, 1.5, 20, 0.08, 0.04, 2, 2, 2, '宗门学习', 500, '恢复气血,治愈伤势'),
(8, '神行术', '辅助', NULL, 0, 0.0, 10, 0.00, 0.00, 1, 1, NULL, '商店购买', 500, '提升速度,先发制人');

-- 6. 装备初始化数据（部分示例）
INSERT INTO `equipment` (`equipment_id`, `equipment_name`, `equipment_type`, `quality`, `base_score`, `attack_power`, `defense_power`, `health_bonus`, `critical_rate`, `speed_bonus`, `physical_resist`, `ice_resist`, `fire_resist`, `lightning_resist`, `enhancement_level`, `gem_slot_count`, `description`) VALUES
(1, '新手木剑', '武器', '普通', 100, 30, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, '初学者使用的普通木剑'),
(2, '寒铁剑', '武器', '稀有', 300, 85, 0, 0, 5, 5, 0, 3, 0, 0, 0, 1, '寒铁锻造的长剑,锋利无比'),
(3, '烈焰战甲', '铠甲', '史诗', 600, 0, 120, 200, 0, 0, 5, 0, 10, 0, 0, 2, '蕴含烈焰之力的战甲'),
(4, '布衣', '铠甲', '普通', 100, 0, 20, 50, 0, 0, 2, 0, 0, 0, 0, 0, '普通布衣,聊胜于无'),
(5, '铁手套', '护手', '普通', 100, 10, 15, 0, 0, 0, 3, 0, 0, 0, 0, 0, '铁制护手'),
(6, '火之戒', '戒指', '稀有', 300, 15, 0, 0, 3, 0, 0, 0, 5, 0, 0, 1, '蕴含火焰之力的戒指'),
(7, '冰霜项链', '项链', '史诗', 600, 0, 0, 150, 0, 5, 0, 8, 0, 0, 0, 1, '冰霜凝聚的项链,寒气逼人');

-- 7. 妖兽初始化数据
INSERT INTO `monster` (`monster_id`, `monster_name`, `realm_id`, `monster_type`, `speed`, `hp`, `attack_power`, `defense_power`, `attack_element`, `physical_resist`, `ice_resist`, `fire_resist`, `lightning_resist`, `stamina_cost`, `exp_reward`, `spirit_stones_reward`, `description`) VALUES
(1, '普通野狼', 1, '普通', 15, 100, 20, 10, '物理', 0, 0, 0, 0, 10, 50, 10, '森林中常见的野狼'),
(2, '森林野猪', 1, '普通', 10, 150, 25, 15, '物理', 5, 0, 0, 0, 10, 60, 12, '力量强大的野猪'),
(3, '火焰蜥蜴', 2, '精英', 20, 500, 80, 40, '火系', 20, 0, 50, 0, 20, 200, 50, '全身覆盖火焰的蜥蜴'),
(4, '冰霜狼', 2, '精英', 25, 450, 75, 35, '冰系', 15, 40, 0, 10, 20, 180, 45, '来自雪域的冰霜狼'),
(5, '雷鸣虎', 3, '精英', 30, 800, 120, 60, '雷系', 25, 5, 5, 50, 20, 350, 80, '能驾驭雷电的猛虎'),
(6, '火龙', 5, 'BOSS', 50, 5000, 500, 200, '火系', 40, 0, 50, 10, 30, 5000, 1000, '传说中的火龙,实力强大');

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- 数据库Schema创建完成
-- ====================================================================
