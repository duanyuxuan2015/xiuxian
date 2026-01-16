-- 修复宗门任务的 target_value，使其匹配新手怪物（realm_id=1）

UPDATE sect_task_template
SET target_value = '1',
    description = '宗门附近出现了一批凡人境界妖兽，前往击杀它们。'
WHERE task_type = 'combat' AND target_value = '2';

UPDATE sect_task_template
SET description = '保护炼器阁，击杀来袭的凡人境界妖兽。'
WHERE sect_id = 2 AND task_type = 'combat';

UPDATE sect_task_template
SET description = '保护丹药堂，击杀来袭的凡人境界妖兽。'
WHERE sect_id = 3 AND task_type = 'combat';

UPDATE sect_task_template
SET description = '驯服一些凡人境界野生妖兽为宗门所用。'
WHERE sect_id = 4 AND task_type = 'combat';

UPDATE sect_task_template
SET description = '保护万法阁，击杀来袭的凡人境界妖兽。'
WHERE sect_id = 5 AND task_type = 'combat';
