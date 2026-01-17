<template>
  <div class="equipment-list">
    <div class="list-header">
      <h3>装备列表</h3>
      <el-button type="primary" @click="handleCreate" :icon="Plus">新增装备</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索装备名称"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <div class="filter-bar">
      <el-select
        v-model="filterType"
        placeholder="装备类型"
        clearable
        @change="handleFilter"
        style="width: 100%; margin-bottom: 8px;"
      >
        <el-option label="武器" value="武器" />
        <el-option label="头盔" value="头盔" />
        <el-option label="胸甲" value="胸甲" />
        <el-option label="护腿" value="护腿" />
        <el-option label="鞋子" value="鞋子" />
        <el-option label="饰品" value="饰品" />
      </el-select>

      <el-select
        v-model="filterQuality"
        placeholder="品质"
        clearable
        @change="handleFilter"
        style="width: 100%;"
      >
        <el-option label="凡品" value="凡品" />
        <el-option label="良品" value="良品" />
        <el-option label="上品" value="上品" />
        <el-option label="极品" value="极品" />
        <el-option label="仙品" value="仙品" />
      </el-select>
    </div>

    <div class="list-content" v-loading="equipmentStore.loading">
      <div
        v-for="item in equipmentStore.pagination.items || []"
        :key="item.equipmentId"
        class="list-item"
        :class="{ active: selectedId === item.equipmentId }"
        @click="handleSelect(item.equipmentId)"
      >
        <div class="item-name">{{ item.equipmentName }}</div>
        <div class="item-info">
          <el-tag size="small" :type="getQualityType(item.quality)">{{ item.quality }}</el-tag>
          <span class="item-type">{{ item.equipmentType }}</span>
        </div>
        <div class="item-stats">
          <span v-if="item.attackPower">⚔️ {{ item.attackPower }}</span>
          <span v-if="item.defensePower">🛡️ {{ item.defensePower }}</span>
        </div>
      </div>

      <el-empty v-if="!equipmentStore.loading && (!equipmentStore.pagination.items || equipmentStore.pagination.items.length === 0)" description="暂无装备" />
    </div>

    <div class="list-pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="equipmentStore.pagination.total"
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
import { useEquipmentStore } from '@/stores/equipment';

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

const equipmentStore = useEquipmentStore();

const searchKeyword = ref('');
const filterType = ref('');
const filterQuality = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

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

// 加载装备列表
const loadList = async () => {
  await equipmentStore.fetchList({
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchKeyword.value || undefined,
    equipmentType: filterType.value || undefined,
    quality: filterQuality.value || undefined
  });
};

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

// 选择装备
const handleSelect = (equipmentId: number) => {
  emit('update:selectedId', equipmentId);
  emit('update:isCreating', false);
};

// 新增装备
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

onMounted(() => {
  loadList();
});
</script>

<style scoped lang="scss">
.equipment-list {
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

        .item-type {
          font-size: 12px;
          color: #909399;
        }
      }

      .item-stats {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: #606266;
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
