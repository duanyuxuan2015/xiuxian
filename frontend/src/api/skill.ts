/**
 * 技能管理 API
 */
import request from './index';
import type { Skill, SkillListItem, PageResult, SectOption } from '@/types/skill';

const skillApi = {
  /**
   * 分页查询技能列表
   */
  list: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    sectId?: number;
    functionType?: string;
  }) => {
    return request.get<PageResult<SkillListItem>>('/admin/skill/list', { params });
  },

  /**
   * 获取技能详情
   */
  getDetail: (skillId: number) => {
    return request.get<Skill>(`/admin/skill/${skillId}`);
  },

  /**
   * 创建技能
   */
  create: (data: Skill) => {
    return request.post<{ skillId: number }>('/admin/skill', data);
  },

  /**
   * 更新技能
   */
  update: (skillId: number, data: Skill) => {
    return request.put(`/admin/skill/${skillId}`, data);
  },

  /**
   * 删除技能
   */
  delete: (skillId: number) => {
    return request.delete(`/admin/skill/${skillId}`);
  },

  /**
   * 批量删除技能
   */
  batchDelete: (skillIds: number[]) => {
    return request.delete('/admin/skill/batch', { data: skillIds });
  },

  /**
   * 获取所有宗门列表
   */
  getSects: () => {
    return request.get<SectOption[]>('/admin/skill/sects');
  },

  /**
   * 获取所有功能类型
   */
  getFunctionTypes: () => {
    return request.get<string[]>('/admin/skill/function-types');
  },

  /**
   * 获取所有元素类型
   */
  getElementTypes: () => {
    return request.get<string[]>('/admin/skill/element-types');
  }
};

export default skillApi;
