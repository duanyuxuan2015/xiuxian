-- 装备配方种子数据
-- 表结构: recipe_id, recipe_name, equipment_id, base_success_rate, forging_level_required, spiritual_cost, stamina_cost, duration, recipe_tier, unlock_method, unlock_cost, description

INSERT INTO equipment_recipe (recipe_name, equipment_id, base_success_rate, forging_level_required, spiritual_cost, stamina_cost, duration, recipe_tier, unlock_method, unlock_cost, description) VALUES
-- 武器配方 (假设装备ID 1-7对应各种武器)
('铁剑图谱', 1, 85, 1, 50, 10, 300, 1, '默认', 0, '最基础的铁剑锻造配方'),
('精铁剑图谱', 2, 75, 2, 80, 15, 450, 2, '购买', 100, '精炼铁剑的锻造配方'),
('玄铁剑图谱', 3, 60, 3, 120, 20, 600, 4, '购买', 500, '玄铁打造的利剑配方'),
('寒冰剑图谱', 4, 50, 4, 200, 30, 900, 4, '购买', 800, '蕴含寒冰之力的神剑配方'),
('烈焰刀图谱', 5, 50, 4, 200, 30, 900, 4, '购买', 800, '燃烧烈焰的战刀配方'),
('雷霆剑图谱', 6, 40, 5, 350, 40, 1200, 7, '掉落', 0, '雷霆之力灌注的神剑配方'),
('天罡剑图谱', 7, 25, 7, 500, 50, 1800, 10, '掉落', 0, '天罡正气凝聚的仙剑配方'),

-- 防具配方 (假设装备ID 8-13对应各种防具)
('铁甲图谱', 8, 85, 1, 50, 10, 300, 1, '默认', 0, '基础铁甲锻造配方'),
('精铁铠甲图谱', 9, 75, 2, 80, 15, 450, 2, '购买', 100, '精炼铁甲的锻造配方'),
('玄铁战甲图谱', 10, 60, 3, 120, 20, 600, 4, '购买', 500, '玄铁战甲锻造配方'),
('寒冰护甲图谱', 11, 50, 4, 200, 30, 900, 4, '购买', 800, '冰属性护甲配方'),
('烈焰战甲图谱', 12, 50, 4, 200, 30, 900, 4, '购买', 800, '火属性战甲配方'),
('雷霆战甲图谱', 13, 40, 5, 350, 40, 1200, 7, '掉落', 0, '雷属性战甲配方'),

-- 饰品配方 (假设装备ID 14-18对应各种饰品)
('铜戒指图谱', 14, 90, 1, 30, 5, 180, 1, '默认', 0, '简单的铜戒指配方'),
('银戒指图谱', 15, 80, 2, 50, 8, 240, 2, '购买', 50, '银质戒指锻造配方'),
('金戒指图谱', 16, 65, 3, 100, 12, 360, 4, '购买', 200, '金质戒指锻造配方'),
('玉项链图谱', 17, 70, 2, 50, 8, 240, 2, '购买', 100, '玉石项链配方'),
('灵玉项链图谱', 18, 55, 4, 150, 15, 480, 4, '购买', 600, '蕴含灵气的玉石项链配方');

-- 装备配方材料关联（使用material_name动态查找material_id）
INSERT INTO equipment_recipe_material (recipe_id, material_id, quantity_required, is_main_material)
SELECT r.recipe_id, m.material_id, rm.quantity_required, rm.is_main_material
FROM (
    SELECT 1 AS recipe_id, '铁' AS material_name, 5 AS quantity_required, 1 AS is_main_material
    UNION ALL SELECT 1, '泉水', 1, 0
    UNION ALL SELECT 2, '灵铁', 3, 1
    UNION ALL SELECT 2, '妖兽血', 1, 0
    UNION ALL SELECT 2, '灵泉水', 1, 0
    UNION ALL SELECT 3, '玄铁', 3, 1
    UNION ALL SELECT 3, '妖兽内丹', 1, 0
    UNION ALL SELECT 3, '朝露', 1, 0
    UNION ALL SELECT 4, '玄铁', 2, 1
    UNION ALL SELECT 4, '雪莲', 2, 0
    UNION ALL SELECT 4, '朝露', 2, 0
    UNION ALL SELECT 5, '玄铁', 2, 1
    UNION ALL SELECT 5, '龙涎', 2, 0
    UNION ALL SELECT 5, '朝露', 2, 0
    UNION ALL SELECT 6, '陨铁', 2, 1
    UNION ALL SELECT 6, '凤凰羽', 1, 0
    UNION ALL SELECT 6, '天河水', 2, 0
    UNION ALL SELECT 7, '紫金', 3, 1
    UNION ALL SELECT 7, '麒麟血', 1, 0
    UNION ALL SELECT 7, '混沌之水', 2, 0
    UNION ALL SELECT 8, '铁', 8, 1
    UNION ALL SELECT 8, '妖兽血', 1, 0
    UNION ALL SELECT 8, '泉水', 1, 0
    UNION ALL SELECT 9, '灵铁', 5, 1
    UNION ALL SELECT 9, '妖兽血', 2, 0
    UNION ALL SELECT 9, '灵泉水', 1, 0
    UNION ALL SELECT 10, '玄铁', 5, 1
    UNION ALL SELECT 10, '妖兽内丹', 2, 0
    UNION ALL SELECT 10, '朝露', 1, 0
    UNION ALL SELECT 11, '玄铁', 3, 1
    UNION ALL SELECT 11, '雪莲', 3, 0
    UNION ALL SELECT 11, '朝露', 2, 0
    UNION ALL SELECT 12, '玄铁', 3, 1
    UNION ALL SELECT 12, '龙涎', 3, 0
    UNION ALL SELECT 12, '朝露', 2, 0
    UNION ALL SELECT 13, '陨铁', 3, 1
    UNION ALL SELECT 13, '凤凰羽', 2, 0
    UNION ALL SELECT 13, '天河水', 2, 0
    UNION ALL SELECT 14, '铁', 2, 1
    UNION ALL SELECT 14, '泉水', 1, 0
    UNION ALL SELECT 15, '灵铁', 2, 1
    UNION ALL SELECT 15, '灵泉水', 1, 0
    UNION ALL SELECT 16, '玄铁', 2, 1
    UNION ALL SELECT 16, '朝露', 1, 0
    UNION ALL SELECT 17, '灵铁', 1, 1
    UNION ALL SELECT 17, '灵芝', 2, 0
    UNION ALL SELECT 17, '灵泉水', 1, 0
    UNION ALL SELECT 18, '玄铁', 1, 1
    UNION ALL SELECT 18, '九叶灵芝', 2, 0
    UNION ALL SELECT 18, '朝露', 2, 0
) rm
JOIN material m ON m.material_name = rm.material_name
JOIN equipment_recipe r ON r.recipe_id = rm.recipe_id
WHERE m.material_id IS NOT NULL;
