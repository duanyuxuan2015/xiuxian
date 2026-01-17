<template>
  <div class="equipment-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="装备名称" prop="equipmentName">
            <el-input
              v-model="formData.equipmentName"
              placeholder="请输入装备名称"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="装备类型" prop="equipmentType">
            <el-select v-model="formData.equipmentType" style="width: 100%;">
              <el-option label="武器" value="武器" />
              <el-option label="头盔" value="头盔" />
              <el-option label="胸甲" value="胸甲" />
              <el-option label="护腿" value="护腿" />
              <el-option label="鞋子" value="鞋子" />
              <el-option label="饰品" value="饰品" />
            </el-select>
          </el-form-item>

          <el-form-item label="品质" prop="quality">
            <el-select v-model="formData.quality" style="width: 100%;">
              <el-option label="凡品" value="凡品" />
              <el-option label="良品" value="良品" />
              <el-option label="上品" value="上品" />
              <el-option label="极品" value="极品" />
              <el-option label="仙品" value="仙品" />
            </el-select>
          </el-form-item>

          <el-form-item label="基础评分" prop="baseScore">
            <el-input-number
              v-model="formData.baseScore"
              :min="0"
              :max="99999"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 战斗属性 -->
        <el-tab-pane label="战斗属性" name="combat">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="攻击力">
                <el-input-number
                  v-model="formData.attackPower"
                  :min="0"
                  :max="9999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="防御力">
                <el-input-number
                  v-model="formData.defensePower"
                  :min="0"
                  :max="9999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="生命加成">
                <el-input-number
                  v-model="formData.healthBonus"
                  :min="0"
                  :max="99999"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="暴击率">
                <el-input-number
                  v-model="formData.criticalRate"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="速度加成">
            <el-input-number
              v-model="formData.speedBonus"
              :min="0"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 3: 抗性属性 -->
        <el-tab-pane label="抗性属性" name="resistance">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="物理抗性">
                <el-input-number
                  v-model="formData.physicalResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="冰抗性">
                <el-input-number
                  v-model="formData.iceResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="火抗性">
                <el-input-number
                  v-model="formData.fireResist"
                  :min="0"
                  :max="100"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
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
        </el-tab-pane>

        <!-- Tab 4: 其他属性 -->
        <el-tab-pane label="其他属性" name="other">
          <el-form-item label="强化等级">
            <el-input-number
              v-model="formData.enhancementLevel"
              :min="0"
              :max="99"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="宝石槽位数">
            <el-input-number
              v-model="formData.gemSlotCount"
              :min="0"
              :max="10"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="特殊效果">
            <el-input
              v-model="formData.specialEffects"
              type="textarea"
              :rows="4"
              placeholder="请输入特殊效果描述"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
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
import { ref, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useEquipmentStore } from '@/stores/equipment';

interface Props {
  equipmentId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const equipmentStore = useEquipmentStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);

// 表单数据默认值
const getDefaultFormData = () => ({
  equipmentName: '',
  equipmentType: '武器',
  quality: '凡品',
  baseScore: 0,
  attackPower: 0,
  defensePower: 0,
  healthBonus: 0,
  criticalRate: 0,
  speedBonus: 0,
  physicalResist: 0,
  iceResist: 0,
  fireResist: 0,
  lightningResist: 0,
  enhancementLevel: 0,
  gemSlotCount: 0,
  specialEffects: ''
});

const formData = ref(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  equipmentName: [
    { required: true, message: '请输入装备名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在2-50个字符之间', trigger: 'blur' }
  ],
  equipmentType: [
    { required: true, message: '请选择装备类型', trigger: 'change' }
  ],
  quality: [
    { required: true, message: '请选择品质', trigger: 'change' }
  ],
  baseScore: [
    { required: true, message: '请输入基础评分', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await equipmentStore.createEquipment(formData.value);
      ElMessage.success('创建成功');
    } else {
      await equipmentStore.updateEquipment(props.equipmentId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载装备详情到表单
const loadEquipmentDetail = async (equipmentId: number) => {
  const detail = await equipmentStore.fetchDetail(equipmentId);
  formData.value = {
    equipmentName: detail.equipmentName,
    equipmentType: detail.equipmentType,
    quality: detail.quality,
    baseScore: detail.baseScore,
    attackPower: detail.attackPower || 0,
    defensePower: detail.defensePower || 0,
    healthBonus: detail.healthBonus || 0,
    criticalRate: detail.criticalRate || 0,
    speedBonus: detail.speedBonus || 0,
    physicalResist: detail.physicalResist || 0,
    iceResist: detail.iceResist || 0,
    fireResist: detail.fireResist || 0,
    lightningResist: detail.lightningResist || 0,
    enhancementLevel: detail.enhancementLevel || 0,
    gemSlotCount: detail.gemSlotCount || 0,
    specialEffects: detail.specialEffects || ''
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 equipmentId 变化，重新加载数据
watch(() => props.equipmentId, async (newEquipmentId) => {
  if (newEquipmentId) {
    await loadEquipmentDetail(newEquipmentId);
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
.equipment-form {
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
