import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const baseURL = '/api'

const service = axios.create({
  baseURL: baseURL,
  timeout: 10000
})

service.interceptors.request.use(config => {
  const userStore = useUserStore()
  // 注册接口不强制携带token
  if (userStore.token && !config.url.includes('/user/register')) {
    config.headers.token = userStore.token
  }
  return config
})

service.interceptors.response.use(
  res => {
    if (res.config.responseType === 'blob') {
      return res
    }
    const data = res.data
    if (data.code === 401) {
      // 普通业务接口401，执行原有登录失效逻辑
      ElMessage.error('登录已失效，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      return Promise.reject(data)
    }
    if (data.code !== 200) {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(data)
    }
    return data
  },
  err => {
    ElMessage.error('网络请求失败，请稍后重试')
    return Promise.reject(err)
  }
)

export default service