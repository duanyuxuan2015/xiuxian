<template>
  <div class="monster-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="怪物名称" prop="monsterName">
                <el-input
                  v-model="formData.monsterName"
                  placeholder="请输入怪物名称"
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="境界" prop="realmId">
                <el-select
                  v-model="formData.realmId"
                  placeholder="请选择境界"
                  style="width: 100%;"
                  :loading="realmStore.loading"
                >
                  <el-option
                    v-for="realm in realmStore.realmList"
                    :key="realm.id"
                    :label="realm.realmName"
                    :value="realm.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="怪物类型" prop="monsterType">
            <el-select v-model="formData.monsterType" style="width: 100%;">
              <el-option label="普通" value="普通" />
              <el-option label="精英" value="精英" />
              <el-option label="BOSS" value="BOSS" />
            </el-select>
          </el-form-item>

          <el-divider content-position="left">基础属性</el-divider>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="速度" prop="speed">
                <el-input-number
                  v-model="formData.speed"
                  :min="1"
                  :max="999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="生命值" prop="hp">
                <el-input-number
                  v-model="formData.hp"
                  :min="1"
                  :max="99999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="攻击力" prop="attackPower">
                <el-input-number
                  v-model="formData.attackPower"
                  :min="0"
                  :max="9999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="防御力" prop="defensePower">
                <el-input-number
                  v-model="formData.defensePower"
                  :min="0"
                  :max="9999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="攻击元素" prop="attackElement">
                <el-select v-model="formData.attackElement" style="width: 100%;">
                  <el-option label="物理" value="物理" />
                  <el-option label="冰" value="冰" />
                  <el-option label="火" value="火" />
                  <el-option label="雷" value="雷" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left">抗性</el-divider>

          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="物理抗性">
                <el-input-number
                  v-model="formData.physicalResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="冰抗性">
                <el-input-number
                  v-model="formData.iceResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="火抗性">
                <el-input-number
                  v-model="formData.fireResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="雷抗性">
                <el-input-number
                  v-model="formData.lightningResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left">奖励</el-divider>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="体力消耗">
                <el-input-number
                  v-model="formData.staminaCost"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="经验奖励" prop="expReward">
                <el-input-number
                  v-model="formData.expReward"
                  :min="0"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="灵石奖励">
                <el-input-number
                  v-model="formData.spiritStonesReward"
                  :min="0"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- Tab 2: 掉落配置 -->
        <el-tab-pane label="掉落配置" name="drops">
          <DropConfig
            v-model:drops="formData.drops"
            :equipment-list="equipmentStore.equipmentList"
            :loading="equipmentStore.loading"
          />
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <!-- 操作按钮 -->
    <div class="form-actions">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button
        type="primary"
        :loading="saving"
        @click="handleSave"
      >
        保存
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useMonsterStore } from '@/stores/monster';
import { useEquipmentStore } from '@/stores/equipment';
import { useRealmStore } from '@/stores/realm';
import DropConfig from './DropConfig.vue';
import type { MonsterDrop } from '@/types/monster';

interface Props {
  monsterId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const monsterStore = useMonsterStore();
const equipmentStore = useEquipmentStore();
const realmStore = useRealmStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);

// 表单数据默认值
const getDefaultFormData = () => ({
  monsterName: '',
  realmId: undefined as number | undefined,
  monsterType: '普通',
  speed: 100,
  hp: 1000,
  attackPower: 100,
  defensePower: 50,
  attackElement: '物理',
  physicalResist: 0,
  iceResist: 0,
  fireResist: 0,
  lightningResist: 0,
  staminaCost: 10,
  expReward: 100,
  spiritStonesReward: 50,
  drops: [] as MonsterDrop[]
});

const formData = ref(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  monsterName: [
    { required: true, message: '请输入怪物名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在2-50个字符之间', trigger: 'blur' }
  ],
  realmId: [
    { required: true, message: '请选择境界', trigger: 'change' }
  ],
  monsterType: [
    { required: true, message: '请选择怪物类型', trigger: 'change' }
  ],
  expReward: [
    { required: true, message: '请输入经验奖励', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await monsterStore.createMonster(formData.value);
      ElMessage.success('创建成功');
    } else {
      await monsterStore.updateMonster(props.monsterId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载怪物详情到表单
const loadMonsterDetail = async (monsterId: number) => {
  const detail = await monsterStore.fetchMonsterDetail(monsterId);
  formData.value = {
    monsterName: detail.monsterName,
    realmId: detail.realmId,
    monsterType: detail.monsterType,
    speed: detail.speed,
    hp: detail.hp,
    attackPower: detail.attackPower,
    defensePower: detail.defensePower,
    attackElement: detail.attackElement,
    physicalResist: detail.physicalResist,
    iceResist: detail.iceResist,
    fireResist: detail.fireResist,
    lightningResist: detail.lightningResist,
    staminaCost: detail.staminaCost,
    expReward: detail.expReward,
    spiritStonesReward: detail.spiritStonesReward,
    drops: detail.drops || []
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

onMounted(async () => {
  await equipmentStore.fetchEquipmentList();
  await realmStore.fetchRealmList();

  if (!props.isCreating && props.monsterId) {
    await loadMonsterDetail(props.monsterId);
  }
});

// 监听 monsterId 变化，重新加载数据
watch(() => props.monsterId, async (newMonsterId, oldMonsterId) => {
  console.log('MonsterForm: monsterId changed', { old: oldMonsterId, new: newMonsterId, isCreating: props.isCreating });
  if (newMonsterId) {
    await loadMonsterDetail(newMonsterId);
  }
});

// 监听 isCreating 变化，重置表单数据
watch(() => props.isCreating, (newVal) => {
  if (newVal) {
    // 重置为默认表单数据
    formData.value = getDefaultFormData();
    activeTab.value = 'basic';
    // 清除表单验证错误
    formRef.value?.clearValidate();
  }
});
</script>

<style scoped lang="scss">
.monster-form {
  height: 100%;
  display: flex;
  flex-direction: column;

  .el-tabs {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;

    :deep(.el-tabs__content) {
      flex: 1;
      overflow-y: auto;
    }
  }

  .form-actions {
    padding: 16px;
    border-top: 1px solid #dcdfe6;
    text-align: right;
    background: white;

    .el-button {
      margin-left: 8px;
    }
  }
}
</style>
