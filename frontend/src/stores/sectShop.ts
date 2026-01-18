import { defineStore } from 'pinia';
import { ref } from 'vue';
import sectShopApi from '@/api/sectShop';
import type { SectShopItem, SectShopItemListItem } from '@/types/sectShop';

export const useSectShopStore = defineStore('sectShop', () => {
  const currentItem = ref<SectShopItem | null>(null);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0,
    items: [] as SectShopItemListItem[]
  });

  /**
   * 分页查询商店物品列表
   */
  const fetchList = async (params?: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    sectId?: number;
    itemType?: string;
  }) => {
    loading.value = true;
    try {
      const response = await sectShopApi.list({
        page: params?.page || pagination.value.page,
        pageSize: params?.pageSize || pagination.value.pageSize,
        keyword: params?.keyword,
        sectId: params?.sectId,
        itemType: params?.itemType
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
   * 获取商店物品详情
   */
  const fetchDetail = async (itemId: number) => {
    loading.value = true;
    try {
      const response = await sectShopApi.getDetail(itemId);
      currentItem.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 创建商店物品
   */
  const createItem = async (data: SectShopItem) => {
    loading.value = true;
    try {
      const response = await sectShopApi.create(data);
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 更新商店物品
   */
  const updateItem = async (itemId: number, data: SectShopItem) => {
    loading.value = true;
    try {
      await sectShopApi.update(itemId, data);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 删除商店物品
   */
  const deleteItem = async (itemId: number) => {
    loading.value = true;
    try {
      await sectShopApi.delete(itemId);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 批量删除商店物品
   */
  const batchDeleteItem = async (itemIds: number[]) => {
    loading.value = true;
    try {
      await sectShopApi.batchDelete(itemIds);
    } finally {
      loading.value = false;
    }
  };

  return {
    currentItem,
    loading,
    pagination,
    fetchList,
    fetchDetail,
    createItem,
    updateItem,
    deleteItem,
    batchDeleteItem
  };
});
