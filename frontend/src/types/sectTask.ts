/**
 * 宗门任务相关类型定义
 */

/**
 * 任务模板实体
 */
export interface SectTaskTemplate {
  templateId?: number;
  sectId: number;
  taskType: string;
  taskName: string;
  description?: string;
  targetType: string;
  targetValue: string;
  targetCount: number;
  requiredPosition: number;
  contributionReward: number;
  reputationReward: number;
  dailyLimit: number;
  isActive: boolean;
}

/**
 * 任务模板列表项
 */
export interface SectTaskTemplateListItem {
  templateId: number;
  sectId: number;
  sectName: string;
  taskType: string;
  taskName: string;
  targetType: string;
  targetCount: number;
  requiredPosition: number;
  contributionReward: number;
  reputationReward: number;
  isActive: boolean;
}

/**
 * 任务模板详情
 */
export interface SectTaskTemplateDetail extends SectTaskTemplate {
  templateId: number;
  sectName?: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}

/**
 * 宗门选项
 */
export interface SectOption {
  sectId: number;
  sectName: string;
}
