<template>
  <div class="card">
    <div class="page-title">需求管理</div>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-input
        v-model="queryParams.taskName"
        placeholder="搜索项目需求/任务名称"
        clearable
        style="width:240px"
      />
      <el-select
        v-model="queryParams.region"
        placeholder="全部区域"
        clearable
        style="width:160px"
      >
        <el-option
          v-for="item in regionOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
      <el-select
        v-model="queryParams.conclusion"
        placeholder="全部结论"
        clearable
        style="width:180px"
      >
        <el-option label="测试团队介入保障" value="测试团队介入保障" />
        <el-option label="研发自测负责" value="研发自测负责" />
      </el-select>
      <el-select
      v-model="queryParams.status"
      placeholder="全部状态"
      clearable
      style="width:160px"
      >
      <el-option label="全部状态" value="" />
      <el-option label="进行中" value="进行中" />
      <el-option label="已完成" value="已完成" />
      <el-option label="已逾期" value="已逾期" />
      </el-select>
      <el-button type="primary" @click="loadList">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增需求
      </el-button>
      <el-button type="success" @click="handleOpenBatchDialog">
        <el-icon><Upload /></el-icon> 批量导入
      </el-button>
      <el-button type="warning" @click="handleExport">
        <el-icon><Download /></el-icon> 导出Excel
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" stripe v-loading="loading">
      <!-- 修改序号列：分页连续自增1,2,3... -->
      <el-table-column label="序号" width="70" align="center">
        <template #default="{ $index }">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="region" label="区域" width="100" />
      <el-table-column prop="techManager" label="技术经理" width="100" />
      <el-table-column prop="devManager" label="研发负责人" width="110" />
      <el-table-column prop="devLeader" label="研发组长" width="110" />
      <el-table-column prop="testLeader" label="测试组长" width="110" />
      <!-- 项目需求/任务名称：蓝色可点击跳转详情 -->
      <el-table-column prop="taskName" label="项目需求/任务名称" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">
          <span style="color:#409EFF;cursor:pointer;" @click="goDetail(row.id)">
            {{ row.taskName }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="triggerItem" label="触发评估项" width="180" show-overflow-tooltip />
      <el-table-column prop="conclusion" label="评估结论" width="140">
        <template #default="{ row }">
          <el-tag size="small" :type="getTagType(row.conclusion)">
            {{ row.conclusion }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deadline" label="上线时间" width="170" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="getTagType(row.status)">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-area">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadList"
        @current-change="loadList"
        :total-text="'共 ' + total + ' 条'"
        :page-size-text="'{size} 条/页'"
        :jumper-text="{
          goto: '前往',
          page: '页'
        }"
      />
    </div>
  </div>

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="760px">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="区域" prop="region">
            <el-select v-model="form.region" placeholder="请选择区域" style="width:100%">
              <el-option
                v-for="item in regionOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="技术经理" prop="techManager">
            <el-input v-model="form.techManager" placeholder="请选择技术经理"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="研发负责人" prop="devManager">
            <el-input v-model="form.devManager" placeholder="请选择研发负责人"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="研发组长" prop="devLeader">
            <el-input v-model="form.devLeader" placeholder="请选择研发组长"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="测试组长" prop="testLeader">
            <el-input v-model="form.testLeader" placeholder="请选择测试组长"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="项目需求/任务名称" prop="taskName">
            <el-input v-model="form.taskName" placeholder="请输入项目需求或任务名称，多个需求可换行填写" type="textarea" :rows="2" maxlength="200" show-word-limit></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 三维触发条件评估 -->
      <div style="background:#407899;color:#fff;padding:10px 14px;border-radius:6px;margin-bottom:12px;">
        <h4 style="margin:0 0 4px 0;">三维触发条件评估</h4>
        <p style="margin:0;font-size:13px;">逐条核查 12 项触发条件 — <span style="color:#ffd43b">命中任意1条 → 测试介入</span> | <span style="color:#95ee7c">全部未命中 → 研发自测</span></p>
      </div>

      <!-- 唯一一组el-checkbox-group -->
      <el-checkbox-group v-model="triggerList">
        <!-- 业务利害性 -->
        <div style="margin-bottom:14px;">
          <div style="font-weight:600;margin-bottom:8px;">● 业务利害性（5条）</div>
          <div style="display:grid;grid-template-columns: 1fr 1fr;gap:8px;">
            <el-checkbox value="B-1 承载高厉害场景（招生报名/考试评价/资金审批）">B-1 承载高厉害场景（招生报名/考试评价/资金审批）</el-checkbox>
            <el-checkbox value="B-2 业务不可中断（7×24可用，宕机影响核心业务）">B-2 业务不可中断（7×24可用，宕机影响核心业务）</el-checkbox>
            <el-checkbox value="B-3 涉及资金交易（支付/结算/计费）">B-3 涉及资金交易（支付/结算/计费）</el-checkbox>
            <el-checkbox value="B-4 有违约赔偿条款（质量问题触发赔偿/罚款/扣分）">B-4 有违约赔偿条款（质量问题触发赔偿/罚款/扣分）</el-checkbox>
            <el-checkbox value="B-5 用户规模大（终端用户 ≥ 10000 人且存在并发高峰）">B-5 用户规模大（终端用户 ≥ 10000 人且存在并发高峰）</el-checkbox>
          </div>
        </div>

        <!-- 数据安全性 -->
        <div style="margin-bottom:14px;">
          <div style="font-weight:600;margin-bottom:8px;">● 数据安全性（3条）</div>
          <div style="display:grid;grid-template-columns: 1fr 1fr;gap:8px;">
            <el-checkbox value="D-1 涉及敏感个人信息（金融账户/医疗/行踪/未成年人）">D-1 涉及敏感个人信息（金融账户/医疗/行踪/未成年人）</el-checkbox>
            <el-checkbox value="D-2 涉及核心业务数据（成绩/学籍/资金流水/招生录取）">D-2 涉及核心业务数据（成绩/学籍/资金流水/招生录取）</el-checkbox>
            <el-checkbox value="D-3 存在数据不可逆操作（批量删除/迁移/覆盖，无回滚机制）">D-3 存在数据不可逆操作（批量删除/迁移/覆盖，无回滚机制）</el-checkbox>
          </div>
        </div>

        <!-- 客户重要性 -->
        <div style="margin-bottom:14px;">
          <div style="font-weight:600;margin-bottom:8px;">● 客户重要性（4条）</div>
          <div style="display:grid;grid-template-columns: 1fr 1fr;gap:8px;">
            <el-checkbox value="C-1 省部级及以上客户">C-1 省部级及以上客户</el-checkbox>
            <el-checkbox value="C-2 战略标杆客户（公司指定/行业推广案例）">C-2 战略标杆客户（公司指定/行业推广案例）</el-checkbox>
            <el-checkbox value="C-3 存在舆情放大风险（公开招标/通报/评审机制）">C-3 存在舆情放大风险（公开招标/通报/评审机制）</el-checkbox>
            <el-checkbox value="C-4 历史合作有过投诉（满意度低于合格线）">C-4 历史合作有过投诉（满意度低于合格线）</el-checkbox>
          </div>
        </div>
      </el-checkbox-group>

      <!-- 命中计数+评估结论 -->
      <div style="display:flex;justify-content:space-between;align-items:center;padding-top:12px;border-top:1px solid #ddd;margin-bottom:16px;">
        <span>命中：{{ triggerList.length }} / 12</span>
        <el-tag size="small" :type="getTagTypeByCount(triggerList.length)">
          {{ form.conclusion }}
        </el-tag>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="上线时间" prop="deadline">
            <el-date-picker
              v-model="form.deadline"
              type="date"
              placeholder="请选择计划上线日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width:100%"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" style="width:100%">
              <el-option label="已完成" value="已完成" />
              <el-option label="进行中" value="进行中" />
              <el-option label="已逾期" value="已逾期" />
              <el-option label="全部状态" value="" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注">
        <el-input v-model="form.remark" placeholder="选填" type="textarea" :rows="3" maxlength="200"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>

  <!-- ========== 新增：批量导入弹窗 ========== -->
  <el-dialog v-model="batchDialogVisible" title="批量导入需求" width="680px">
    <div style="background:#f5f7fa;padding:14px;border-radius:6px;margin-bottom:16px;">
      <el-icon><InfoFilled /></el-icon>
      <span style="font-weight:600;">操作说明</span>
      <p style="margin:6px 0 0 0;font-size:13px;color:#555;">
        1.点击下方按钮下载导入模板<br>
        2.按模板格式填写需求数据（支持.xlsx格式）<br>
        3.上传填写好的文件，系统自动解析并导入
      </p>
    </div>

    <div style="margin-bottom:12px;">
      <h4 style="margin:0 0 8px 0;">第一步：下载模板</h4>
      <el-button type="primary" link @click="handleDownloadTemplate">
        <el-icon><Download /></el-icon>下载导入模板
      </el-button>
      <span style="color:#888;font-size:13px;">需求导入模板.xlsx</span>
    </div>

    <div>
      <h4 style="margin:0 0 8px 0;">第二步：上传文件</h4>
      <el-upload
        v-model:file-list="batchFileList"
        drag
        :auto-upload="false"
        accept=".xlsx,.xls"
        :limit="1"
      >
        <el-icon size="40"><UploadFilled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 .xlsx / xls 格式文件，单次最多导入100条</div>
        </template>
      </el-upload>
    </div>

    <template #footer>
      <el-button @click="batchDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleBatchConfirm">确认导入</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { Plus, Upload, Download, InfoFilled, UploadFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { getReqPageApi, addReqApi, updateReqApi, delReqApi, exportReqApi, batchAddReqApi, getEnableRegionApi } from '@/api/requirement'
import { ElMessage, ElMessageBox } from 'element-plus'
import { el } from 'element-plus/es/locales.mjs'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 区域下拉选项
const regionOptions = ref([])
// 加载所有启用区域
const loadRegionOptions = async () => {
  const res = await getEnableRegionApi()
  if (res.code === 200) {
    regionOptions.value = res.data.map(item => ({ label: item.name, value: item.name }))
  }
}


// 获取当前时间
const getNowDate = () => {
  const date = new Date()
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}:${s}`
}

onMounted(() => {
  loadRegionOptions()
  if (userStore.user?.role === 'regional') {
    queryParams.region = userStore.user.region
  }
  loadList()
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await getReqPageApi(queryParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (err) {
    console.error('加载需求列表失败', err)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.region = userStore.user?.role === 'regional' ? userStore.user.region : ''
  queryParams.taskName = ''
  queryParams.conclusion = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  loadList()
}

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref({})
const triggerList = ref([])
const isEdit = ref(false)

const rules = {
  region: [{ required: true, message: '请选择区域', trigger: 'change' }],
  taskName: [{ required: true, message: '请输入需求名称', trigger: 'blur' }]
}

watch(triggerList, (newVal) => {
  form.value.conclusion = newVal.length >= 1 ? '测试团队介入保障' : '研发自测负责'
}, { deep: true })

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增需求'
  form.value = {
    region: userStore.user?.role === 'regional' ? userStore.user.region : '',
    status: '待评估',
    conclusion: '研发自测负责',
    deadline: '',
    triggerItem: '',
    remark: '',
    updateTime: ''
  }
  triggerList.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑需求'
  form.value = { ...row }
  triggerList.value = row.triggerItem ? row.triggerItem.split('、') : []
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  form.value.triggerItem = triggerList.value.join('、')
  form.value.updateTime = getNowDate()
  try {
    if (isEdit.value) {
      await updateReqApi(form.value)
      ElMessage.success('修改成功')
    } else {
      await addReqApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (err) {
    console.error('提交失败', err)
    ElMessage.error('提交失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除需求【${row.taskName}】吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await delReqApi(row.id)
      ElMessage.success('删除成功')
      loadList()
    })
}

const getTagType = (val) => {
  // 评估结论
  if (val === '测试团队介入保障') return 'warning'
  if (val === '研发自测负责') return 'success'
  // 状态字段
  if (val === '进行中') return 'primary'    // 蓝色
  if (val === '已完成') return 'success'    // 绿色
  if (val === '已逾期') return 'danger'     // 红色
  return 'info'
}

const getTagTypeByCount = (count) => {
  return count >= 1 ? 'warning' : 'success'
}

// 导出Excel
const handleExport = async () => {
  try {
    const res = await exportReqApi({
      region: queryParams.region,
      taskName: queryParams.taskName,
      conclusion: queryParams.conclusion
    })
    const blob = new Blob([res.data])
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = '需求列表.xlsx'
    document.body.appendChild(a)
    a.click()
    URL.revokeObjectURL(a.href)
    document.body.removeChild(a)
  } catch (err) {
    ElMessage.error('导出失败')
    console.error('导出异常详情：', err)
  }
}

// 跳转详情页面
const goDetail = (id) => {
  router.push({
    path: '/requirement/detail',
    query: { id }
  })
}

// ========== 批量导入相关逻辑 ==========
const batchDialogVisible = ref(false)
const batchFileList = ref([])

// 打开导入弹窗
const handleOpenBatchDialog = () => {
  batchFileList.value = []
  batchDialogVisible.value = true
}

// 下载导入模板
const handleDownloadTemplate = async () => {
  try {
    const res = await exportReqApi({
      isTemplate: true
    })
    const blob = new Blob([res.data])
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = '需求导入模板.xlsx'
    document.body.appendChild(a)
    a.click()
    URL.revokeObjectURL(a.href)
    document.body.removeChild(a)
  } catch (err) {
    ElMessage.error('模板下载失败')
    console.error(err)
  }
}
// 确认导入
const handleBatchConfirm = async () => {
  if (!batchFileList.value.length) {
    ElMessage.warning('请先选择上传文件')
    return
  }
  const formData = new FormData()
  formData.append('file', batchFileList.value[0].raw)
  try {
    await batchAddReqApi(formData)
    ElMessage.success('导入成功！')
    batchDialogVisible.value = false
    loadList()
  } catch (err) {
    ElMessage.error('导入失败')
    console.error(err)
  }
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  region: '',
  taskName: '',
  conclusion: '',
  status: ''
})


</script>

<style scoped>
.card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
.filter-bar,
.action-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.pagination-area {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
