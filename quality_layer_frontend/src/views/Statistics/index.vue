<template>
<div class="page-wrap">
  <!-- 筛选区域 -->
  <div class="card filter-card">
    <el-row :gutter="16" align="middle">
      <el-col :span="10">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          style="width:100%"
        ></el-date-picker>
      </el-col>
      <el-col :span="4">
        <el-select v-model="filterRegion" placeholder="全部区域" style="width:100%">
          <el-option
            v-for="item in regionOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-select v-model="filterConclusion" placeholder="全部结论" style="width:100%">
          <el-option label="全部结论" value=""></el-option>
          <el-option label="测试团队介入保障" value="测试团队介入保障"></el-option>
          <el-option label="研发自测负责" value="研发自测负责"></el-option>
        </el-select>
      </el-col>
      <el-col :span="5">
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </el-col>
    </el-row>
  </div>

  <!-- 切换标签 -->
  <div class="tab-wrap" style="margin-top:16px">
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="按区域" name="region"></el-tab-pane>
      <el-tab-pane label="按技术经理" name="techManager"></el-tab-pane>
      <el-tab-pane label="按研发负责人" name="devManager"></el-tab-pane>
      <el-tab-pane label="按研发组长" name="devLeader"></el-tab-pane>
      <el-tab-pane label="按测试组长" name="testLeader"></el-tab-pane>
      <el-tab-pane label="按评估结论" name="conclusion"></el-tab-pane>
    </el-tabs>
  </div>

  <!-- 顶部统计卡片【已重构为带左侧彩色图标布局】 -->
  <el-row :gutter="16" style="margin-top:16px">
    <el-col :span="6">
      <div class="stat-card">
        <div class="stat-wrap">
          <div class="stat-icon blue">
            <el-icon><CreditCard /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-num">{{ stat.total || 0 }}</div>
            <div class="stat-label">需求总数</div>
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
            <div class="stat-num">{{ stat.regionCount || 0 }}</div>
            <div class="stat-label">分组数量</div>
          </div>
        </div>
      </div>
    </el-col>
    <el-col :span="6">
      <div class="stat-card">
        <div class="stat-wrap">
          <div class="stat-icon green">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-num" style="color:#e6a23c">{{ stat.testJoin || 0 }}</div>
            <div class="stat-label">测试介入需求</div>
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
            <div class="stat-num" style="color:#f56c6c">{{ stat.devSelf || 0 }}</div>
            <div class="stat-label">研发自测需求</div>
          </div>
        </div>
      </div>
    </el-col>
  </el-row>

  <!-- 图表区域 -->
  <el-row :gutter="16" style="margin-top:16px">
    <el-col :span="12">
      <div class="card">
        <div class="page-title">区域—需求数量分布</div>
        <div ref="barChartRef" style="height:340px"></div>
      </div>
    </el-col>
    <el-col :span="12">
      <div class="card">
        <div class="page-title">区域—测试介入 vs 研发自测</div>
        <div ref="stackChartRef" style="height:340px"></div>
      </div>
    </el-col>
  </el-row>

  <!-- 明细表格 -->
  <div class="card" style="margin-top:16px">
    <div class="page-title">区域维度明细</div>
    <el-table :data="tableData" stripe border style="width:100%">
      <el-table-column prop="groupName" label="区域" width="160" />
      <el-table-column prop="total" label="需求总数" sortable width="160" />
      <el-table-column prop="testJoin" label="测试介入" sortable width="160" />
      <el-table-column prop="devSelf" label="研发自测" sortable width="160" />
      <el-table-column label="测试介入率" sortable width="240">
        <template #default="{ row }">
          <div style="display:flex;align-items:center;gap:8px;width:100%">
            <el-progress
              :percentage="Number(row.rate.replace('%',''))"
              :show-text="false"
              color="#e6a23c"
              style="flex:1"
            />
            <span>{{ row.rate }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="触发项分布" min-width="200">
        <template #default="{ row }">
          <span v-if="row.triggerShortList && row.triggerShortList.length > 0">
            <el-tag v-for="tag in row.triggerShortList" :key="tag" size="small" style="margin:0 4px">
              {{ tag }}
            </el-tag>
          </span>
          <span v-else>无</span>
        </template>
      </el-table-column>

      <!-- 底部合计行 -->
      <template #append>
        <div class="table-summary-row">
          <div class="sum-cell" style="width:160px;"><strong>合计</strong></div>
          <div class="sum-cell" style="width:160px;"><strong>{{ sumTotal }}</strong></div>
          <div class="sum-cell" style="width:160px;"><strong>{{ sumTest }}</strong></div>
          <div class="sum-cell" style="width:160px;"><strong>{{ sumDev }}</strong></div>
          <div class="sum-cell" style="width:240px;">
            <strong>{{ sumRate }}%</strong>
          </div>
          <div class="sum-cell" style="flex:1;"></div>
        </div>
      </template>
    </el-table>
  </div>
</div>
</template>

<script setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { statGroupApi } from '@/api/requirement'
import { getEnableRegionApi } from '@/api/requirement'
// 新增图标导入
import { CreditCard, Histogram, Check, Grid } from '@element-plus/icons-vue'

// 筛选条件（区间数组）
const dateRange = ref([])
const filterRegion = ref('')
const filterConclusion = ref('')
const activeTab = ref('region')
// 区域下拉选项
const regionOptions = ref([])

// 统计卡片数据
const stat = ref({
  total:0,
  regionCount:0,
  testJoin:0,
  devSelf:0
})

const tableData = ref([])

// 表格合计计算
const sumTotal = computed(() => {
  return tableData.value.reduce((s, item) => s + Number(item.total || 0), 0)
})
const sumTest = computed(() => {
  return tableData.value.reduce((s, item) => s + Number(item.testJoin || 0), 0)
})
const sumDev = computed(() => {
  return tableData.value.reduce((s, item) => s + Number(item.devSelf || 0), 0)
})
const sumRate = computed(() => {
  if(sumTotal.value === 0) return 0
  return Math.round(sumTest.value / sumTotal.value * 100)
})

let barChart = null
let stackChart = null
const barChartRef = ref()
const stackChartRef = ref()

// 加载区域下拉选项【已修改】
const loadRegionOptions = async () => {
  try {
    const res = await getEnableRegionApi()
    if(res.code === 200){
      regionOptions.value = [
        {label:'全部区域', value:''},
        ...res.data.map(item=>({label:item.name, value:item.name}))
      ]
    }
  }catch(e){
    console.error('加载区域列表失败',e)
  }
}

// 请求数据
const loadData = async () => {
  try {
    let startDate = ''
    let endDate = ''
    // 拆分区间
    if (dateRange.value && dateRange.value.length === 2) {
      startDate = dateRange.value[0]
      endDate = dateRange.value[1]
    }
    // 严格日期正则校验
    const datePattern = /^\d{4}-\d{2}-\d{2}$/
    if (startDate && !datePattern.test(startDate)) startDate = ''
    if (endDate && !datePattern.test(endDate)) endDate = ''

    const params = {
      startDate,
      endDate,
      region: filterRegion.value,
      conclusion: filterConclusion.value,
      groupType: activeTab.value
    }
    console.log('请求参数：', params)
    const res = await statGroupApi(params)
    if(res.code !== 200) return
    const dataList = res.data

    // 1.计算顶部统计卡片
    let totalCount = 0
    let testJoinCount = 0
    let devSelfCount = 0
    const groupSet = new Set()
    dataList.forEach(item=>{
      totalCount += Number(item.total || 0)
      testJoinCount += Number(item.testJoin || 0)
      devSelfCount += Number(item.devSelf || 0)
      if(item.groupName) groupSet.add(item.groupName)
    })
    stat.value.total = totalCount
    stat.value.regionCount = groupSet.size
    stat.value.testJoin = testJoinCount
    stat.value.devSelf = devSelfCount

    // 2.表格数据：兼容中文/英文冒号，提取编号 + 过滤空标签
    tableData.value = dataList.map(item=>{
      const test = Number(item.testJoin||0)
      const dev = Number(item.devSelf||0)
      const all = test + dev
      const rate = all === 0 ? '0%' : (test / all *100).toFixed(0)+'%'
      let triggerList = []
      let triggerShortList = []
      if (item.triggerItems) {
        triggerList = item.triggerItems.split(',')
        triggerShortList = triggerList.map(text => {
          let val = text.trim()
          // 中文冒号
          if(val.includes('：')){
            return val.split('：')[0].trim()
          }
          // 英文冒号
          if(val.includes(':')){
            return val.split(':')[0].trim()
          }
          // 空格分隔
          if(val.includes(' ')){
            return val.split(' ')[0].trim()
          }
          return val
        }).filter(tag => tag) // 过滤空字符串，消除空白tag
      }
      return {
        groupName: item.groupName,
        total: item.total,
        testJoin: test,
        devSelf: dev,
        rate: rate,
        triggerShortList: triggerShortList
      }
    })

    await nextTick()
    // 3.柱状图
    const xData = dataList.map(i=>i.groupName)
    const allData = dataList.map(i=>Number(i.total))
    if(!barChart) barChart = echarts.init(barChartRef.value)
    barChart.setOption({
      tooltip:{trigger:'axis'},
      xAxis:{type:'category',data:xData},
      yAxis:{type:'value'},
      series:[{type:'bar',data:allData,color:'#409eff'}]
    })

    // 4.堆叠柱状图
    if(!stackChart) stackChart = echarts.init(stackChartRef.value)
    stackChart.setOption({
      tooltip:{trigger:'axis'},
      legend:{},
      xAxis:{type:'category',data:xData},
      yAxis:{type:'value'},
      series:[
        {name:'测试团队介入保障',type:'bar',stack:'total',
          data:dataList.map(i=>Number(i.testJoin)),color:'#e6a23c'},
        {name:'研发自测负责',type:'bar',stack:'total',
          data:dataList.map(i=>Number(i.devSelf)),color:'#67c23a'}
      ]
    })
  } catch (err) {
    console.error('加载统计数据失败',err)
  }
}

// 重置筛选
const resetFilter = () => {
  dateRange.value = []
  filterRegion.value = ''
  filterConclusion.value = ''
  loadData()
}

onMounted(() => {
  loadRegionOptions()
  loadData()
  // 窗口大小变化重绘图表
  window.addEventListener('resize',()=>{
    barChart?.resize()
    stackChart?.resize()
  })
})
</script>

<style scoped>
.page-wrap{
  padding:16px;
}
.filter-card{
  padding:16px;
  background:#fff;
  border-radius:8px;
}
.tab-wrap{
  background:#fff;
  border-radius:8px;
  padding:0 16px;
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
.stat-icon.orange {
  background: #fff7e6;
  color: #e6a23c;
}
.stat-icon.green {
  background: #f0fff4;
  color: #67c23a;
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
  margin-bottom:16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

/* 表格合计行样式，flex布局，严格对齐 */
.table-summary-row {
  display: flex;
  background-color: #f5f7fa;
  font-weight: 600;
}
.sum-cell {
  padding: 12px 16px;
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  box-sizing: border-box;
}
.sum-cell:last-child {
  border-right: none;
}
</style>
