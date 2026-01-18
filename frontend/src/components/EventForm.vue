<template>
  <div class="event-form">
    <div class="form-header">
      <h3>{{ isCreating ? '新增探索事件' : '编辑探索事件' }}</h3>
    </div>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      v-loading="loading"
    >
      <el-form-item label="所属区域" prop="areaId">
        <el-select
          v-model="formData.areaId"
          placeholder="请选择所属区域"
          style="width: 100%"
        >
          <el-option
            v-for="area in areas"
            :key="area.areaId"
            :label="area.areaName"
            :value="area.areaId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="事件类型" prop="eventType">
        <el-select
          v-model="formData.eventType"
          placeholder="请选择事件类型"
          style="width: 100%"
        >
          <el-option
            v-for="type in EventTypes"
            :key="type.value"
            :label="type.label"
            :value="type.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="事件名称" prop="eventName">
        <el-input v-model="formData.eventName" placeholder="请输入事件名称" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入事件描述"
        />
      </el-form-item>

      <el-form-item label="事件级别" prop="level">
        <el-select
          v-model="formData.level"
          placeholder="请选择事件级别"
          style="width: 100%"
        >
          <el-option
            v-for="lvl in EventLevels"
            :key="lvl.value"
            :label="lvl.label"
            :value="lvl.value"
          />
        </el-select>
        <span class="form-tip">级别影响触发概率和奖励质量</span>
      </el-form-item>

      <!-- 奖励配置：只有采集和机缘类型显示 -->
      <template v-if="showRewardConfig">
        <el-divider content-position="left">
          <span style="font-size: 14px; color: #409eff;">奖励配置</span>
        </el-divider>

        <!-- 已配置奖励信息展示 -->
        <div v-if="formData.eventId && formData.rewardId" class="info-box">
          <div class="info-item">
            <span class="label">奖励类型:</span>
            <span class="value">{{ getRewardTypeName(formData.rewardType) }}</span>
          </div>
          <div class="info-item">
            <span class="label">奖励ID:</span>
            <span class="value">{{ formData.rewardId }}</span>
          </div>
          <div class="info-item">
            <span class="label">奖励数量:</span>
            <span class="value">{{ formData.rewardQuantityMin }} - {{ formData.rewardQuantityMax }}</span>
          </div>
        </div>

        <el-form-item label="奖励类型" prop="rewardType">
          <el-select
            v-model="formData.rewardType"
            placeholder="请选择奖励类型"
            style="width: 100%"
          >
            <el-option
              v-for="type in RewardTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="奖励物品" prop="rewardId">
          <el-select
            v-model="formData.rewardId"
            placeholder="请选择奖励物品"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in rewardItems"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 12px">
                {{ item.extra }}
              </span>
            </el-option>
          </el-select>
          <span class="form-tip">根据奖励类型选择对应的物品</span>
        </el-form-item>

        <el-form-item label="奖励数量最小" prop="rewardQuantityMin">
          <el-input-number
            v-model="formData.rewardQuantityMin"
            :min="0"
            placeholder="最小数量"
          />
        </el-form-item>

        <el-form-item label="奖励数量最大" prop="rewardQuantityMax">
          <el-input-number
            v-model="formData.rewardQuantityMax"
            :min="0"
            placeholder="最大数量"
          />
          <span class="form-tip">实际奖励数量将在最小值和最大值之间随机</span>
        </el-form-item>
      </template>

      <!-- 怪物配置：只有战斗类型显示 -->
      <template v-if="showMonsterId">
        <el-divider content-position="left">
          <span style="font-size: 14px; color: #f56c6c;">战斗配置</span>
        </el-divider>

        <!-- 已配置怪物信息展示 -->
        <div v-if="formData.eventId && getMonsterInfo()" class="info-box">
          <div class="info-item">
            <span class="label">怪物ID:</span>
            <span class="value">{{ getMonsterInfo()?.monsterId }}</span>
          </div>
          <div class="info-item">
            <span class="label">怪物名称:</span>
            <span class="value">{{ getMonsterInfo()?.monsterName }}</span>
          </div>
          <div class="info-item">
            <span class="label">境界:</span>
            <span class="value">{{ getMonsterInfo()?.realmName }}</span>
          </div>
          <div class="info-item">
            <span class="label">生命值:</span>
            <span class="value">{{ getMonsterInfo()?.hp }}</span>
          </div>
          <div class="info-item">
            <span class="label">攻击力:</span>
            <span class="value">{{ getMonsterInfo()?.attackPower }}</span>
          </div>
          <div class="info-item">
            <span class="label">防御力:</span>
            <span class="value">{{ getMonsterInfo()?.defensePower }}</span>
          </div>
        </div>

        <el-form-item label="关联怪物" prop="monsterId">
          <el-select
            v-model="formData.monsterId"
            placeholder="请选择怪物"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="monster in monsters"
              :key="monster.monsterId"
              :label="`${monster.monsterName} (${monster.realmName})`"
              :value="monster.monsterId"
            >
              <span style="float: left">{{ monster.monsterName }}</span>
              <span style="float: right; color: #8492a6; font-size: 12px">
                {{ monster.realmName }}
              </span>
            </el-option>
          </el-select>
          <span class="form-tip">战斗事件需要关联一个怪物</span>
        </el-form-item>
      </template>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { explorationEventApi, explorationAreaApi } from '@/api/exploration';
import { monsterApi } from '@/api/monster';
import type { ExplorationEventFormData, ExplorationArea } from '@/types/exploration';
import type { MonsterListItem } from '@/types/monster';
import { EventTypes, EventLevels, RewardTypes } from '@/types/exploration';

interface Props {
  eventId?: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const formRef = ref<FormInstance>();
const loading = ref(false);
const areas = ref<ExplorationArea[]>([]);
const monsters = ref<MonsterListItem[]>([]);
const rewardItems = ref<Array<{ id: number; name: string; extra?: string }>>([]);

// 中文事件类型到英文的映射
const eventTypeMap: Record<string, string> = {
  '采集': 'gather',
  '战斗': 'combat',
  '机缘': 'fortune',
  '陷阱': 'trap',
  '无事': 'none'
};

// 标准化事件类型（将中文转换为英文）
const normalizeEventType = (type: string) => {
  return eventTypeMap[type] || type;
};

// 获取标准化的事件类型
const getNormalizedEventType = computed(() => {
  return normalizeEventType(formData.value.eventType);
});

// 获取怪物信息
const getMonsterInfo = () => {
  if (!formData.value.monsterId) return null;
  return monsters.value.find(m => m.monsterId === formData.value.monsterId);
};

// 获取奖励类型名称
const getRewardTypeName = (type: string) => {
  const found = RewardTypes.find(t => t.value === type);
  return found?.label || type;
};

const formData = ref<ExplorationEventFormData>({
  eventId: undefined,
  areaId: 0,
  eventType: 'gather',
  eventName: '',
  description: '',
  level: 1,
  rewardType: 'material',
  rewardId: undefined,
  rewardQuantityMin: 1,
  rewardQuantityMax: 1,
  monsterId: undefined
});

// 计算属性：是否显示奖励配置（只有采集和机缘类型需要）
const showRewardConfig = computed(() => {
  const normalizedType = getNormalizedEventType.value;
  return normalizedType === 'gather' || normalizedType === 'fortune';
});

// 计算属性：是否显示怪物ID（只有战斗类型需要）
const showMonsterId = computed(() => {
  const normalizedType = getNormalizedEventType.value;
  return normalizedType === 'combat';
});

// 动态验证规则
const rules = computed<FormRules>(() => {
  const baseRules: FormRules = {
    areaId: [
      { required: true, message: '请选择所属区域', trigger: 'change' }
    ],
    eventType: [
      { required: true, message: '请选择事件类型', trigger: 'change' }
    ],
    eventName: [
      { required: true, message: '请输入事件名称', trigger: 'blur' }
    ],
    level: [
      { required: true, message: '请选择事件级别', trigger: 'change' }
    ]
  };

  // 只有采集和机缘类型需要验证奖励类型
  if (showRewardConfig.value) {
    baseRules.rewardType = [
      { required: true, message: '请选择奖励类型', trigger: 'change' }
    ];
  }

  // 只有战斗类型需要验证怪物ID
  if (showMonsterId.value) {
    baseRules.monsterId = [
      { required: true, message: '请选择关联怪物', trigger: 'change' }
    ];
  }

  return baseRules;
});

const loadAreas = async () => {
  try {
    const response = await explorationAreaApi.getList();
    areas.value = response.data;
  } catch (error) {
    ElMessage.error('加载区域列表失败');
    console.error(error);
  }
};

const loadMonsters = async () => {
  try {
    // 获取所有怪物，使用大的pageSize来获取全部
    const response = await monsterApi.getList({ page: 1, pageSize: 1000 });
    monsters.value = response.data.items;
    console.log('加载怪物列表:', monsters.value);
  } catch (error) {
    ElMessage.error('加载怪物列表失败');
    console.error(error);
  }
};

// 加载奖励物品列表
const loadRewardItems = async () => {
  if (!formData.value.rewardType) {
    rewardItems.value = [];
    return;
  }

  try {
    if (formData.value.rewardType === 'material') {
      // 加载材料列表
      const response = await monsterApi.getMaterialList();
      rewardItems.value = (response.data || []).map((item: any) => ({
        id: item.materialId,
        name: item.materialName,
        extra: `${item.materialType} / ${item.quality}`
      }));
    } else if (formData.value.rewardType === 'pill') {
      // 加载丹药列表 - 使用分页查询
      const { pillApi } = await import('@/api/pill');
      const response = await pillApi.getList({ page: 1, pageSize: 1000 });
      rewardItems.value = (response.data?.items || []).map((item: any) => ({
        id: item.pillId,
        name: item.pillName,
        extra: `${item.quality} / ${item.pillType}`
      }));
    } else if (formData.value.rewardType === 'equipment') {
      // 加载装备列表
      const response = await monsterApi.getEquipmentList();
      rewardItems.value = (response.data || []).map((item: any) => ({
        id: item.equipmentId,
        name: item.equipmentName,
        extra: `${item.equipmentType} / ${item.quality}`
      }));
    }
    console.log(`加载${formData.value.rewardType}列表成功，共${rewardItems.value.length}个物品`);
  } catch (error) {
    ElMessage.error('加载奖励物品列表失败');
    console.error('加载奖励物品列表错误:', error);
    rewardItems.value = [];
  }
};

const loadEventDetail = async () => {
  if (!props.eventId || props.isCreating) return;

  loading.value = true;
  try {
    const response = await explorationEventApi.getDetail(props.eventId);
    formData.value = response.data;

    const normalizedType = normalizeEventType(formData.value.eventType);
    console.log('加载事件详情成功:', {
      原始类型: formData.value.eventType,
      标准化类型: normalizedType,
      isCombat: normalizedType === 'combat',
      isReward: normalizedType === 'gather' || normalizedType === 'fortune',
      monsterId: formData.value.monsterId,
      rewardId: formData.value.rewardId,
      rewardType: formData.value.rewardType
    });

    // 如果是采集或机缘类型，加载奖励物品列表
    if (showRewardConfig.value && formData.value.rewardType) {
      await loadRewardItems();
    }
  } catch (error) {
    ElMessage.error('加载事件详情失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      if (props.isCreating) {
        await explorationEventApi.create(formData.value);
        ElMessage.success('创建成功');
      } else {
        await explorationEventApi.update(formData.value);
        ElMessage.success('更新成功');
      }
      emit('save');
    } catch (error) {
      ElMessage.error(props.isCreating ? '创建失败' : '更新失败');
      console.error(error);
    } finally {
      loading.value = false;
    }
  });
};

const handleCancel = () => {
  emit('cancel');
};

// 监听事件类型变化，清理不相关的字段数据
watch(() => formData.value.eventType, (newType) => {
  const normalizedType = normalizeEventType(newType);
  console.log('事件类型变化:', {
    原始类型: newType,
    标准化类型: normalizedType,
    showRewardConfig: showRewardConfig.value,
    showMonsterId: showMonsterId.value
  });

  if (!showRewardConfig.value) {
    // 清理奖励相关字段
    formData.value.rewardType = 'material';
    formData.value.rewardId = undefined;
    formData.value.rewardQuantityMin = 1;
    formData.value.rewardQuantityMax = 1;
    rewardItems.value = [];
  }

  if (!showMonsterId.value) {
    // 清理怪物ID
    formData.value.monsterId = undefined;
  }
});

// 监听奖励类型变化，重新加载奖励物品列表
watch(() => formData.value.rewardType, async (newType) => {
  if (newType && showRewardConfig.value) {
    console.log('奖励类型变化，加载物品列表:', newType);
    // 清空已选择的奖励ID
    formData.value.rewardId = undefined;
    await loadRewardItems();
  }
});

watch(() => props.eventId, () => {
  loadEventDetail();
}, { immediate: true });

onMounted(() => {
  loadAreas();
  loadMonsters();
});
</script>

<style scoped lang="scss">
.event-form {
  padding: 20px;

  .form-header {
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 18px;
      color: #333;
    }
  }

  .form-tip {
    margin-left: 10px;
    font-size: 12px;
    color: #999;
  }

  .el-form-item {
    margin-bottom: 20px;
  }

  .info-box {
    background: #f5f7fa;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 12px;
    margin-bottom: 15px;
    font-size: 13px;

    .info-item {
      display: inline-block;
      margin-right: 20px;
      margin-bottom: 5px;

      .label {
        color: #909399;
        margin-right: 5px;
      }

      .value {
        color: #333;
        font-weight: 500;
      }
    }
  }
}
</style>
