<template>
  <div class="sect-shop-form">
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

          <el-form-item label="物品类型" prop="itemType">
            <el-select
              v-model="formData.itemType"
              placeholder="请选择物品类型"
              style="width: 100%;"
              filterable
              allow-create
              @change="handleItemTypeChange"
            >
              <el-option
                v-for="type in itemTypes"
                :key="type"
                :label="type"
                :value="type"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="关联物品" prop="refItemId">
            <!-- 如果有物品列表，显示下拉选择 -->
            <el-select
              v-if="items.length > 0"
              v-model="formData.refItemId"
              placeholder="请选择关联物品"
              style="width: 100%;"
              filterable
              :loading="loadingItems"
              :disabled="!formData.itemType"
              @change="handleItemChange"
            >
              <el-option
                v-for="item in items"
                :key="item.itemId"
                :label="item.itemName"
                :value="item.itemId"
              />
            </el-select>
            <!-- 如果没有物品列表，显示输入框让用户手动输入 -->
            <el-input-number
              v-else
              v-model="formData.refItemId"
              placeholder="请手动输入物品ID"
              :min="1"
              style="width: 100%;"
              :disabled="!formData.itemType"
            />
            <span v-if="items.length === 0" style="margin-left: 8px; color: #909399;">
              该类型暂无预设物品，请手动输入ID
            </span>
          </el-form-item>

          <el-form-item label="物品名称" prop="itemName">
            <el-input
              v-model="formData.itemName"
              placeholder="请输入物品名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="物品品阶" prop="itemTier">
            <el-input-number
              v-model="formData.itemTier"
              :min="1"
              :max="999"
              style="width: 100%;"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- Tab 2: 价格库存 -->
        <el-tab-pane label="价格库存" name="price">
          <el-form-item label="价格" prop="price">
            <el-input-number
              v-model="formData.price"
              :min="0"
              style="width: 100%;"
            />
            <span style="margin-left: 8px; color: #909399;">贡献点</span>
          </el-form-item>

          <el-form-item label="库存上限" prop="stockLimit">
            <el-input-number
              v-model="formData.stockLimit"
              :min="1"
              :max="99999"
              style="width: 100%;"
            />
          </el-form-item>

          <el-form-item label="当前库存" prop="currentStock">
            <el-input-number
              v-model="formData.currentStock"
              :min="0"
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

        <!-- Tab 3: 其他信息 -->
        <el-tab-pane label="其他信息" name="other">
          <el-form-item label="描述">
            <el-input
              v-model="formData.description"
              type="textarea"
              :rows="6"
              placeholder="请输入物品描述"
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
import { useSectShopStore } from '@/stores/sectShop';
import sectShopApi from '@/api/sectShop';
import type { SectShopItem, SectOption } from '@/types/sectShop';

interface Props {
  itemId: number | null;
  isCreating: boolean;
}

interface Emits {
  (e: 'save'): void;
  (e: 'cancel'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const sectShopStore = useSectShopStore();

const formRef = ref();
const activeTab = ref('basic');
const saving = ref(false);
const sects = ref<SectOption[]>([]);
const itemTypes = ref<string[]>([]);
const items = ref<Array<{ itemId: number; itemName: string }>>([]);
const loadingItems = ref(false);

// 表单数据默认值
const getDefaultFormData = (): SectShopItem => ({
  sectId: 0,
  itemType: '装备',
  refItemId: 0,
  itemName: '',
  itemTier: 1,
  description: '',
  price: 100,
  stockLimit: 99,
  currentStock: 99,
  requiredPosition: 1
});

const formData = ref<SectShopItem>(getDefaultFormData());

// 表单验证规则
const formRules = computed(() => ({
  sectId: [
    { required: true, message: '请选择宗门', trigger: 'change' }
  ],
  itemType: [
    { required: true, message: '请选择物品类型', trigger: 'change' }
  ],
  refItemId: [
    { required: true, message: '请选择关联物品', trigger: 'change' }
  ],
  itemName: [
    { required: true, message: '请输入物品名称', trigger: 'blur' }
  ],
  itemTier: [
    { required: true, message: '请输入物品品阶', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  stockLimit: [
    { required: true, message: '请输入库存上限', trigger: 'blur' }
  ],
  currentStock: [
    { required: true, message: '请输入当前库存', trigger: 'blur' }
  ],
  requiredPosition: [
    { required: true, message: '请输入要求职位', trigger: 'blur' }
  ]
}));

const handleSave = async () => {
  try {
    const valid = await formRef.value?.validate();
    if (!valid) return;

    saving.value = true;

    if (props.isCreating) {
      await sectShopStore.createItem(formData.value);
      ElMessage.success('创建成功');
    } else {
      await sectShopStore.updateItem(props.itemId!, formData.value);
      ElMessage.success('更新成功');
    }

    emit('save');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

// 处理物品类型变化
const handleItemTypeChange = async () => {
  // 清空已选择的物品
  formData.value.refItemId = 0;
  formData.value.itemName = '';
  items.value = [];

  if (!formData.value.itemType) {
    return;
  }

  // 加载对应类型的物品列表
  try {
    loadingItems.value = true;
    const response = await sectShopApi.getItemsByType(formData.value.itemType);
    items.value = response.data;
  } catch (error) {
    console.error('加载物品列表失败:', error);
    ElMessage.error('加载物品列表失败');
  } finally {
    loadingItems.value = false;
  }
};

// 处理物品选择变化
const handleItemChange = (itemId: number) => {
  const selectedItem = items.value.find(item => item.itemId === itemId);
  if (selectedItem) {
    formData.value.itemName = selectedItem.itemName;
  }
};

// 加载宗门列表
const loadSects = async () => {
  try {
    const response = await sectShopApi.getSects();
    sects.value = response.data;
  } catch (error) {
    console.error('加载宗门列表失败:', error);
  }
};

// 加载物品类型列表
const loadItemTypes = async () => {
  try {
    const response = await sectShopApi.getItemTypes();
    itemTypes.value = response.data;
  } catch (error) {
    console.error('加载物品类型失败:', error);
  }
};

onMounted(() => {
  loadSects();
  loadItemTypes();
});

// 加载物品详情到表单
const loadItemDetail = async (itemId: number) => {
  const detail = await sectShopStore.fetchDetail(itemId);
  formData.value = {
    sectId: detail.sectId,
    itemType: detail.itemType,
    refItemId: detail.refItemId,
    itemName: detail.itemName,
    itemTier: detail.itemTier,
    description: detail.description || '',
    price: detail.price,
    stockLimit: detail.stockLimit,
    currentStock: detail.currentStock,
    requiredPosition: detail.requiredPosition
  };
  // 重置 tab 到第一个
  activeTab.value = 'basic';

  // 加载对应类型的物品列表
  if (formData.value.itemType) {
    try {
      loadingItems.value = true;
      const response = await sectShopApi.getItemsByType(formData.value.itemType);
      items.value = response.data;
    } catch (error) {
      console.error('加载物品列表失败:', error);
    } finally {
      loadingItems.value = false;
    }
  }
};

// 监听 itemId 变化，重新加载数据
watch(() => props.itemId, async (newItemId) => {
  if (newItemId) {
    await loadItemDetail(newItemId);
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
    // 清空物品列表
    items.value = [];
  }
});
</script>

<style scoped lang="scss">
.sect-shop-form {
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
