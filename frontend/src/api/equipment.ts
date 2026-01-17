import request from './index';
import type { Equipment, EquipmentListItem, PageResult } from '@/types/equipment';

export const equipmentApi = {
  /**
   * 分页查询装备列表
   */
  getList: (params: {
    page: number;
    pageSize: number;
    keyword?: string;
    equipmentType?: string;
    quality?: string;
  }) => {
    return request.get<PageResult<EquipmentListItem>>('/admin/equipment/list', { params });
  },

  /**
   * 获取装备详情
   */
  getDetail: (equipmentId: number) => {
    return request.get<Equipment>(`/admin/equipment/${equipmentId}`);
  },

  /**
   * 创建装备
   */
  create: (data: Equipment) => {
    return request.post<{ equipmentId: number }>('/admin/equipment', data);
  },

  /**
   * 更新装备
   */
  update: (equipmentId: number, data: Equipment) => {
    return request.put(`/admin/equipment/${equipmentId}`, data);
  },

  /**
   * 删除装备
   */
  delete: (equipmentId: number) => {
    return request.delete(`/admin/equipment/${equipmentId}`);
  },

  /**
   * 批量删除装备
   */
  batchDelete: (equipmentIds: number[]) => {
    return request.delete('/admin/equipment/batch', { data: equipmentIds });
  },

  /**
   * 检查名称唯一性
   */
  checkName: (name: string, excludeId?: number) => {
    return request.get<{ isUnique: boolean }>('/admin/equipment/check-name', {
      params: { name, excludeId }
    });
  }
};
