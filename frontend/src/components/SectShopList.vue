<template>
  <div class="sect-shop-list">
    <div class="list-header">
      <h3>商店物品列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增物品</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索物品名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <div class="filter-bar">
      <el-select
        v-model="filterSectId"
        placeholder="所属宗门"
        clearable
        @change="handleFilter"
        style="width: 100%; margin-bottom: 8px;"
      >
        <el-option
          v-for="sect in sects"
          :key="sect.sectId"
          :label="sect.sectName"
          :value="sect.sectId"
        />
      </el-select>

      <el-select
        v-model="filterItemType"
        placeholder="物品类型"
        clearable
        @change="handleFilter"
        style="width: 100%;"
      >
        <el-option
          v-for="type in itemTypes"
          :key="type"
          :label="type"
          :value="type"
        />
      </el-select>
    </div>

    <div class="list-content" v-loading="sectShopStore.loading">
      <div
        v-for="item in sectShopStore.pagination.items || []"
        :key="item.itemId"
        class="list-item"
        :class="{ active: selectedId === item.itemId }"
        @click="handleSelect(item.itemId)"
      >
        <div class="item-name">{{ item.itemName }}</div>
        <div class="item-info">
          <el-tag size="small" type="primary">{{ item.itemType }}</el-tag>
          <span class="item-tier">Lv.{{ item.itemTier }}</span>
        </div>
        <div class="item-stats">
          <span class="sect-name">{{ item.sectName }}</span>
        </div>
        <div class="item-price-stock">
          <span class="price">💰 {{ item.price }}</span>
          <span class="stock">库存: {{ item.currentStock }}</span>
        </div>
      </div>

      <el-empty v-if="!sectShopStore.loading && (!sectShopStore.pagination.items || sectShopStore.pagination.items.length === 0)" description="暂无物品" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="sectShopStore.pagination.total"
        layout="prev, pager, next"
        small
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { Plus, Search } from '@element-plus/icons-vue';
import { useSectShopStore } from '@/stores/sectShop';
import sectShopApi from '@/api/sectShop';
import type { SectOption } from '@/types/sectShop';

interface Props {
  selectedId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'update:selectedId', value: number | null): void;
  (e: 'update:isCreating', value: boolean): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const sectShopStore = useSectShopStore();

const searchKeyword = ref('');
const filterSectId = ref<number | null>(null);
const filterItemType = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const sects = ref<SectOption[]>([]);
const itemTypes = ref<string[]>([]);

// 加载商店物品列表
const loadList = async () => {
  await sectShopStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined,
    sectId: filterSectId.value || undefined,
    itemType: filterItemType.value || undefined
  });
};

// 加载宗门列表
const loadSects = async () => {
  try {
    const response = await sectShopApi.getSects();
    sects.value = response.data;
  } catch (error) {
    console.error('加载宗门列表失败:', error);
  }
};

// 加载物品类型列表
const loadItemTypes = async () => {
  try {
    const response = await sectShopApi.getItemTypes();
    itemTypes.value = response.data;
  } catch (error) {
    console.error('加载物品类型失败:', error);
  }
};

onMounted(() => {
  loadSects();
  loadItemTypes();
  loadList();
});

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadList();
};

// 筛选
const handleFilter = () => {
  currentPage.value = 1;
  loadList();
};

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadList();
};

// 选择物品
const handleSelect = (itemId: number) => {
  emit('update:selectedId', itemId);
  emit('update:isCreating', false);
};

// 新增物品
const handleCreate = () => {
  emit('update:selectedId', null);
  emit('update:isCreating', true);
};

// 监听 isCreating 变化
watch(() => props.isCreating, (newVal) => {
  if (newVal) {
    emit('update:selectedId', null);
  }
});
</script>

<style scoped lang="scss">
.sect-shop-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  border-right: 1px solid #dcdfe6;

  .list-header {
    padding: 16px;
    background: white;
    border-bottom: 1px solid #dcdfe6;
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
    }
  }

  .search-bar {
    padding: 12px 16px;
    background: white;
    border-bottom: 1px solid #dcdfe6;
  }

  .filter-bar {
    padding: 8px 16px;
    background: white;
    border-bottom: 1px solid #dcdfe6;
  }

  .list-content {
    flex: 1;
    overflow-y: auto;
    padding: 8px;

    .list-item {
      background: white;
      border-radius: 4px;
      padding: 12px;
      margin-bottom: 8px;
      cursor: pointer;
      transition: all 0.2s;
      border: 2px solid transparent;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      &.active {
        border-color: var(--el-color-primary);
        background: #f0f7ff;
      }

      .item-name {
        font-weight: 500;
        margin-bottom: 8px;
        font-size: 15px;
      }

      .item-info {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 6px;

        .item-tier {
          font-size: 12px;
          color: #909399;
        }
      }

      .item-stats {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #606266;
        margin-bottom: 6px;

        .sect-name {
          color: #409eff;
        }
      }

      .item-price-stock {
        display: flex;
        justify-content: space-between;
        font-size: 12px;

        .price {
          color: #f56c6c;
          font-weight: 500;
        }

        .stock {
          color: #67c23a;
        }
      }
    }
  }

  .list-pagination {
    padding: 12px;
    background: white;
    border-top: 1px solid #dcdfe6;
    text: center;
  }
}
</style>
