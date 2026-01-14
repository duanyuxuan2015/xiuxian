-- 丹方种子数据
-- 表结构: recipe_id, recipe_name, pill_id, output_quantity, base_success_rate, alchemy_level_required, spiritual_cost, stamina_cost, duration, recipe_tier, unlock_method, unlock_cost, description

INSERT INTO pill_recipe (recipe_name, pill_id, output_quantity, base_success_rate, alchemy_level_required, spiritual_cost, stamina_cost, duration, recipe_tier, unlock_method, unlock_cost, description) VALUES
('聚气丹方', 1, 1, 80, 1, 30, 5, 60, 1, '默认', 0, '最基础的丹方，适合新手练习'),
('回春丹方', 2, 1, 80, 1, 30, 5, 60, 1, '默认', 0, '基础回复丹药的配方'),
('培元丹方', 3, 1, 70, 2, 50, 8, 120, 2, '购买', 100, '中级回复丹药配方'),
('九转回魂丹方', 4, 1, 50, 4, 100, 15, 300, 4, '购买', 500, '高级回复丹药，炼制难度较高'),
('天元丹方', 5, 1, 30, 6, 200, 25, 600, 6, '掉落', 0, '顶级回复丹药，需要精湛技艺'),
('筑基丹方', 6, 1, 75, 1, 30, 5, 60, 1, '默认', 0, '修炼辅助丹药基础配方'),
('凝气丹方', 7, 1, 65, 2, 50, 8, 120, 2, '购买', 100, '凝聚灵气的进阶配方'),
('金丹丹方', 8, 1, 40, 4, 100, 15, 300, 4, '购买', 500, '结丹期必备，炼制困难'),
('元婴丹方', 9, 1, 25, 6, 200, 25, 600, 6, '掉落', 0, '元婴期丹药，极难炼制'),
('渡劫丹方', 10, 1, 10, 8, 400, 40, 1200, 8, '掉落', 0, '渡劫神丹，千难万险'),
('力量丹方', 11, 1, 75, 1, 30, 5, 60, 1, '默认', 0, '增强攻击力的基础配方'),
('铁壁丹方', 12, 1, 75, 1, 30, 5, 60, 1, '默认', 0, '增强防御力的基础配方'),
('狂暴丹方', 13, 1, 60, 2, 50, 8, 120, 2, '购买', 100, '狂暴药剂的配方'),
('金刚丹方', 14, 1, 60, 2, 50, 8, 120, 2, '购买', 100, '金刚药剂的配方'),
('破天丹方', 15, 1, 45, 4, 100, 15, 300, 4, '购买', 500, '破天丹的珍贵配方'),
('不灭丹方', 16, 1, 45, 4, 100, 15, 300, 4, '购买', 500, '不灭丹的珍贵配方'),
('神威丹方', 17, 1, 20, 6, 200, 25, 600, 6, '掉落', 0, '传说级丹方'),
('洗髓丹方', 18, 1, 55, 3, 80, 12, 180, 2, '购买', 300, '改善资质的特殊丹方'),
('易容丹方', 19, 1, 50, 3, 80, 12, 180, 4, '购买', 200, '变化容貌的奇特丹方'),
('解毒丹方', 20, 1, 85, 1, 20, 3, 30, 1, '默认', 0, '简单的解毒配方'),
('破障丹方', 21, 1, 25, 5, 150, 20, 450, 6, '掉落', 0, '增加突破几率的珍贵丹方');

-- 丹方材料关联（使用INSERT ... VALUES方式，分批插入）
INSERT INTO pill_recipe_material (recipe_id, material_id, quantity_required, is_main_material)
SELECT r.recipe_id, m.material_id, rm.quantity_required, rm.is_main_material
FROM (
    SELECT 1 AS recipe_id, '灵草' AS material_name, 3 AS quantity_required, 1 AS is_main_material
    UNION ALL SELECT 1, '泉水', 1, 0
    UNION ALL SELECT 2, '紫芝', 2, 1
    UNION ALL SELECT 2, '黄精', 1, 0
    UNION ALL SELECT 2, '泉水', 1, 0
    UNION ALL SELECT 3, '何首乌', 2, 1
    UNION ALL SELECT 3, '人参', 1, 0
    UNION ALL SELECT 3, '灵泉水', 1, 0
    UNION ALL SELECT 4, '雪莲', 2, 1
    UNION ALL SELECT 4, '九叶灵芝', 1, 0
    UNION ALL SELECT 4, '龙涎', 1, 0
    UNION ALL SELECT 4, '朝露', 1, 0
    UNION ALL SELECT 5, '万年人参', 1, 1
    UNION ALL SELECT 5, '太岁', 1, 0
    UNION ALL SELECT 5, '凤凰羽', 1, 0
    UNION ALL SELECT 5, '天河水', 2, 0
    UNION ALL SELECT 6, '灵草', 2, 1
    UNION ALL SELECT 6, '妖兽血', 1, 0
    UNION ALL SELECT 6, '泉水', 1, 0
    UNION ALL SELECT 7, '人参', 1, 1
    UNION ALL SELECT 7, '灵芝', 1, 0
    UNION ALL SELECT 7, '妖兽内丹', 1, 0
    UNION ALL SELECT 7, '灵泉水', 1, 0
    UNION ALL SELECT 8, '九叶灵芝', 2, 1
    UNION ALL SELECT 8, '龙涎', 1, 0
    UNION ALL SELECT 8, '玄铁', 1, 0
    UNION ALL SELECT 8, '朝露', 2, 0
    UNION ALL SELECT 9, '万年人参', 1, 1
    UNION ALL SELECT 9, '凤凰羽', 1, 0
    UNION ALL SELECT 9, '陨铁', 1, 0
    UNION ALL SELECT 9, '天河水', 2, 0
    UNION ALL SELECT 10, '仙灵草', 2, 1
    UNION ALL SELECT 10, '麒麟血', 1, 0
    UNION ALL SELECT 10, '紫金', 1, 0
    UNION ALL SELECT 10, '混沌之水', 2, 0
    UNION ALL SELECT 11, '灵草', 1, 1
    UNION ALL SELECT 11, '妖兽血', 2, 0
    UNION ALL SELECT 11, '泉水', 1, 0
    UNION ALL SELECT 12, '灵草', 1, 1
    UNION ALL SELECT 12, '铁', 2, 0
    UNION ALL SELECT 12, '泉水', 1, 0
    UNION ALL SELECT 13, '何首乌', 1, 1
    UNION ALL SELECT 13, '妖兽内丹', 2, 0
    UNION ALL SELECT 13, '灵泉水', 1, 0
    UNION ALL SELECT 14, '人参', 1, 1
    UNION ALL SELECT 14, '灵铁', 2, 0
    UNION ALL SELECT 14, '灵泉水', 1, 0
    UNION ALL SELECT 15, '雪莲', 1, 1
    UNION ALL SELECT 15, '龙涎', 2, 0
    UNION ALL SELECT 15, '玄铁', 1, 0
    UNION ALL SELECT 15, '朝露', 1, 0
    UNION ALL SELECT 16, '九叶灵芝', 1, 1
    UNION ALL SELECT 16, '玄铁', 2, 0
    UNION ALL SELECT 16, '朝露', 2, 0
    UNION ALL SELECT 17, '万年人参', 1, 1
    UNION ALL SELECT 17, '凤凰羽', 1, 0
    UNION ALL SELECT 17, '陨铁', 1, 0
    UNION ALL SELECT 17, '天河水', 1, 0
    UNION ALL SELECT 18, '人参', 2, 1
    UNION ALL SELECT 18, '灵芝', 1, 0
    UNION ALL SELECT 18, '妖兽内丹', 1, 0
    UNION ALL SELECT 18, '灵泉水', 2, 0
    UNION ALL SELECT 19, '雪莲', 1, 1
    UNION ALL SELECT 19, '龙涎', 1, 0
    UNION ALL SELECT 19, '朝露', 2, 0
    UNION ALL SELECT 20, '紫芝', 2, 1
    UNION ALL SELECT 20, '泉水', 2, 0
    UNION ALL SELECT 21, '太岁', 1, 1
    UNION ALL SELECT 21, '凤凰羽', 1, 0
    UNION ALL SELECT 21, '天河水', 2, 0
) rm
JOIN material m ON m.material_name = rm.material_name
JOIN pill_recipe r ON r.recipe_id = rm.recipe_id
WHERE m.material_id IS NOT NULL;
