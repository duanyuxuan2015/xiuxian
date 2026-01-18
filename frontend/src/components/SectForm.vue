<template>
  <div class="sect-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-tabs v-model="activeTab">
        <!-- Tab 1: 基础信息 -->
        <el-tab-pane label="基础信息" name="basic">
          <el-form-item label="宗门名称" prop="sectName">
            <el-input
              v-model="formData.sectName"
              placeholder="请输入宗门名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="宗门类型" prop="sectType">
            <el-select
              v-model="formData.sectType"
              placeholder="请选择宗门类型"
              style="width: 100%;"
              filterable
              allow-create
            >
              <el-option
                v-for="type in sectTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="要求境界等级" prop="requiredRealmLevel">
            <el-input-number
              v-model="formData.requiredRealmLevel"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="专长">
            <el-input
              v-model="formData.specialty"
              placeholder="请输入宗门专长"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 详细配置 -->
        <el-tab-pane label="详细配置" name="detail">
          <el-form-item label="技能侧重">
            <el-input
              v-model="formData.skillFocus"
              placeholder="请输入技能侧重"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="加入要求">
            <el-input
              v-model="formData.joinRequirement"
              type="textarea"
              :rows="4"
              placeholder="请输入加入要求"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="宗门描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="6"
              placeholder="请输入宗门描述"
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
import { useSectStore } from '@/stores/sect';
import sectApi from '@/api/sect';
import type { Sect } from '@/types/sect';

interface Props {
  sectId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const sectStore = useSectStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const sectTypes = ref<string[]>([]);

// 表单数据默认值
const getDefaultFormData = (): Sect => ({
  sectName: '',
  sectType: '',
  description: '',
  specialty: '',
  requiredRealmLevel: 1,
  skillFocus: '',
  joinRequirement: ''
});

const formData = ref<Sect>(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  sectName: [
    { required: true, message: '请输入宗门名称', trigger: 'blur' },
    { min: 2, max: 100, message: '名称长度在2-100个字符之间', trigger: 'blur' }
  ],
  sectType: [
    { required: true, message: '请选择宗门类型', trigger: 'change' }
  ],
  requiredRealmLevel: [
    { required: true, message: '请输入要求境界等级', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await sectStore.createSect(formData.value);
      ElMessage.success('创建成功');
    } else {
      await sectStore.updateSect(props.sectId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 加载宗门类型列表
const loadSectTypes = async () => {
  try {
    const response = await sectApi.getSectTypes();
    sectTypes.value = response.data;
  } catch (error) {
    console.error('加载宗门类型失败:', error);
  }
};

onMounted(() => {
  loadSectTypes();
});

// 加载宗门详情到表单
const loadSectDetail = async (sectId: number) => {
  const detail = await sectStore.fetchDetail(sectId);
  formData.value = {
    sectName: detail.sectName,
    sectType: detail.sectType,
    description: detail.description || '',
    specialty: detail.specialty || '',
    requiredRealmLevel: detail.requiredRealmLevel,
    skillFocus: detail.skillFocus || '',
    joinRequirement: detail.joinRequirement || ''
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';
};

// 监听 sectId 变化，重新加载数据
watch(() => props.sectId, async (newSectId) => {
  if (newSectId) {
    await loadSectDetail(newSectId);
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
.sect-form {
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
