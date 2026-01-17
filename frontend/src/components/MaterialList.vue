<template>
  <div class="material-list">
    <div class="list-header">
      <h3>材料列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增材料</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索材料名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <div class="filter-bar">
      <el-select
        v-model="filterQuality"
        placeholder="品质"
        clearable
        @change="handleFilter"
        style="width: 100%; margin-bottom: 8px;"
      >
        <el-option label="凡品" value="凡品" />
        <el-option label="良品" value="良品" />
        <el-option label="上品" value="上品" />
        <el-option label="极品" value="极品" />
        <el-option label="仙品" value="仙品" />
      </el-select>

      <el-select
        v-model="filterMaterialType"
        placeholder="材料类型"
        clearable
        @change="handleFilter"
        style="width: 100%;"
      >
        <el-option
          v-for="type in materialTypes"
          :key="type"
          :label="type"
          :value="type"
        />
      </el-select>
    </div>

    <div class="list-content" v-loading="materialStore.loading">
      <div
        v-for="item in materialStore.pagination.items || []"
        :key="item.materialId"
        class="list-item"
        :class="{ active: selectedId === item.materialId }"
        @click="handleSelect(item.materialId)"
      >
        <div class="item-name">{{ item.materialName }}</div>
        <div class="item-info">
          <el-tag size="small" :type="getQualityType(item.quality)">{{ item.quality }}</el-tag>
          <span class="item-tier">Lv.{{ item.materialTier }}</span>
        </div>
        <div class="item-stats">
          <span class="material-type">{{ item.materialType }}</span>
        </div>
        <div class="item-price">
          <span>💰 {{ item.spiritStones }}</span>
        </div>
      </div>

      <el-empty v-if="!materialStore.loading && (!materialStore.pagination.items || materialStore.pagination.items.length === 0)" description="暂无材料" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="materialStore.pagination.total"
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
import { useMaterialStore } from '@/stores/material';
import materialApi from '@/api/material';

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

const materialStore = useMaterialStore();

const searchKeyword = ref('');
const filterQuality = ref('');
const filterMaterialType = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const materialTypes = ref<string[]>([]);

// 品质对应的标签类型
const getQualityType = (quality: string) => {
  const map: Record<string, string> = {
    '凡品': 'info',
    '良品': '',
    '上品': 'warning',
    '极品': 'danger',
    '仙品': 'success'
  };
  return map[quality] || '';
};

// 加载材料列表
const loadList = async () => {
  await materialStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined,
    quality: filterQuality.value || undefined,
    materialType: filterMaterialType.value || undefined
  });
};

// 加载材料类型列表
const loadMaterialTypes = async () => {
  try {
    const response = await materialApi.getMaterialTypes();
    materialTypes.value = response.data;
  } catch (error) {
    console.error('加载材料类型失败:', error);
  }
};

onMounted(() => {
  loadMaterialTypes();
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

// 选择材料
const handleSelect = (materialId: number) => {
  emit('update:selectedId', materialId);
  emit('update:isCreating', false);
};

// 新增材料
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
.material-list {
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
        margin-bottom: 4px;
      }

      .item-price {
        font-size: 12px;
        color: #f56c6c;
        font-weight: 500;
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
