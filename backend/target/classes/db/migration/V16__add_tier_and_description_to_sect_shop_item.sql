-- 为宗门商店物品表添加阶位和描述字段
ALTER TABLE sect_shop_item
ADD COLUMN item_tier INT DEFAULT 1 COMMENT '物品阶位',
ADD COLUMN description VARCHAR(500) DEFAULT NULL COMMENT '物品描述';
