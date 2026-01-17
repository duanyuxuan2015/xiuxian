<template>
  <div class="pill-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="丹药名称" prop="pillName">
            <el-input
              v-model="formData.pillName"
              placeholder="请输入丹药名称"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="丹药等级" prop="pillTier">
            <el-input-number
              v-model="formData.pillTier"
              :min="1"
              :max="99"
              style="width: 100%;"
            />
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

          <el-form-item label="堆叠上限" prop="stackLimit">
            <el-input-number
              v-model="formData.stackLimit"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="灵石价格" prop="spiritStones">
            <el-input-number
              v-model="formData.spiritStones"
              :min="0"
              :max="999999"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 效果配置 -->
        <el-tab-pane label="效果配置" name="effect">
          <el-form-item label="效果类型" prop="effectType">
            <el-select
              v-model="formData.effectType"
              placeholder="请选择效果类型"
              style="width: 100%;"
              filterable
              allow-create
            >
              <el-option
                v-for="type in effectTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="效果值" prop="effectValue">
            <el-input-number
              v-model="formData.effectValue"
              :min="0"
              :max="9999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="持续时间">
            <el-input-number
              v-model="formData.duration"
              :min="0"
              :max="9999"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">秒（0表示瞬间生效）</span>
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 3: 其他信息 -->
        <el-tab-pane label="其他信息" name="other">
          <el-form-item label="描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="6"
              placeholder="请输入丹药描述"
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
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { usePillStore } from '@/stores/pill';
import { pillApi } from '@/api/pill';

interface Props {
  pillId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const pillStore = usePillStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const effectTypes = ref<string[]>([]);

// 表单数据默认值
const getDefaultFormData = () => ({
  pillName: '',
  pillTier: 1,
  quality: '凡品',
  effectType: '',
  effectValue: 100,
  duration: 0,
  stackLimit: 99,
  spiritStones: 100,
  description: ''
});

const formData = ref(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  pillName: [
    { required: true, message: '请输入丹药名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在2-50个字符之间', trigger: 'blur' }
  ],
  pillTier: [
    { required: true, message: '请输入丹药等级', trigger: 'blur' }
  ],
  quality: [
    { required: true, message: '请选择品质', trigger: 'change' }
  ],
  effectType: [
    { required: true, message: '请选择效果类型', trigger: 'change' }
  ],
  effectValue: [
    { required: true, message: '请输入效果值', trigger: 'blur' }
  ],
  stackLimit: [
    { required: true, message: '请输入堆叠上限', trigger: 'blur' }
  ],
  spiritStones: [
    { required: true, message: '请输入灵石价格', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await pillStore.createPill(formData.value);
      ElMessage.success('创建成功');
    } else {
      await pillStore.updatePill(props.pillId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载效果类型列表
const loadEffectTypes = async () => {
  try {
    const response = await pillApi.getEffectTypes();
    effectTypes.value = response.data;
  } catch (error) {
    console.error('加载效果类型失败:', error);
  }
};

onMounted(() => {
  loadEffectTypes();
});

// 加载丹药详情到表单
const loadPillDetail = async (pillId: number) => {
  const detail = await pillStore.fetchDetail(pillId);
  formData.value = {
    pillName: detail.pillName,
    pillTier: detail.pillTier,
    quality: detail.quality,
    effectType: detail.effectType,
    effectValue: detail.effectValue,
    duration: detail.duration || 0,
    stackLimit: detail.stackLimit,
    spiritStones: detail.spiritStones,
    description: detail.description || ''
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 pillId 变化，重新加载数据
watch(() => props.pillId, async (newPillId) => {
  if (newPillId) {
    await loadPillDetail(newPillId);
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
.pill-form {
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
