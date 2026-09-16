<template>
  <div class="table-card">
    <!-- 搜索筛选区域 -->
    <div class="table-toolbar">
      <div class="search-area">
        <el-input
          v-model="searchParams.operator"
          placeholder="搜索操作人"
          clearable
          style="width:220px"
          prefix-icon="Search"
          @keyup.enter="getLogList"
        />
        <el-select
          v-model="searchParams.module"
          placeholder="全部模块"
          clearable
          style="width:140px"
          @change="handleSearchChange"
        >
          <el-option label="全部模块" value="" />
          <el-option label="需求管理" value="需求管理" />
          <el-option label="用户管理" value="用户管理" />
          <el-option label="区域管理" value="区域管理" />
          <el-option label="系统登录" value="系统登录" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width:260px"
          @change="handleSearchChange"
        />
        <el-button type="primary" @click="getLogList">
          <el-icon><Search /></el-icon>查询
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><RefreshRight /></el-icon>重置
        </el-button>
      </div>
    </div>

    <!-- 日志表格：字段与SysOperLog实体完全对应 -->
    <el-table
      :data="tableData"
      stripe
      style="width:100%"
      table-layout="fixed"
      v-loading="loading"
    >
      <!-- 分页序号，规范写法 -->
      <el-table-column label="序号" width="70" align="center">
        <template #default="{ $index }">
          {{ searchParams.pageSize * (searchParams.pageNum - 1) + $index + 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人" width="110" />
      <el-table-column label="操作时间" width="170">
        <template #default="{ row }">
          {{ row.operateTime ? row.operateTime.replace('T',' ') : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="ip" label="IP地址" width="140" />
      <el-table-column prop="org" label="所属机构" width="120" />
      <el-table-column prop="module" label="系统模块" width="110">
        <template #default="{ row }">
          <el-tag size="small" :type="getModuleTagType(row.module)">
            {{ row.module }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="action" label="操作类型" width="100" />
      <el-table-column prop="content" label="操作内容" min-width="240" show-overflow-tooltip />
    </el-table>

    <div style="margin-top:16px;text-align:right" v-if="total > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, jumper"
        :total="total"
        :page-size="searchParams.pageSize"
        :current-page="searchParams.pageNum"
        :page-sizes="[10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, RefreshRight } from '@element-plus/icons-vue'
import { getLogPageApi } from '@/api/operLog'

// 加载状态
const loading = ref(false)
// 表格数据
const tableData = ref([])
const total = ref(0)

// 查询参数
const searchParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operator: '',
  module: '',
  startTime: '',
  endTime: ''
})
const dateRange = ref([])

// 模块标签颜色
const getModuleTagType = (moduleName) => {
  const map = {
    '用户管理': 'warning',
    '区域管理': 'success',
    '系统登录': 'info'
  }
  return map[moduleName] || ''
}

// 切换筛选条件重置页码
const handleSearchChange = () => {
  searchParams.pageNum = 1
  getLogList()
}

// 每页条数切换
const handleSizeChange = (val) => {
  searchParams.pageSize = val
  searchParams.pageNum = 1
  getLogList()
}

// 页码切换
const handlePageChange = (val) => {
  searchParams.pageNum = val
  getLogList()
}

// 重置所有查询条件
const resetSearch = () => {
  searchParams.pageNum = 1
  searchParams.operator = ''
  searchParams.module = ''
  searchParams.startTime = ''
  searchParams.endTime = ''
  dateRange.value = []
  getLogList()
}

// 请求日志分页接口
const getLogList = async () => {
  loading.value = true
  if (dateRange.value && dateRange.value.length === 2) {
    searchParams.startTime = dateRange.value[0]
    searchParams.endTime = dateRange.value[1]
  } else {
    searchParams.startTime = ''
    searchParams.endTime = ''
  }
  try {
    const res = await getLogPageApi(searchParams)
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (err) {
    ElMessage.error('操作日志查询失败，请检查接口')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 页面挂载自动加载
onMounted(() => {
  getLogList()
})
</script>

<style scoped>
.table-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}
.search-area {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.pagination-area {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>