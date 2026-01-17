import { defineStore } from 'pinia';
import { ref } from 'vue';
import { monsterApi } from '@/api/monster';
import { equipmentApi } from '@/api/equipment';
import type { Equipment, EquipmentListItem } from '@/types/equipment';

export interface EquipmentOption {
  equipmentId: number;
  equipmentName: string;
  equipmentType: string;
  quality: string;
  baseScore: number;
}

export const useEquipmentStore = defineStore('equipment', () => {
  const equipmentList = ref<EquipmentOption[]>([]);
  const loading = ref(false);

  // 管理端相关状态
  const currentEquipment = ref<Equipment | null>(null);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as EquipmentListItem[]
  });

  /**
   * 获取所有装备列表（用于怪物掉落配置选择器）
   */
  const fetchEquipmentList = async () => {
    loading.value = true;
    try {
      const response = await monsterApi.getEquipmentList();
      equipmentList.value = response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 分页查询装备列表（管理端）
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    equipmentType?: string;
    quality?: string;
  }) => {
    loading.value = true;
    try {
      const response = await equipmentApi.getList({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        equipmentType: params?.equipmentType,
        quality: params?.quality
      });

      pagination.value.items = response.data.items;
      pagination.value.total = response.data.total;
      pagination.value.page = response.data.page;
      pagination.value.pageSize = response.data.pageSize;

      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 获取装备详情
   */
  const fetchDetail = async (equipmentId: number) => {
    loading.value = true;
    try {
      const response = await equipmentApi.getDetail(equipmentId);
      currentEquipment.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建装备
   */
  const createEquipment = async (data: Equipment) => {
    loading.value = true;
    try {
      const response = await equipmentApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新装备
   */
  const updateEquipment = async (equipmentId: number, data: Equipment) => {
    loading.value = true;
    try {
      await equipmentApi.update(equipmentId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除装备
   */
  const deleteEquipment = async (equipmentId: number) => {
    loading.value = true;
    try {
      await equipmentApi.delete(equipmentId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除装备
   */
  const batchDeleteEquipment = async (equipmentIds: number[]) => {
    loading.value = true;
    try {
      await equipmentApi.batchDelete(equipmentIds);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 检查名称唯一性
   */
  const checkNameUnique = async (name: string, excludeId?: number) => {
    const response = await equipmentApi.checkName(name, excludeId);
    return response.data.isUnique;
  };

  return {
    // 选择器用
    equipmentList,
    // 管理端用
    currentEquipment,
    loading,
    pagination,
    fetchEquipmentList,
    fetchList,
    fetchDetail,
    createEquipment,
    updateEquipment,
    deleteEquipment,
    batchDeleteEquipment,
    checkNameUnique
  };
});
