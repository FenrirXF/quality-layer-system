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
  // 注册、通知接口均不强制携带token
  if (userStore.token && !config.url.includes('/user/register') && !config.url.includes('/notice/')) {
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
      // 判断：通知接口，静默打印，不跳转登录、不弹登录失效提示
      if(res.config.url.includes('/notice/')){
        console.log('通知服务暂不可用')
        return Promise.reject(data)
      }
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
    const url = err.config?.url || ''
    // 通知接口报错，只控制台输出，不弹窗
    if(url.includes('/notice/')){
      console.log('通知服务暂不可用', err)
      return Promise.reject(err)
    }
    ElMessage.error('网络请求失败，请稍后重试')
    return Promise.reject(err)
  }
)

export default service