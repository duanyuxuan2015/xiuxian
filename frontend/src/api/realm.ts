import request from './index';
import type { Realm } from '@/types/realm';

export const realmApi = {
  /**
   * 获取所有境界列表
   */
  getAllRealms: () => {
    return request.get<Realm[]>('/admin/realms');
  }
};
