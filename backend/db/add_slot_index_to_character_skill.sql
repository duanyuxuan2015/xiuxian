-- 为 character_skill 表添加 slot_index 字段
-- 用于记录技能装备槽位索引

ALTER TABLE character_skill
ADD COLUMN slot_index INT DEFAULT NULL
COMMENT '技能槽位索引' AFTER is_equipped;
