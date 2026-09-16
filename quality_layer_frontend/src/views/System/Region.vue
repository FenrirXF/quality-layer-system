<template>
  <div class="card">
    <div class="page-title">区域管理</div>

    <div class="filter-bar">
      <div class="search-area">
        <span style="font-size:14px;color:#606266;">区域列表</span>
      </div>
      <div class="action-area">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增区域
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" stripe v-loading="loading" class="user-table">
      <!-- 已修复：分页自动序号，永远1、2、3、4... -->
      <el-table-column label="序号" width="70" align="center">
        <template #default="{ $index }">
          {{ queryParams.pageSize * (queryParams.pageNum - 1) + $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="name" label="区域名称" min-width="160" />
      <el-table-column prop="techManager" label="默认技术经理" min-width="140" />
      <el-table-column label="需求数量" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small">{{ row.reqCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联用户数" width="110" align="center">
        <template #default="{ row }">{{ row.userCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status ? 'success' : 'info'" size="small">
            {{ row.status ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">
          {{ row.createTime ? row.createTime.replace('T',' ') : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;text-align:right" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="loadList"
      />
    </div>

    <!-- 新增/编辑区域弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110">
        <el-form-item label="区域名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入区域名称" maxlength="20" />
        </el-form-item>
        <el-form-item label="默认技术经理">
          <el-input v-model="form.techManager" placeholder="选填" maxlength="20" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRegionPageApi, addRegionApi, updateRegionApi, delRegionApi } from '@/api/system'

// 加载状态
const loading = ref(false)
// 表格数据
const tableData = ref([])
const total = ref(0)
// 分页参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref({})
const isEdit = ref(false)

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入区域名称', trigger: 'blur' }]
}

// 加载区域列表
const loadList = async () => {
  loading.value = true
  try {
    const res = await getRegionPageApi(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

// 页面初始化加载
onMounted(() => loadList())

// 打开新增弹窗
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增区域'
  form.value = {
    name: '',
    techManager: '',
    status: 1
  }
  dialogVisible.value = true
}

// 打开编辑弹窗
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑区域'
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateRegionApi(form.value)
    ElMessage.success('修改区域成功')
  } else {
    await addRegionApi(form.value)
    ElMessage.success('新增区域成功')
  }
  dialogVisible.value = false
  loadList()
}

// 删除区域
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除【${row.name}】？删除后无法恢复`, '删除提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await delRegionApi(row.id)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => {})
}
</script>

<style scoped>
.page-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.search-area {
  display: flex;
  align-items: center;
  gap: 12px;
}
.action-area {
  display: flex;
  align-items: center;
  gap: 8px;
}
:deep(.user-table .el-table__cell) {
  padding: 14px 0;
  font-size: 15px;
}
:deep(.user-table .el-table__header th) {
  padding: 14px 0;
  font-size: 15px;
}
.card {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
}
</style>