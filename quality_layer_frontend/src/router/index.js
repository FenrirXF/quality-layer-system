import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

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

// 全局守卫：【调试开关】true=直接跳过登录，false=正常登录流程
const SKIP_LOGIN_DEBUG = false;

router.beforeEach(async (to, from, next) => {
  // 调试模式：直接模拟管理员登录状态
  if (SKIP_LOGIN_DEBUG) {
    // 模拟admin管理员数据，适配权限判断
    localStorage.setItem('token', 'debug-admin-token');
    localStorage.setItem('userInfo', JSON.stringify({
      username: 'admin',
      role: 'admin',
      name: '超级管理员'
    }));
    // 访问登录页直接跳转首页
    if (to.path === '/login') {
      next('/home');
    } else {
      next();
    }
    return;
  }

  // ========== 下面是你原本正常的登录逻辑（关闭调试模式生效） ==========
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
    return
  }

  // 没有token → 执行免密登录
  if (!token) {
    try {
      // 请求后端免密接口
      const res = await axios.get('/api/test/freeLogin')
      if (res.code === 200) {
        // 和正常登录逻辑保持一致：存储token、用户信息
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.user))
        // 重新放行目标页面
        next(to.path)
        return
      } else {
        // 免密失败，跳转登录页
        ElMessage.error('自动登录失败，请手动登录')
        next('/login')
        return
      }
    } catch (err) {
      ElMessage.error('服务连接异常，跳转登录')
      next('/login')
      return
    }
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