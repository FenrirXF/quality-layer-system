import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout/index.vue'),
    redirect: '/home',
    children: [
      { path: 'home', component: () => import('@/views/Home/index.vue'), meta: { title: '首页' } },
      { path: 'requirement/list', component: () => import('@/views/Requirement/List.vue'), meta: { title: '需求管理' } },
      { path: 'statistics', component: () => import('@/views/Statistics/index.vue'), meta: { title: '统计分析' } },
      // 系统管理分组，全部子页面标记 auth:admin
      {
        path: 'system',
        redirect: '/system/user',
        meta: { title: '系统管理', auth: 'admin' },
        children: [
          { path: 'user', component: () => import('@/views/System/User.vue'), meta: { title: '用户管理', auth: 'admin' } },
          { path: 'role', component: () => import('@/views/System/Role.vue'), meta: { title: '角色管理', auth: 'admin' } },
          { path: 'region', component: () => import('@/views/System/Region.vue'), meta: { title: '区域管理', auth: 'admin' } },
          { path: 'operLog/list', component: () => import('@/views/OperLog/List.vue'), meta: { title: '操作日志', auth: 'admin' } }
        ]
      },
      { path: 'requirement/detail', name: 'RequirementDetail', component: () => import('@/views/Requirement/RequirementDetail.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 访问登录页，如果有token直接跳转首页
  if (to.path === '/login') {
    if (token) {
      next('/home')
    } else {
      next()
    }
    return
  }

  // 访问其他页面，没有token跳转登录页
  if (!token) {
    next('/login')
    return
  }

  // 获取登录用户信息
  const userInfoStr = localStorage.getItem('userInfo')
  const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null

  // 路由标记auth=admin，并且当前角色不是admin → 拦截
  if (to.meta.auth === 'admin' && userInfo?.role !== 'admin') {
    ElMessage.warning('该模块仅管理员账号可以访问')
    next('/home')
    return
  }
  next()
})

export default router