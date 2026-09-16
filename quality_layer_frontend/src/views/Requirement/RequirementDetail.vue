<template>
  <div class="card">
    <div class="header-wrap">
      <h2 class="title">需求详情</h2>
      <div class="btn-group">
        <el-button @click="goEdit">编辑</el-button>
        <el-button @click="backList">返回列表</el-button>
      </div>
    </div>

    <div class="detail-table">
      <div class="row">
        <div class="label">序号</div>
        <div class="value">{{ info.id || '-' }}</div>
        <div class="label">区域</div>
        <div class="value">{{ info.region || '-' }}</div>
      </div>

      <div class="row">
        <div class="label">技术经理</div>
        <div class="value">{{ info.techManager || '-' }}</div>
        <div class="label">研发负责人</div>
        <div class="value">{{ info.devManager || '-' }}</div>
      </div>

      <div class="row">
        <div class="label">研发组长</div>
        <div class="value">{{ info.devLeader || '-' }}</div>
        <div class="label">测试组长</div>
        <div class="value">{{ info.testLeader || '-' }}</div>
      </div>

      <div class="row">
        <div class="label">评估结论</div>
        <div class="value">
          <el-tag size="small" :type="getTagType(info.conclusion)">
            {{ info.conclusion || '-' }}
          </el-tag>
        </div>
        <div class="label">状态</div>
        <div class="value">
          <el-tag size="small" :type="getStatusTagType(info.status)">
            {{ info.status || '-' }}
          </el-tag>
        </div>
      </div>

      <div class="row">
        <div class="label">上线时间</div>
        <div class="value">{{ info.deadline || '-' }}</div>
        <div class="label">创建人</div>
        <div class="value">{{ info.createUser || '-' }}</div>
      </div>

      <div class="row">
        <div class="label">创建时间</div>
        <div class="value">{{ info.createTime || '-' }}</div>
        <div class="label">修改人</div>
        <div class="value">{{ info.updateUser || '-' }}</div>
      </div>

      <div class="row">
        <div class="label">修改时间</div>
        <div class="value">{{ info.updateTime || '-' }}</div>
        <div class="label"></div>
        <div class="value"></div>
      </div>

      <div class="row full-row">
        <div class="label">项目需求/任务名称</div>
        <div class="value">{{ info.taskName || '-' }}</div>
      </div>

      <div class="row full-row">
        <div class="label">触发评估项</div>
        <div class="value">{{ info.triggerItem || '-' }}</div>
      </div>

      <div class="row full-row">
        <div class="label">备注</div>
        <div class="value">{{ info.remark || '-' }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getReqDetailApi } from '@/api/requirement'

const router = useRouter()
const route = useRoute()
const info = ref({})
const reqId = ref('')

const loadDetail = async (id) => {
  if (!id) return
  try {
    const res = await getReqDetailApi(id)
    info.value = res.data
  } catch (err) {
    ElMessage.error('加载详情失败')
    console.error(err)
  }
}

watch(
  () => route.query.id,
  (newId) => {
    reqId.value = newId
    loadDetail(newId)
  },
  { immediate: true }
)

const backList = () => {
  router.push('/requirement/list')
}

const goEdit = () => {
  router.push({
    path: '/requirement/list',
    query: {
      editId: reqId.value
    }
  })
}

const getTagType = (val) => {
  return val === '测试团队介入保障' ? 'warning' : ''
}

const getStatusTagType = (status) => {
  if (status === '已逾期') return 'danger'
  if (status === '已完成') return 'success'
  return ''
}
</script>

<style scoped>
.card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}

.header-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title {
  font-size: 20px;
  font-weight: 500;
  margin: 0;
}

.detail-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}

.row {
  display: grid;
  grid-template-columns: 120px 1fr 120px 1fr;
}

.full-row {
  grid-template-columns: 120px 1fr;
}

.row .label,
.row .value {
  border-bottom: 1px solid #e8e8e8;
  padding: 12px 16px;
  box-sizing: border-box;
}

.row .label {
  background: #fafafa;
  color: #666;
  font-weight: 500;
  border-right: 1px solid #e8e8e8;
}

.row .value {
  color: #333;
}

.row:last-child .label,
.row:last-child .value {
  border-bottom: none;
}
</style>