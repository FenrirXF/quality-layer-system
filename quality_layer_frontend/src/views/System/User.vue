<template>
  <div class="card">
    <div class="page-title">用户管理</div>

    <div class="filter-bar">
      <div class="filter-left">
        <el-input v-model="queryParams.username" placeholder="用户名" clearable style="width:180px" />
        <el-select v-model="queryParams.role" placeholder="角色" clearable style="width:160px">
          <el-option label="管理员" value="admin" />
          <el-option label="区域人员" value="regional" />
        </el-select>
        <el-select v-model="queryParams.region" placeholder="区域" clearable style="width:160px">
          <el-option label="安徽" value="安徽" />
          <el-option label="芜湖" value="芜湖" />
          <el-option label="合肥" value="合肥" />
        </el-select>
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增用户
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" stripe v-loading="loading" class="user-table" table-layout="auto">
      <!-- 前端连续序号（只做展示，不传给后端！） -->
      <el-table-column label="序号" width="70">
        <template #default="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column prop="name" label="姓名" min-width="120" />
      <el-table-column prop="role" label="角色" min-width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small">
            {{ row.role === 'admin' ? '管理员' : '区域人员' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 修复：region为空展示“全部” -->
      <el-table-column label="所属区域" min-width="120">
        <template #default="{ row }">
          {{ row.region || '全部' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status === 1"
            @change="() => handleStatus(row)"
            active-text="启用"
            inactive-text="禁用"
            :disabled="row.username === 'admin'"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="180">
        <template #default="{ row }">
          {{ row.createTime ? row.createTime.replace('T',' ') : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;text-align:right">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="loadList"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="管理员" value="admin" />
            <el-option label="区域人员" value="regional" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域" prop="region">
          <el-select v-model="form.region" style="width:100%">
            <el-option label="安徽" value="安徽" />
            <el-option label="芜湖" value="芜湖" />
            <el-option label="合肥" value="合肥" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { getUserPageApi, addUserApi, updateUserApi, delUserApi, updateStatusApi } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  role: '',
  region: ''
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await getUserPageApi(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => loadList())

const resetQuery = () => {
  queryParams.username = ''
  queryParams.role = ''
  queryParams.region = ''
  queryParams.pageNum = 1
  loadList()
}

const dialogVisible = ref()
const dialogTitle = ref('')
const formRef = ref()
const form = ref({})
const isEdit = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  form.value = { role: 'regional', status: 1, region: '安徽' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateUserApi(form.value)
    ElMessage.success('修改成功')
  } else {
    await addUserApi(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户【${row.username}】吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await delUserApi(row.id)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => {})
}

const handleStatus = async (row) => {
  const status = row.status === 1 ? 0 : 1
  await updateStatusApi(row.id, status)
  ElMessage.success('状态更新成功')
  loadList()
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.filter-left {
  display: flex;
  gap: 12px;
  align-items: center;
}
:deep(.user-table .el-table__cell) {
  padding: 14px 16px;
  font-size: 15px;
}
:deep(.user-table .el-table__header th) {
  padding: 14px 16px;
  font-size: 15px;
}
.card {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-sizing: border-box;
}
</style>