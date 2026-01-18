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

          <!-- 物品类型选择 -->
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="物品类型" label-width="80px">
                <el-select
                  :model-value="drop.itemType"
                  @change="handleItemTypeChange(index, $event)"
                  style="width: 100%;"
                >
                  <el-option label="装备" value="equipment" />
                  <el-option label="材料" value="material" />
                </el-select>
              </el-form-item>
            </el-col>

            <!-- 动态物品选择器 -->
            <el-col :span="drop.itemType === 'material' ? 12 : 18">
              <!-- 装备选择器 -->
              <el-form-item v-if="drop.itemType === 'equipment'" label="装备" label-width="60px">
                <el-select
                  :model-value="drop.itemId"
                  filterable
                  @change="handleItemChange(index, $event)"
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
                      <span class="item-name">{{ item.equipmentName }}</span>
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

              <!-- 材料选择器 -->
              <el-form-item v-else label="材料" label-width="60px">
                <el-select
                  :model-value="drop.itemId"
                  filterable
                  @change="handleItemChange(index, $event)"
                  style="width: 100%;"
                >
                  <el-option
                    v-for="item in materialList"
                    :key="item.materialId"
                    :label="item.materialName"
                    :value="item.materialId"
                  >
                    <div class="material-option">
                      <span
                        class="quality-dot"
                        :style="{ backgroundColor: getQualityColor(item.quality) }"
                      />
                      <span class="item-name">{{ item.materialName }}</span>
                      <el-tag size="small" type="info">{{ item.materialType }}</el-tag>
                      <el-tag
                        size="small"
                        :type="getQualityTagType(item.quality)"
                      >
                        {{ item.quality }}
                      </el-tag>
                      <el-tag size="small" type="warning">T{{ item.materialTier }}</el-tag>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="6">
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

          <!-- 数量和品质范围 -->
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
import { computed, ref, onMounted } from 'vue';
import { Plus, Delete, Rank } from '@element-plus/icons-vue';
import draggable from 'vuedraggable';
import { monsterApi } from '@/api/monster';
import type { MonsterDrop } from '@/types/monster';

interface EquipmentOption {
  equipmentId: number;
  equipmentName: string;
  equipmentType: string;
  quality: string;
  baseScore: number;
}

interface MaterialOption {
  materialId: number;
  materialName: string;
  materialType: string;
  quality: string;
  materialTier: number;
}

interface Props {
  drops: MonsterDrop[];
  loading?: boolean;
}

interface Emits {
  (e: 'update:drops', value: MonsterDrop[]): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const equipmentList = ref<EquipmentOption[]>([]);
const materialList = ref<MaterialOption[]>([]);

const dropList = computed({
  get: () => props.drops,
  set: (value) => emit('update:drops', value)
});

onMounted(async () => {
  // 加载装备列表
  const equipRes = await monsterApi.getEquipmentList();
  equipmentList.value = equipRes.data || [];

  // 加载材料列表
  const materialRes = await monsterApi.getMaterialList();
  materialList.value = materialRes.data || [];
});

const handleAddDrop = () => {
  dropList.value.push({
    monsterId: 0,
    itemType: 'equipment', // 默认装备
    itemId: 0,
    dropRate: 10.0,
    dropQuantity: 1,
    isGuaranteed: false
  });
};

const handleRemoveDrop = (index: number) => {
  dropList.value.splice(index, 1);
};

const handleItemTypeChange = (index: number, itemType: string) => {
  const drop = dropList.value[index];
  if (drop) {
    drop.itemType = itemType;
    drop.itemId = 0; // 重置物品ID
    drop.itemName = ''; // 重置名称
    drop.quality = ''; // 重置品质
  }
};

const handleItemChange = (index: number, itemId: number) => {
  const drop = dropList.value[index];
  if (drop) {
    drop.itemId = itemId;

    // 根据类型设置名称和品质
    if (drop.itemType === 'equipment') {
      const equipment = equipmentList.value.find(e => e.equipmentId === itemId);
      if (equipment) {
        drop.itemName = equipment.equipmentName;
        drop.quality = equipment.quality;
      }
    } else if (drop.itemType === 'material') {
      const material = materialList.value.find(m => m.materialId === itemId);
      if (material) {
        drop.itemName = material.materialName;
        drop.quality = material.quality;
      }
    }
  }
};

const getQualityColor = (quality: string) => {
  const colors: Record<string, string> = {
    '凡品': '#9e9e9e',
    '良品': '#4caf50',
    '上品': '#2196f3',
    '极品': '#9c27b0',
    '仙品': '#ff9800'
  };
  return colors[quality] || '#9e9e9e';
};

const getQualityTagType = (quality: string) => {
  const types: Record<string, string> = {
    '凡品': 'info',
    '良品': 'success',
    '上品': 'primary',
    '极品': 'warning',
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

  .equipment-option, .material-option {
    display: flex;
    align-items: center;
    gap: 8px;

    .quality-dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .item-name {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
