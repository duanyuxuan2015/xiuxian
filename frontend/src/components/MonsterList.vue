<template>
  <div class="monster-list">
    <!-- 搜索框 -->
    <el-input
      v-model="searchKeyword"
      placeholder="搜索怪物名称"
      :prefix-icon="Search"
      clearable
      @input="handleSearch"
      style="margin-bottom: 12px;"
    />

    <!-- 新增按钮 -->
    <el-button
      type="primary"
      :icon="Plus"
      @click="$emit('create')"
      style="margin-bottom: 12px; width: 100%;"
    >
      新增怪物
    </el-button>

    <!-- 怪物列表 -->
    <div class="monster-items" v-loading="monsterStore.loading">
      <div
        v-for="monster in monsterStore.monsterList"
        :key="monster.monsterId"
        class="monster-item"
        :class="{ active: monster.monsterId === selectedId }"
        @click="handleSelect(monster.monsterId)"
      >
        <div class="monster-name">{{ monster.monsterName }}</div>
        <div class="monster-info">
          <el-tag size="small" type="success">{{ monster.realmName }}</el-tag>
          <el-tag size="small" :type="getMonsterTypeTag(monster.monsterType)">
            {{ monster.monsterType }}
          </el-tag>
        </div>
        <div class="monster-stats">
          <span>速度: {{ monster.speed }}</span>
          <span>生命: {{ monster.hp }}</span>
          <span>攻击: {{ monster.attackPower }}</span>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="monsterStore.pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="prev, pager, next"
      small
      @current-change="handlePageChange"
      style="margin-top: auto; padding-top: 12px;"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { Search, Plus } from '@element-plus/icons-vue';
import { useMonsterStore } from '@/stores/monster';

interface Props {
  selectedId: number | null;
  isCreating?: boolean;
}

interface Emits {
  (e: 'update:selectedId', value: number | null): void;
  (e: 'update:isCreating', value: boolean): void;
  (e: 'create'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const monsterStore = useMonsterStore();
const searchKeyword = ref('');

const currentPage = computed({
  get: () => monsterStore.pagination.page,
  set: (val) => {
    monsterStore.pagination.page = val;
  }
});

const pageSize = computed({
  get: () => monsterStore.pagination.pageSize,
  set: (val) => {
    monsterStore.pagination.pageSize = val;
  }
});

const handleSearch = () => {
  monsterStore.fetchMonsterList({ keyword: searchKeyword.value });
};

const handleSelect = (monsterId: number) => {
  console.log('MonsterList: handleSelect called', { monsterId, currentSelectedId: props.selectedId });
  emit('update:selectedId', monsterId);
  emit('update:isCreating', false);
};

const handlePageChange = () => {
  monsterStore.fetchMonsterList();
};

const getMonsterTypeTag = (type: string) => {
  const typeMap: Record<string, string> = {
    '普通': 'info',
    '精英': 'warning',
    'BOSS': 'danger'
  };
  return typeMap[type] || 'info';
};

onMounted(() => {
  monsterStore.fetchMonsterList();
});

watch(() => props.isCreating, ( newVal) => {
  if (newVal) {
    emit('update:selectedId', null);
  }
});
</script>

<style scoped lang="scss">
.monster-list {
  height: 100%;
  display: flex;
  flex-direction: column;

  .monster-items {
    flex: 1;
    overflow-y: auto;
    margin-bottom: 12px;
  }

  .monster-item {
    padding: 12px;
    margin-bottom: 8px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s;
    background: white;

    &:hover {
      border-color: #409eff;
      background-color: #f0f9ff;
    }

    &.active {
      border-color: #409eff;
      background-color: #e6f7ff;
    }

    .monster-name {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 8px;
      color: #303133;
    }

    .monster-info {
      display: flex;
      gap: 6px;
      margin-bottom: 8px;
    }

    .monster-stats {
      display: flex;
      gap: 12px;
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
