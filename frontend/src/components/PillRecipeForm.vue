<template>
  <div class="pill-recipe-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="丹方名称" prop="recipeName">
            <el-input
              v-model="formData.recipeName"
              placeholder="请输入丹方名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="产出丹药" prop="pillId">
            <el-select
              v-model="formData.pillId"
              placeholder="请选择产出丹药"
              style="width: 100%;"
              filterable
            >
              <el-option
                v-for="pill in pills"
                :key="pill.pillId"
                :label="pill.pillName"
                :value="pill.pillId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="产出数量" prop="outputQuantity">
            <el-input-number
              v-model="formData.outputQuantity"
              :min="1"
              :max="99"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="基础成功率" prop="baseSuccessRate">
            <el-input-number
              v-model="formData.baseSuccessRate"
              :min="1"
              :max="100"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">%</span>
          </el-form-item>

          <el-form-item label="炼丹等级要求" prop="alchemyLevelRequired">
            <el-input-number
              v-model="formData.alchemyLevelRequired"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 消耗配置 -->
        <el-tab-pane label="消耗配置" name="cost">
          <el-form-item label="灵力消耗" prop="spiritualCost">
            <el-input-number
              v-model="formData.spiritualCost"
              :min="0"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="体力消耗" prop="staminaCost">
            <el-input-number
              v-model="formData.staminaCost"
              :min="0"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="炼制时长" prop="duration">
            <el-input-number
              v-model="formData.duration"
              :min="1"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">秒</span>
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 3: 丹方材料 -->
        <el-tab-pane label="丹方材料" name="materials">
          <div class="materials-section">
            <div class="materials-header">
              <span>所需材料</span>
              <el-button type="primary" size="small" :icon="Plus" @click="handleAddMaterial">添加材料</el-button>
            </div>

            <div class="materials-list">
              <div
                v-for="(material, index) in formData.materials"
                :key="index"
                class="material-item"
              >
                <el-form-item
                  :label="'材料 ' + (index + 1)"
                  :prop="'materials.' + index + '.materialId'"
                  :rules="{ required: true, message: '请选择材料', trigger: 'change' }"
                >
                  <el-select
                    v-model="material.materialId"
                    placeholder="请选择材料"
                    filterable
                    style="width: 200px; margin-right: 8px;"
                  >
                    <el-option
                      v-for="mat in materials"
                      :key="mat.materialId"
                      :label="mat.materialName"
                      :value="mat.materialId"
                    />
                  </el-select>

                  <el-input-number
                    v-model="material.quantityRequired"
                    :min="1"
                    :max="999"
                    placeholder="数量"
                    style="width: 120px; margin-right: 8px;"
                  />

                  <el-select
                    v-model="material.isMainMaterial"
                    placeholder="类型"
                    style="width: 100px; margin-right: 8px;"
                  >
                    <el-option label="主材" :value="1" />
                    <el-option label="辅材" :value="0" />
                  </el-select>

                  <el-button type="danger" size="small" :icon="Delete" @click="handleDeleteMaterial(index)" />
                </el-form-item>
              </div>

              <el-empty v-if="!formData.materials || formData.materials.length === 0" description="暂无材料，请添加材料" :image-size="80" />
            </div>
          </div>
        </el-tab-pane>

        <!-- Tab 4: 其他信息 -->
        <el-tab-pane label="其他信息" name="other">
          <el-form-item label="丹方品阶" prop="recipeTier">
            <el-input-number
              v-model="formData.recipeTier"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="解锁方式">
            <el-input
              v-model="formData.unlockMethod"
              placeholder="请输入解锁方式"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="解锁花费">
            <el-input-number
              v-model="formData.unlockCost"
              :min="0"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="6"
              placeholder="请输入丹方描述"
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
import { Plus, Delete } from '@element-plus/icons-vue';
import { usePillRecipeStore } from '@/stores/pillRecipe';
import pillRecipeApi from '@/api/pillRecipe';
import type { PillRecipe, PillOption, MaterialOption } from '@/types/pillRecipe';

interface Props {
  recipeId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const pillRecipeStore = usePillRecipeStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const pills = ref<PillOption[]>([]);
const materials = ref<MaterialOption[]>([]);

// 表单数据默认值
const getDefaultFormData = (): PillRecipe => ({
  recipeName: '',
  pillId: 0,
  outputQuantity: 1,
  baseSuccessRate: 80,
  alchemyLevelRequired: 1,
  spiritualCost: 100,
  staminaCost: 50,
  duration: 60,
  recipeTier: 1,
  unlockMethod: '',
  unlockCost: 0,
  description: '',
  materials: []
});

const formData = ref<PillRecipe>(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  recipeName: [
    { required: true, message: '请输入丹方名称', trigger: 'blur' },
    { min: 2, max: 100, message: '名称长度在2-100个字符之间', trigger: 'blur' }
  ],
  pillId: [
    { required: true, message: '请选择产出丹药', trigger: 'change' }
  ],
  outputQuantity: [
    { required: true, message: '请输入产出数量', trigger: 'blur' }
  ],
  baseSuccessRate: [
    { required: true, message: '请输入基础成功率', trigger: 'blur' }
  ],
  alchemyLevelRequired: [
    { required: true, message: '请输入炼丹等级要求', trigger: 'blur' }
  ],
  spiritualCost: [
    { required: true, message: '请输入灵力消耗', trigger: 'blur' }
  ],
  staminaCost: [
    { required: true, message: '请输入体力消耗', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入炼制时长', trigger: 'blur' }
  ],
  recipeTier: [
    { required: true, message: '请输入丹方品阶', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await pillRecipeStore.createRecipe(formData.value);
      ElMessage.success('创建成功');
    } else {
      await pillRecipeStore.updateRecipe(props.recipeId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载丹药列表
const loadPills = async () => {
  try {
    const response = await pillRecipeApi.getPills();
    pills.value = response.data;
  } catch (error) {
    console.error('加载丹药列表失败:', error);
  }
};

// 加载材料列表
const loadMaterials = async () => {
  try {
    const response = await pillRecipeApi.getMaterials();
    materials.value = response.data;
  } catch (error) {
    console.error('加载材料列表失败:', error);
  }
};

onMounted(() => {
  loadPills();
  loadMaterials();
});

// 添加材料
const handleAddMaterial = () => {
  if (!formData.value.materials) {
    formData.value.materials = [];
  }
  formData.value.materials.push({
    materialId: 0,
    quantityRequired: 1,
    isMainMaterial: 0
  });
};

// 删除材料
const handleDeleteMaterial = (index: number) => {
  formData.value.materials?.splice(index, 1);
};

// 加载丹方详情到表单
const loadRecipeDetail = async (recipeId: number) => {
  const detail = await pillRecipeStore.fetchDetail(recipeId);
  formData.value = {
    recipeName: detail.recipeName,
    pillId: detail.pillId,
    outputQuantity: detail.outputQuantity,
    baseSuccessRate: detail.baseSuccessRate,
    alchemyLevelRequired: detail.alchemyLevelRequired,
    spiritualCost: detail.spiritualCost,
    staminaCost: detail.staminaCost,
    duration: detail.duration,
    recipeTier: detail.recipeTier,
    unlockMethod: detail.unlockMethod || '',
    unlockCost: detail.unlockCost || 0,
    description: detail.description || '',
    materials: detail.materials || []
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 recipeId 变化，重新加载数据
watch(() => props.recipeId, async (newRecipeId) => {
  if (newRecipeId) {
    await loadRecipeDetail(newRecipeId);
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
.pill-recipe-form {
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

  .materials-section {
    .materials-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #dcdfe6;

      span {
        font-size: 16px;
        font-weight: 500;
      }
    }

    .materials-list {
      .material-item {
        background: #f5f7fa;
        padding: 12px;
        border-radius: 4px;
        margin-bottom: 8px;

        .el-form-item {
          margin-bottom: 0;
        }
      }
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
