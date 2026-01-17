import request from './index';
import type { Pill, PillListItem, PageResult } from '@/types/pill';

export const pillApi = {
  /**
   * 分页查询丹药列表
   */
  getList: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    quality?: string;
    effectType?: string;
  }) => {
    return request.get<PageResult<PillListItem>>('/admin/pill/list', { params });
  },

  /**
   * 获取丹药详情
   */
  getDetail: (pillId: number) => {
    return request.get<Pill>(`/admin/pill/${pillId}`);
  },

  /**
   * 创建丹药
   */
  create: (data: Pill) => {
    return request.post<{ pillId: number }>('/admin/pill', data);
  },

  /**
   * 更新丹药
   */
  update: (pillId: number, data: Pill) => {
    return request.put(`/admin/pill/${pillId}`, data);
  },

  /**
   * 删除丹药
   */
  delete: (pillId: number) => {
    return request.delete(`/admin/pill/${pillId}`);
  },

  /**
   * 批量删除丹药
   */
  batchDelete: (pillIds: number[]) => {
    return request.delete('/admin/pill/batch', { data: pillIds });
  },

  /**
   * 检查名称唯一性
   */
  checkName: (name: string, excludeId?: number) => {
    return request.get<{ isUnique: boolean }>('/admin/pill/check-name', {
      params: { name, excludeId }
    });
  },

  /**
   * 获取所有效果类型列表
   */
  getEffectTypes: () => {
    return request.get<string[]>('/admin/pill/effect-types');
  }
};
