-- 为角色表添加暴击率、暴击伤害和速度字段
ALTER TABLE `character`
    ADD COLUMN `crit_rate` DOUBLE DEFAULT 5.0 COMMENT '暴击率（%）',
    ADD COLUMN `crit_damage` DOUBLE DEFAULT 150.0 COMMENT '暴击伤害（%）',
    ADD COLUMN `speed` DOUBLE DEFAULT 100.0 COMMENT '速度';
