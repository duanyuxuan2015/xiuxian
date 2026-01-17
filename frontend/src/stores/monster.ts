import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { MonsterDetail, MonsterListItem } from '@/types/monster';
import { monsterApi } from '@/api/monster';

export const useMonsterStore = defineStore('monster', () => {
  const monsterList = ref<MonsterListItem[]>([]);
  const currentMonster = ref<MonsterDetail | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    totalPages: 0
  });

  /**
   * 获取怪物列表
   */
  const fetchMonsterList = async (params?: { page?: number; pageSize?: number; keyword?: string }) => {
    loading.value = true;
    try {
      const response = await monsterApi.getList({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword
      });
      monsterList.value = response.data.items;
      pagination.value.total = response.data.total;
      pagination.value.totalPages = response.data.totalPages;
      pagination.value.page = response.data.page;
      pagination.value.pageSize = response.data.pageSize;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 获取怪物详情
   */
  const fetchMonsterDetail = async (monsterId: number) => {
    loading.value = true;
    try {
      const response = await monsterApi.getDetail(monsterId);
      currentMonster.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建怪物
   */
  const createMonster = async (data: any) => {
    const response = await monsterApi.create(data);
    await fetchMonsterList();
    return response.data;
  };

  /**
   * 更新怪物
   */
  const updateMonster = async (monsterId: number, data: any) => {
    await monsterApi.update(monsterId, data);
    await fetchMonsterDetail(monsterId);
  };

  /**
   * 删除怪物
   */
  const deleteMonster = async (monsterId: number) => {
    await monsterApi.delete(monsterId);
    await fetchMonsterList();
    if (currentMonster.value?.monsterId === monsterId) {
      currentMonster.value = null;
    }
  };

  /**
   * 批量删除怪物
   */
  const batchDeleteMonsters = async (monsterIds: number[]) => {
    await monsterApi.batchDelete(monsterIds);
    await fetchMonsterList();
  };

  /**
   * 检查名称唯一性
   */
  const checkNameUnique = async (name: string, excludeId?: number) => {
    const response = await monsterApi.checkName(name, excludeId);
    return response.data;
  };

  /**
   * 保存掉落配置
   */
  const saveDrops = async (monsterId: number, drops: any[]) => {
    await monsterApi.saveDrops(monsterId, drops);
  };

  return {
    monsterList,
    currentMonster,
    loading,
    pagination,
    fetchMonsterList,
    fetchMonsterDetail,
    createMonster,
    updateMonster,
    deleteMonster,
    batchDeleteMonsters,
    checkNameUnique,
    saveDrops
  };
});
