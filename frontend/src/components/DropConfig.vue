<template>
  <div class="drop-config">
    <div class="drop-header">
      <el-button
        type="primary"
        :icon="Plus"
        size="small"
        @click="handleAddDrop"
      >
        添加掉落
      </el-button>
      <span class="drop-count">共 {{ drops.length }} 条配置</span>
    </div>

    <draggable
      v-model="dropList"
      item-key="monsterDropId"
      handle=".drag-handle"
      tag="div"
    >
      <template #item="{ element: drop, index }">
        <el-card class="drop-item" shadow="never">
          <el-icon class="drag-handle">
            <Rank />
          </el-icon>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="装备" label-width="60px">
                <el-select
                  :model-value="drop.equipmentId"
                  filterable
                  @change="handleEquipmentChange(index, $event)"
                  style="width: 100%;"
                >
                  <el-option
                    v-for="item in equipmentList"
                    :key="item.equipmentId"
                    :label="item.equipmentName"
                    :value="item.equipmentId"
                  >
                    <div class="equipment-option">
                      <span
                        class="quality-dot"
                        :style="{ backgroundColor: getQualityColor(item.quality) }"
                      />
                      <span class="equipment-name">{{ item.equipmentName }}</span>
                      <el-tag size="small" type="info">{{ item.equipmentType }}</el-tag>
                      <el-tag
                        size="small"
                        :type="getQualityTagType(item.quality)"
                      >
                        {{ item.quality }}
                      </el-tag>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="掉落率" label-width="60px">
                <el-input-number
                  v-model="drop.dropRate"
                  :min="0"
                  :max="100"
                  :precision="1"
                  :step="0.1"
                  style="width: 100%;"
                />
                <span class="unit-text">%</span>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="数量" label-width="60px">
                <el-input-number
                  v-model="drop.dropQuantity"
                  :min="1"
                  :max="99"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="品质范围" label-width="80px">
                <el-select
                  v-model="drop.minQuality"
                  placeholder="最低"
                  clearable
                  style="width: 48%;"
                >
                  <el-option label="凡品" value="凡品" />
                  <el-option label="良品" value="良品" />
                  <el-option label="上品" value="上品" />
                  <el-option label="极品" value="极品" />
                  <el-option label="仙品" value="仙品" />
                </el-select>
                <span class="quality-separator">~</span>
                <el-select
                  v-model="drop.maxQuality"
                  placeholder="最高"
                  clearable
                  style="width: 48%;"
                >
                  <el-option label="凡品" value="凡品" />
                  <el-option label="良品" value="良品" />
                  <el-option label="上品" value="上品" />
                  <el-option label="极品" value="极品" />
                  <el-option label="仙品" value="仙品" />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="必掉" label-width="60px">
                <el-switch
                  v-model="drop.isGuaranteed"
                  active-text="是"
                  inactive-text="否"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-button
            type="danger"
            size="small"
            :icon="Delete"
            @click="handleRemoveDrop(index)"
            style="position: absolute; top: 10px; right: 10px;"
          >
            删除
          </el-button>
        </el-card>
      </template>
    </draggable>

    <el-empty
      v-if="dropList.length === 0"
      description="暂无掉落配置"
      :image-size="100"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Plus, Delete, Rank } from '@element-plus/icons-vue';
import draggable from 'vuedraggable';
import type { MonsterDrop } from '@/types/monster';

interface EquipmentOption {
  equipmentId: number;
  equipmentName: string;
  equipmentType: string;
  quality: string;
  baseScore: number;
}

interface Props {
  drops: MonsterDrop[];
  equipmentList: EquipmentOption[];
  loading?: boolean;
}

interface Emits {
  (e: 'update:drops', value: MonsterDrop[]): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const dropList = computed({
  get: () => props.drops,
  set: (value) => emit('update:drops', value)
});

const handleAddDrop = () => {
  dropList.value.push({
    monsterId: 0,
    equipmentId: 0,
    dropRate: 10.0,
    dropQuantity: 1,
    isGuaranteed: false
  });
};

const handleRemoveDrop = (index: number) => {
  dropList.value.splice(index, 1);
};

const handleEquipmentChange = (index: number, equipmentId: number) => {
  const drop = dropList.value[index];
  if (drop) {
    drop.equipmentId = equipmentId;
    const equipment = props.equipmentList.find(e => e.equipmentId === equipmentId);
    if (equipment) {
      drop.equipmentName = equipment.equipmentName;
      drop.equipmentType = equipment.equipmentType;
      drop.quality = equipment.quality;
    }
  }
};

const getQualityColor = (quality: string) => {
  const colors: Record<string, string> = {
    '普通': '#9e9e9e',
    '良品': '#4caf50',
    '稀有': '#2196f3',
    '史诗': '#9c27b0',
    '传说': '#ff9800',
    '仙品': '#f44336'
  };
  return colors[quality] || '#9e9e9e';
};

const getQualityTagType = (quality: string) => {
  const types: Record<string, string> = {
    '普通': 'info',
    '良品': 'success',
    '稀有': 'primary',
    '史诗': 'warning',
    '传说': 'danger',
    '仙品': 'danger'
  };
  return types[quality] || 'info';
};
</script>

<style scoped lang="scss">
.drop-config {
  .drop-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 8px;
    background: #f5f7fa;
    border-radius: 4px;

    .drop-count {
      font-size: 14px;
      color: #909399;
    }
  }

  .drop-item {
    margin-bottom: 12px;
    position: relative;

    .drag-handle {
      position: absolute;
      top: 12px;
      right: 100px;
      cursor: move;
      font-size: 18px;
      color: #909399;
      z-index: 1;

      &:hover {
        color: #409eff;
      }
    }

    .unit-text {
      margin-left: 8px;
      color: #909399;
      font-size: 14px;
    }

    .quality-separator {
      margin: 0 4px;
      color: #909399;
    }

    :deep(.el-form-item) {
      margin-bottom: 12px;
    }
  }

  .equipment-option {
    display: flex;
    align-items: center;
    gap: 8px;

    .quality-dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .equipment-name {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
