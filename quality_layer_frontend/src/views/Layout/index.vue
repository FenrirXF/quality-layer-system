<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="sidebar-logo">
        <span v-if="!isCollapse">质量分层系统</span>
        <span v-else>质</span>
      </div>
      <div class="sidebar-nav">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          router
          background-color="#001529"
          text-color="#b7c0cc"
          active-text-color="#fff"
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/requirement/list">
            <el-icon><Document /></el-icon>
            <template #title>需求管理</template>
          </el-menu-item>
          <el-menu-item index="/statistics">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>统计分析</template>
          </el-menu-item>

          <!-- 仅admin展示系统管理 -->
          <el-sub-menu v-if="userStore.user?.role === 'admin'" index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">用户管理</el-menu-item>
            <el-menu-item index="/system/role">角色管理</el-menu-item>
            <el-menu-item index="/system/region">区域管理</el-menu-item>
            <el-menu-item index="/system/operLog/list">操作日志</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
      <!-- 侧边底部折叠按钮 -->
      <div class="sidebar-footer" @click="isCollapse = !isCollapse">
        <el-icon :size="18">
          <component :is="isCollapse ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <template v-for="item in $route.matched" :key="item.path">
              <el-breadcrumb-item v-if="item.meta.title && item.path !== '/'">
                {{ item.meta.title }}
              </el-breadcrumb-item>
            </template>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 消息通知 -->
          <el-popover placement="bottom-end" width="360" trigger="click" @show="loadNotificationList">
            <template #reference>
              <el-badge :value="unreadCount" :max="99" class="notify-badge">
                <el-icon :size="20" class="notify-icon"><Bell /></el-icon>
              </el-badge>
            </template>
            <div class="notify-panel">
              <div class="notify-header">
                <span class="notify-title">消息通知</span>
                <el-button link size="small" @click="markAllRead">全部已读</el-button>
              </div>
              <div class="notify-list">
                <div
                  v-for="item in notificationList"
                  :key="item.id"
                  class="notify-item"
                  :class="{ unread: item.isRead === 0 }"
                  @click="handleClickNotify(item)"
                >
                  <div class="notify-text">{{ item.title }}</div>
                  <div class="notify-time">{{ item.createTime }}</div>
                </div>
                <div v-if="notificationList.length === 0" class="notify-empty">暂无消息</div>
              </div>
            </div>
          </el-popover>

          <!-- 用户下拉 -->
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <div class="user-avatar">
                {{ userStore.user?.name?.charAt(0) || '用' }}
              </div>
              <div class="user-text-wrap">
                <div class="user-name">{{ userStore.user?.name || '用户' }}</div>
                <div class="user-tag">
                  {{ userStore.user?.role === 'admin'
                    ? '管理员'
                    : (userStore.user?.region || '') + ' · 区域填报人员' }}
                </div>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="pwd">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <!-- 修改密码弹窗-->
  <el-dialog v-model="pwdDialogVisible" title="修改密码" width="420px">
    <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
      <el-form-item label="旧密码" prop="oldPwd">
        <el-input v-model="pwdForm.oldPwd" type="password" show-password></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPwd">
        <el-input v-model="pwdForm.newPwd" type="password" show-password></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPwd">
        <el-input v-model="pwdForm.confirmPwd" type="password" show-password></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleUpdatePwd">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updatePwdApi } from '@/api/user'
import { getNoticeUnreadApi, getNoticeListApi, markNoticeReadApi } from '@/api/notice'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Document, DataAnalysis, Setting, Fold, Expand, ArrowDown, Bell } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠
const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

// 通知相关
const notificationList = ref([])
const unreadCount = ref(0)

// 加载未读数量
const loadNoticeCount = async () => {
  try{
    const res = await getNoticeUnreadApi()
    unreadCount.value = res.data
  }catch(e){
    console.log('通知服务暂不可用')
  }
}

// 加载通知列表（弹窗打开触发）
const loadNotificationList = async () => {
  try {
    const res = await getNoticeListApi()
    if (res.code === 200) {
      notificationList.value = res.data
    }
  } catch (err) {
    console.log('通知列表加载失败', err)
    notificationList.value = []
  }
}

// 点击单条通知，标记已读
const handleClickNotify = async (item) => {
  if (item.isRead === 1) return
  await markNoticeReadApi(item.id)
  loadNoticeCount()
  loadNotificationList()
}

// 全部标记已读
const markAllRead = async () => {
  const unreadItems = notificationList.value.filter(i => i.isRead === 0)
  for (const item of unreadItems) {
    await markNoticeReadApi(item.id)
  }
  loadNoticeCount()
  loadNotificationList()
}

// 修改密码弹窗
const pwdDialogVisible = ref(false)
const pwdFormRef = ref()
const pwdForm = ref({
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
})
const pwdRules = {
  oldPwd: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPwd: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPwd: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.newPwd) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 用户下拉菜单
const handleCommand = async (cmd) => {
  if (cmd === 'pwd') {
    pwdForm.value = {
      oldPwd: '',
      newPwd: '',
      confirmPwd: ''
    }
    pwdDialogVisible.value = true
  } else if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
      .then(async () => {
        await userStore.logout()
        router.push('/login')
      })
      .catch(() => {})
  }
}

// 修改密码提交
const handleUpdatePwd = async () => {
  await pwdFormRef.value.validate()
  await updatePwdApi(pwdForm.value)
  ElMessage.success('修改成功，请重新登录')
  pwdDialogVisible.value = false
  await userStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadNoticeCount()
  // 定时轮询刷新红点（30秒）
  setInterval(() => loadNoticeCount(), 30000)
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.sidebar {
  background: #001529;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}
.sidebar-logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  background: #002140;
}
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
}
.sidebar-footer {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255,255,255,0.08);
  color: rgba(255,255,0.5);
  cursor: pointer;
}
.sidebar-footer:hover {
  color: #fff;
}
:deep(.el-menu) {
  border-right: none;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.notify-badge {
  cursor: pointer;
}
.notify-icon {
  color: #606266;
}
.notify-panel {
  width: 360px;
}
.notify-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}
.notify-title {
  font-weight: 600;
  font-size: 14px;
}
.notify-list {
  max-height: 320px;
  overflow-y: auto;
}
.notify-item {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f2f5;
}
.notify-item:hover {
  background: #f5f7fa;
}
.notify-item.unread .notify-text {
  font-weight: 600;
}
.notify-text {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}
.notify-time {
  font-size: 12px;
  color: #c0c4cc;
}
.notify-empty {
  padding: 30px;
  text-align: center;
  color: #909399;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
}
.user-info:hover {
  background: #f0f2f5;
}
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}
.user-text-wrap {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}
.user-name {
  font-size: 14px;
  color: #303133;
}
.user-tag {
  font-size: 12px;
  color: #909399;
}
:deep(.el-main) {
  max-width: unset !important;
  width: 100%;
}
.main-content {
  background: #f0f2f5;
  padding: 20px;
}
</style>
