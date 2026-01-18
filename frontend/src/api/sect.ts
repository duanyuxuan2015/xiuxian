/**
 * 宗门管理 API
 */
import request from './index';
import type { Sect, SectListItem, PageResult } from '@/types/sect';

const sectApi = {
  /**
   * 分页查询宗门列表
   */
  list: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    sectType?: string;
  }) => {
    return request.get<PageResult<SectListItem>>('/admin/sect/list', { params });
  },

  /**
   * 获取宗门详情
   */
  getDetail: (sectId: number) => {
    return request.get<Sect>(`/admin/sect/${sectId}`);
  },

  /**
   * 创建宗门
   */
  create: (data: Sect) => {
    return request.post<{ sectId: number }>('/admin/sect', data);
  },

  /**
   * 更新宗门
   */
  update: (sectId: number, data: Sect) => {
    return request.put(`/admin/sect/${sectId}`, data);
  },

  /**
   * 删除宗门
   */
  delete: (sectId: number) => {
    return request.delete(`/admin/sect/${sectId}`);
  },

  /**
   * 批量删除宗门
   */
  batchDelete: (sectIds: number[]) => {
    return request.delete('/admin/sect/batch', { data: sectIds });
  },

  /**
   * 检查名称唯一性
   */
  checkName: (name: string, excludeId?: number) => {
    return request.get<{ isUnique: boolean }>('/admin/sect/check-name', {
      params: { name, excludeId }
    });
  },

  /**
   * 获取所有宗门类型
   */
  getSectTypes: () => {
    return request.get<string[]>('/admin/sect/sect-types');
  }
};

export default sectApi;
