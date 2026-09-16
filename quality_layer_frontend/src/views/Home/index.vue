<template>
  <div>
    <!-- 统计卡片【已修改为带左侧彩色图标布局】 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-wrap">
            <div class="stat-icon blue">
              <el-icon><CreditCard /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-num">{{ statData.total || 0 }}</div>
              <div class="stat-label">需求总数</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-wrap">
            <div class="stat-icon green">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-num" style="color:#67c23a">{{ statData.monthNew || 0 }}</div>
              <div class="stat-label">本月新增</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-wrap">
            <div class="stat-icon orange">
              <el-icon><Histogram /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-num" style="color:#e6a23c">{{ statData.testJoin || 0 }}</div>
              <div class="stat-label">测试介入</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-wrap">
            <div class="stat-icon purple">
              <el-icon><Grid /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-num" style="color:#f56c6c">{{ statData.devSelf || 0 }}</div>
              <div class="stat-label">研发自测</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <div class="card">
          <div class="page-title">各区域需求分布</div>
          <div ref="regionChartRef" style="height:320px"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="page-title">评估结论占比</div>
          <div ref="conclusionChartRef" style="height:320px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 近期需求 -->
    <div class="card" style="margin-top:16px">
      <div class="page-title">最近需求</div>
      <el-table :data="recentList" stripe>
        <el-table-column prop="id" label="序号" width="80" />
        <el-table-column prop="region" label="区域" width="120" />
        <el-table-column prop="taskName" label="项目需求/任务名称" />
        <el-table-column prop="conclusion" label="评估结论" width="160">
          <template #default="{ row }">
            <el-tag :type="row.conclusion === '测试团队介入保障' ? 'warning' : 'success'" size="small">
              {{ row.conclusion }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="calcStatusTagType(row)">
              {{ calcStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import * as echarts from 'echarts'
import { statRegionApi, statTotalApi, getRecentReqApi } from '@/api/requirement'
import { ElTableColumn } from 'element-plus'
// 新增图标导入
import { CreditCard, Clock, Histogram, Grid } from '@element-plus/icons-vue'

const regionChartRef = ref()
const conclusionChartRef = ref()
const statData = ref({
  total: 0,
  monthNew: 0,
  testJoin: 0,
  devSelf: 0
})
const recentList = ref([])

let regionChart = null
let conclusionChart = null

// 加载首页全部数据
const loadAllData = async () => {
  try {
    console.log('====开始加载首页数据====')
    // 1. 汇总统计（顶部4张卡片）
    const totalRes = await statTotalApi()
    console.log('statTotalApi 返回原始数据：', totalRes)
    const totalInfo = totalRes.data
    console.log('解析data对象：', totalInfo)

    // 单独逐个赋值，杜绝整体替换对象造成的响应式失效
    statData.value.total = totalInfo.totalAll || 0
    statData.value.monthNew = totalInfo.monthNew || 0
    statData.value.testJoin = totalInfo.testJoinTotal || 0
    statData.value.devSelf = totalInfo.devSelfTotal || 0
    console.log('✅赋值后statData：', statData.value)

    await nextTick()

    // 2. 区域柱状图
    const regionRes = await statRegionApi()
    const regionList = regionRes.data || []
    if (!regionChart) {
      regionChart = echarts.init(regionChartRef.value)
    }
    regionChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: regionList.map(item => item.regionName)
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        axisLabel: {
          formatter: '{value}'
        }
      },
      series: [{
        type: 'bar',
        data: regionList.map(item => item.count),
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] },
        barWidth: '40%'
      }]
    })

    // 3. 环形饼图
    if (!conclusionChart) {
      conclusionChart = echarts.init(conclusionChartRef.value)
    }
    conclusionChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: statData.value.testJoin, name: '测试团队介入保障', itemStyle: { color: '#e6a23c' } },
          { value: statData.value.devSelf, name: '研发自测负责', itemStyle: { color: '#67c23a' } }
        ],
        label: { formatter: '{b}: {c} ({d}%)' }
      }]
    })

    // 4. 近期需求列表
    const recentRes = await getRecentReqApi(5)
    recentList.value = recentRes.data || []
  } catch (e) {
    console.error('加载首页数据失败', e)
  }
}

// 页面挂载完成执行，移除不稳定的路由watch
onMounted(() => {
  loadAllData()
})

// 窗口大小自适应
window.addEventListener('resize', () => {
  regionChart?.resize()
  conclusionChart?.resize()
})


// 计算状态文本
const calcStatusText = (row) => {
  // 如果没有上线时间，直接使用数据库原始status
  if (!row.deadlineTime) {
    return row.status ?? '进行中'
  }
  const now = new Date()
  const deadlineTime = new Date(row.deadlineTime)
  if (now > deadlineTime) {
    return '已逾期'
  } else {
    return '进行中'
  }
}
// 计算标签颜色【和需求管理页面规则完全对齐】
const calcStatusTagType = (row) => {
  // 场景1：存在截止时间，自动判断逾期
  if (row.deadlineTime) {
    const now = new Date()
    const deadlineTime = new Date(row.deadlineTime)
    if (now > deadlineTime) {
      return 'danger' // 已逾期 → 红色
    } else {
      return 'primary' // 进行中 → 蓝色
    }
  }

  // 场景2：无截止时间，直接读取row.status，和需求管理保持一致
  if(row.status === '已完成') return 'success'
  if(row.status === '进行中') return 'primary'
  if(row.status === '已逾期') return 'danger'
  return 'primary'
}
</script>

<style scoped>
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.stat-wrap {
  display: flex;
  align-items: center;
  gap: 14px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.stat-icon.blue {
  background: #e8f3ff;
  color: #409EFF;
}
.stat-icon.green {
  background: #f0fff4;
  color: #67c23a;
}
.stat-icon.orange {
  background: #fff7e6;
  color: #e6a23c;
}
.stat-icon.purple {
  background: #f9f0ff;
  color: #9254de;
}
.stat-content {
  display: flex;
  flex-direction: column;
}
.stat-num {
  font-size: 32px;
  font-weight: 700;
  margin-top: 4px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
</style>
