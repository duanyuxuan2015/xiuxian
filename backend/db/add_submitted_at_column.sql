-- 为 sect_task_progress 表添加 submitted_at 字段
-- 用于记录任务提交时间（提交待领奖状态）

ALTER TABLE sect_task_progress
ADD COLUMN submitted_at DATETIME DEFAULT NULL
COMMENT '任务提交时间' AFTER completed_at;
