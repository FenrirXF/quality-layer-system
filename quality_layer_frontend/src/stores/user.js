import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => {
    const cacheToken = localStorage.getItem('token')
    const cacheUserStr = localStorage.getItem('userInfo')
    let user = null
    // 增加健壮判断，排除 "undefined" / "null" 非法值
    if (cacheUserStr && cacheUserStr !== 'undefined' && cacheUserStr !== 'null') {
      try {
        user = JSON.parse(cacheUserStr)
      } catch (err) {
        // json解析失败直接清空
        localStorage.removeItem('userInfo')
      }
    }
    return {
      token: cacheToken || '',
      user
    }
  },
  actions: {
    setUserInfo(user, token) {
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})