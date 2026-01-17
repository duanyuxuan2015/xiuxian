<template>
  <div class="material-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="材料名称" prop="materialName">
            <el-input
              v-model="formData.materialName"
              placeholder="请输入材料名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="材料品阶" prop="materialTier">
            <el-input-number
              v-model="formData.materialTier"
              :min="1"
              :max="999"
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
              :max="99999"
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

        <!-- Tab 2: 材料配置 -->
        <el-tab-pane label="材料配置" name="config">
          <el-form-item label="材料类型" prop="materialType">
            <el-select
              v-model="formData.materialType"
              placeholder="请选择材料类型"
              style="width: 100%;"
              filterable
              allow-create
            >
              <el-option
                v-for="type in materialTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 3: 其他信息 -->
        <el-tab-pane label="其他信息" name="other">
          <el-form-item label="描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="6"
              placeholder="请输入材料描述"
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
import { useMaterialStore } from '@/stores/material';
import materialApi from '@/api/material';

interface Props {
  materialId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const materialStore = useMaterialStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const materialTypes = ref<string[]>([]);

// 表单数据默认值
const getDefaultFormData = () => ({
  materialName: '',
  materialTier: 1,
  quality: '凡品',
  materialType: '',
  stackLimit: 99,
  spiritStones: 100,
  description: ''
});

const formData = ref(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  materialName: [
    { required: true, message: '请输入材料名称', trigger: 'blur' },
    { min: 2, max: 100, message: '名称长度在2-100个字符之间', trigger: 'blur' }
  ],
  materialTier: [
    { required: true, message: '请输入材料品阶', trigger: 'blur' }
  ],
  quality: [
    { required: true, message: '请选择品质', trigger: 'change' }
  ],
  materialType: [
    { required: true, message: '请选择材料类型', trigger: 'change' }
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
      await materialStore.createMaterial(formData.value as any);
      ElMessage.success('创建成功');
    } else {
      await materialStore.updateMaterial(props.materialId!, formData.value as any);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
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
});

// 加载材料详情到表单
const loadMaterialDetail = async (materialId: number) => {
  const detail = await materialStore.fetchDetail(materialId);
  formData.value = {
    materialName: detail.materialName,
    materialTier: detail.materialTier,
    quality: detail.quality,
    materialType: detail.materialType,
    stackLimit: detail.stackLimit,
    spiritStones: detail.spiritStones,
    description: detail.description || ''
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 materialId 变化，重新加载数据
watch(() => props.materialId, async (newMaterialId) => {
  if (newMaterialId) {
    await loadMaterialDetail(newMaterialId);
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
.material-form {
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
