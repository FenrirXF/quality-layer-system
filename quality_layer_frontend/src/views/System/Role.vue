<template>
  <div class="card">
    <div class="page-title">角色管理</div>

    <div class="filter-bar">
      <div class="filter-left">
        <el-input v-model="queryParams.name" placeholder="搜索角色名称" clearable style="width:260px" />
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增角色
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" stripe v-loading="loading" table-layout="auto">
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="name" label="角色名称" min-width="140" />
      <el-table-column prop="code" label="角色编码" min-width="140" />
      <el-table-column prop="description" label="描述" min-width="280" />
      <el-table-column prop="userCount" label="用户数" width="100" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 操作列加宽，放入两个按钮 -->
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handlePermConfig(row)">权限配置</el-button>
          <el-button type="primary" link size="small" @click="handleViewUser(row)">查看用户</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" align-center>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="选填" />
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

    <!-- 角色关联用户弹窗 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="userDialogTitle"
      width="420"
      align-center
    >
      <p>关联用户：{{ userNameList.length ? userNameList.join('、') : '暂无用户' }}</p>
      <template #footer>
        <div class="dialog-footer" style="text-align:right">
          <el-button type="primary" @click="userDialogVisible = false">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ========== 权限配置弹窗（和原型保持一致） ========== -->
    <el-dialog
      v-model="permDialogVisible"
      title="权限配置"
      width="460"
      align-center
    >
      <el-tag style="margin-bottom:12px;">{{ currentRole.name }}</el-tag>
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-expand-all="true"
        :props="{ label: 'name', children: 'children' }"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePerm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getRoleListApi, addRoleApi, updateRoleApi } from '@/api/role'
import { getUserByRoleApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 查询条件
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: ''
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await getRoleListApi(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => loadList())

// 重置搜索
const resetQuery = () => {
  queryParams.name = ''
  queryParams.pageNum = 1
  loadList()
}

// 新增编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref({})
const isEdit = ref(false)

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  form.value = {
    name: '',
    code: '',
    description: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateRoleApi(form.value)
    ElMessage.success('修改成功')
  } else {
    await addRoleApi(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadList()
}

// ========== 查看用户逻辑 ==========
const userDialogVisible = ref(false)
const userDialogTitle = ref('')
const userNameList = ref([])

const handleViewUser = async (row) => {
  try {
    const res = await getUserByRoleApi(row.code)
    console.log('接口原始返回数据：', res)
    const list = Array.isArray(res) ? res : res.data
    userNameList.value = list.map(item => item.name ?? '未知姓名')

    userDialogTitle.value = `${row.name} - 用户列表`
    userDialogVisible.value = true
  } catch (err) {
    console.error('查询角色关联用户异常', err)
    ElMessage.error('加载用户失败')
  }
}

// ========== 权限配置相关 ==========
const permDialogVisible = ref(false)
const treeRef = ref(null)
const currentRole = ref({})
// 模拟菜单树，后续替换为后端接口返回
const menuTree = ref([
  { id: 1, name: '首页' },
  { id: 2, name: '需求管理' },
  { id: 3, name: '系统管理' }
])

// 打开权限弹窗
const handlePermConfig = (row) => {
  currentRole.value = { ...row }
  permDialogVisible.value = true
  // todo：后端开发完成后在这里请求【该角色已拥有权限ID】，回显勾选
  nextTick(() => {
    // treeRef.value.setCheckedKeys(后端返回id数组)
  })
}

// 保存权限
const savePerm = async () => {
  const checkedIds = treeRef.value.getCheckedKeys()
  console.log('选中菜单id：', checkedIds)
  // todo：调用后端保存接口
  // await saveRoleMenuApi({roleId: currentRole.value.id, menuIdList: checkedIds})
  ElMessage.success('权限保存成功')
  permDialogVisible.value = false
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
:deep(.el-table .el-table__cell) {
  padding: 14px 16px;
  font-size: 15px;
}
:deep(.el-table__header th) {
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