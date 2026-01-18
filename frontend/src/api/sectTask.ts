/**
 * 宗门任务管理 API
 */
import request from './index';
import type { SectTaskTemplate, SectTaskTemplateListItem, PageResult, SectOption } from '@/types/sectTask';

const sectTaskApi = {
  /**
   * 分页查询任务模板列表
   */
  list: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    sectId?: number;
    taskType?: string;
  }) => {
    return request.get<PageResult<SectTaskTemplateListItem>>('/admin/sect-task/list', { params });
  },

  /**
   * 获取任务模板详情
   */
  getDetail: (templateId: number) => {
    return request.get<SectTaskTemplate>(`/admin/sect-task/${templateId}`);
  },

  /**
   * 创建任务模板
   */
  create: (data: SectTaskTemplate) => {
    return request.post<{ templateId: number }>('/admin/sect-task', data);
  },

  /**
   * 更新任务模板
   */
  update: (templateId: number, data: SectTaskTemplate) => {
    return request.put(`/admin/sect-task/${templateId}`, data);
  },

  /**
   * 删除任务模板
   */
  delete: (templateId: number) => {
    return request.delete(`/admin/sect-task/${templateId}`);
  },

  /**
   * 批量删除任务模板
   */
  batchDelete: (templateIds: number[]) => {
    return request.delete('/admin/sect-task/batch', { data: templateIds });
  },

  /**
   * 获取所有宗门列表
   */
  getSects: () => {
    return request.get<SectOption[]>('/admin/sect-task/sects');
  },

  /**
   * 获取所有任务类型
   */
  getTaskTypes: () => {
    return request.get<string[]>('/admin/sect-task/task-types');
  },

  /**
   * 获取所有目标类型
   */
  getTargetTypes: () => {
    return request.get<string[]>('/admin/sect-task/target-types');
  }
};

export default sectTaskApi;
