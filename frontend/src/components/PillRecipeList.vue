<template>
  <div class="pill-recipe-list">
    <div class="list-header">
      <h3>丹方列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增丹方</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索丹方名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <div class="list-content" v-loading="pillRecipeStore.loading">
      <div
        v-for="item in pillRecipeStore.pagination.items || []"
        :key="item.recipeId"
        class="list-item"
        :class="{ active: selectedId === item.recipeId }"
        @click="handleSelect(item.recipeId)"
      >
        <div class="item-name">{{ item.recipeName }}</div>
        <div class="item-info">
          <el-tag size="small" type="info">Lv.{{ item.recipeTier }}</el-tag>
          <span class="item-pill">产出: {{ item.pillName }}</span>
        </div>
        <div class="item-stats">
          <span class="success-rate">成功率: {{ item.baseSuccessRate }}%</span>
          <span class="output-quantity">产出: {{ item.outputQuantity }}</span>
        </div>
      </div>

      <el-empty v-if="!pillRecipeStore.loading && (!pillRecipeStore.pagination.items || pillRecipeStore.pagination.items.length === 0)" description="暂无丹方" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="pillRecipeStore.pagination.total"
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
import { usePillRecipeStore } from '@/stores/pillRecipe';

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

const pillRecipeStore = usePillRecipeStore();

const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 加载丹方列表
const loadList = async () => {
  await pillRecipeStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined
  });
};

onMounted(() => {
  loadList();
});

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadList();
};

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadList();
};

// 选择丹方
const handleSelect = (recipeId: number) => {
  emit('update:selectedId', recipeId);
  emit('update:isCreating', false);
};

// 新增丹方
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
.pill-recipe-list {
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

        .item-pill {
          font-size: 12px;
          color: #606266;
        }
      }

      .item-stats {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #606266;

        .success-rate {
          color: #67c23a;
        }

        .output-quantity {
          color: #409eff;
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
