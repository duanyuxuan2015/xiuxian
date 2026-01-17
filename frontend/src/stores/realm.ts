import { defineStore } from 'pinia';
import { ref } from 'vue';
import { realmApi } from '@/api/realm';
import type { Realm } from '@/types/realm';

export const useRealmStore = defineStore('realm', () => {
  const realmList = ref<Realm[]>([]);
  const loading = ref(false);

  /**
   * 获取所有境界列表
   */
  const fetchRealmList = async () => {
    // 如果已经加载过，直接返回
    if (realmList.value.length > 0) {
      return realmList.value;
    }

    loading.value = true;
    try {
      const response = await realmApi.getAllRealms();
      realmList.value = response.data;
      return response.data;
    } finally {
      loading.value = false;
    }
  };

  return {
    realmList,
    loading,
    fetchRealmList
  };
});
