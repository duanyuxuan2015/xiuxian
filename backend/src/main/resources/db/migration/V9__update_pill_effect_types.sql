-- 更新丹药效果类型
-- 将"增加攻击"改为"增加体质"，"增加防御"改为"增加精神"

-- 更新力量丹系列（增加攻击 -> 增加体质）
UPDATE pill SET effect_type = '增加体质', description = '永久增加体质属性' WHERE pill_name = '力量丹';
UPDATE pill SET effect_type = '增加体质', description = '永久增加体质属性' WHERE pill_name = '狂暴丹';
UPDATE pill SET effect_type = '增加体质', description = '永久增加体质属性' WHERE pill_name = '破天丹';
UPDATE pill SET effect_type = '增加体质', description = '永久增加体质属性' WHERE pill_name = '神威丹';

-- 更新铁壁丹系列（增加防御 -> 增加精神）
UPDATE pill SET effect_type = '增加精神', description = '永久增加精神属性' WHERE pill_name = '铁壁丹';
UPDATE pill SET effect_type = '增加精神', description = '永久增加精神属性' WHERE pill_name = '金刚丹';
UPDATE pill SET effect_type = '增加精神', description = '永久增加精神属性' WHERE pill_name = '不灭丹';

-- 确认更新
SELECT pill_name, effect_type, effect_value, description FROM pill WHERE effect_type IN ('增加体质', '增加精神');
