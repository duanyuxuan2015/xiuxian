<template>
  <div class="sect-task-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="所属宗门" prop="sectId">
            <el-select
              v-model="formData.sectId"
              placeholder="请选择宗门"
              style="width: 100%;"
            >
              <el-option
                v-for="sect in sects"
                :key="sect.sectId"
                :label="sect.sectName"
                :value="sect.sectId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="任务类型" prop="taskType">
            <el-select
              v-model="formData.taskType"
              placeholder="请选择任务类型"
              style="width: 100%;"
              filterable
              allow-create
            >
              <el-option
                v-for="type in taskTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="任务名称" prop="taskName">
            <el-input
              v-model="formData.taskName"
              placeholder="请输入任务名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入任务描述"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="是否启用" prop="isActive">
            <el-switch v-model="formData.isActive" />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 任务目标 -->
        <el-tab-pane label="任务目标" name="target">
          <el-form-item label="目标类型" prop="targetType">
            <el-select
              v-model="formData.targetType"
              placeholder="请选择目标类型"
              style="width: 100%;"
              filterable
              allow-create
            >
              <el-option
                v-for="type in targetTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="目标值" prop="targetValue">
            <el-input
              v-model="formData.targetValue"
              placeholder="请输入目标值"
              maxlength="100"
              show-word-limit
            />
            <span style="margin-left: 8px; color: #909399;">目标的具体值（如怪物ID、材料ID等）</span>
          </el-form-item>

          <el-form-item label="目标数量" prop="targetCount">
            <el-input-number
              v-model="formData.targetCount"
              :min="1"
              :max="99999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="要求职位" prop="requiredPosition">
            <el-input-number
              v-model="formData.requiredPosition"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 3: 奖励设置 -->
        <el-tab-pane label="奖励设置" name="rewards">
          <el-form-item label="贡献奖励" prop="contributionReward">
            <el-input-number
              v-model="formData.contributionReward"
              :min="0"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">完成任务获得的贡献点</span>
          </el-form-item>

          <el-form-item label="声望奖励" prop="reputationReward">
            <el-input-number
              v-model="formData.reputationReward"
              :min="0"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">完成任务获得的声望</span>
          </el-form-item>

          <el-form-item label="每日上限" prop="dailyLimit">
            <el-input-number
              v-model="formData.dailyLimit"
              :min="0"
              :max="999"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">每日可完成次数限制，0为无限制</span>
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
import { useSectTaskStore } from '@/stores/sectTask';
import sectTaskApi from '@/api/sectTask';
import type { SectTaskTemplate, SectOption } from '@/types/sectTask';

interface Props {
  templateId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const sectTaskStore = useSectTaskStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const sects = ref<SectOption[]>([]);
const taskTypes = ref<string[]>([]);
const targetTypes = ref<string[]>([]);

// 表单数据默认值
const getDefaultFormData = (): SectTaskTemplate => ({
  sectId: 0,
  taskType: '日常任务',
  taskName: '',
  description: '',
  targetType: '击杀',
  targetValue: '',
  targetCount: 1,
  requiredPosition: 1,
  contributionReward: 10,
  reputationReward: 0,
  dailyLimit: 10,
  isActive: true
});

const formData = ref<SectTaskTemplate>(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  sectId: [
    { required: true, message: '请选择宗门', trigger: 'change' }
  ],
  taskType: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  targetType: [
    { required: true, message: '请选择目标类型', trigger: 'change' }
  ],
  targetValue: [
    { required: true, message: '请输入目标值', trigger: 'blur' }
  ],
  targetCount: [
    { required: true, message: '请输入目标数量', trigger: 'blur' }
  ],
  requiredPosition: [
    { required: true, message: '请输入要求职位', trigger: 'blur' }
  ],
  contributionReward: [
    { required: true, message: '请输入贡献奖励', trigger: 'blur' }
  ],
  reputationReward: [
    { required: true, message: '请输入声望奖励', trigger: 'blur' }
  ],
  dailyLimit: [
    { required: true, message: '请输入每日上限', trigger: 'blur' }
  ],
  isActive: [
    { required: true, message: '请选择是否启用', trigger: 'change' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await sectTaskStore.createTemplate(formData.value);
      ElMessage.success('创建成功');
    } else {
      await sectTaskStore.updateTemplate(props.templateId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载宗门列表
const loadSects = async () => {
  try {
    const response = await sectTaskApi.getSects();
    sects.value = response.data;
  } catch (error) {
    console.error('加载宗门列表失败:', error);
  }
};

// 加载任务类型列表
const loadTaskTypes = async () => {
  try {
    const response = await sectTaskApi.getTaskTypes();
    taskTypes.value = response.data;
  } catch (error) {
    console.error('加载任务类型失败:', error);
  }
};

// 加载目标类型列表
const loadTargetTypes = async () => {
  try {
    const response = await sectTaskApi.getTargetTypes();
    targetTypes.value = response.data;
  } catch (error) {
    console.error('加载目标类型失败:', error);
  }
};

onMounted(() => {
  loadSects();
  loadTaskTypes();
  loadTargetTypes();
});

// 加载任务详情到表单
const loadTemplateDetail = async (templateId: number) => {
  const detail = await sectTaskStore.fetchDetail(templateId);
  formData.value = {
    sectId: detail.sectId,
    taskType: detail.taskType,
    taskName: detail.taskName,
    description: detail.description || '',
    targetType: detail.targetType,
    targetValue: detail.targetValue,
    targetCount: detail.targetCount,
    requiredPosition: detail.requiredPosition,
    contributionReward: detail.contributionReward,
    reputationReward: detail.reputationReward,
    dailyLimit: detail.dailyLimit,
    isActive: detail.isActive
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 templateId 变化，重新加载数据
watch(() => props.templateId, async (newTemplateId) => {
  if (newTemplateId) {
    await loadTemplateDetail(newTemplateId);
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
.sect-task-form {
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
