import request from './index';
import type { MonsterDetail, MonsterListItem, PageResult } from '@/types/monster';

export const monsterApi = {
  /**
   * 分页查询怪物列表
   */
  getList: (params: { page: number; pageSize: number; keyword?: string }) => {
    return request.get<PageResult<MonsterListItem>>('/admin/monster/list', { params });
  },

  /**
   * 获取怪物详情（含掉落配置）
   */
  getDetail: (monsterId: number) => {
    return request.get<MonsterDetail>(`/admin/monster/${monsterId}`);
  },

  /**
   * 创建怪物
   */
  create: (data: any) => {
    return request.post<{ monsterId: number }>('/admin/monster', data);
  },

  /**
   * 更新怪物
   */
  update: (monsterId: number, data: any) => {
    return request.put(`/admin/monster/${monsterId}`, data);
  },

  /**
   * 删除怪物
   */
  delete: (monsterId: number) => {
    return request.delete(`/admin/monster/${monsterId}`);
  },

  /**
   * 批量删除怪物
   */
  batchDelete: (monsterIds: number[]) => {
    return request.delete('/admin/monster/batch', { data: monsterIds });
  },

  /**
   * 检查名称唯一性
   */
  checkName: (name: string, excludeId?: number) => {
    return request.get<{ isUnique: boolean }>('/admin/monster/check-name', {
      params: { name, excludeId }
    });
  },

  /**
   * 保存掉落配置
   */
  saveDrops: (monsterId: number, drops: any[]) => {
    return request.post(`/admin/monster/${monsterId}/drops`, drops);
  },

  /**
   * 获取所有装备列表（用于选择器）
   */
  getEquipmentList: () => {
    return request.get('/admin/monster/equipment/list');
  },

  /**
   * 获取所有材料列表（用于选择器）
   */
  getMaterialList: () => {
    return request.get('/admin/monster/material/list');
  }
};
