<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <h1 class="sys-title">质量分层系统</h1>
        <p class="sys-desc">基于质量评估模型的需求分层管理平台<br>支持多区域协同、智能评估分类，助力研发质量提升</p>
      </div>
      <div class="login-right">
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-subtitle">请输入您的账号信息</p>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form>
        <div style="margin-top:12px;text-align:right">
          <span @click="openRegisterDialog" style="color:#409EFF;cursor:pointer">没有账号？去注册</span>
        </div>
        <div class="demo-accounts">
          <p>演示账号：</p>
          <p>管理员：admin / admin123</p>
          <p>安徽区域：chuzhuoting / 123456</p>
          <p>芜湖区域：zhangjie / 123456</p>
        </div>
      </div>
    </div>

    <!-- 注册弹窗【增加确认密码】 -->
    <el-dialog v-model="registerVisible" title="账号注册" width="460px">
      <el-form :model="registerForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="registerForm.name"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" show-password></el-input>
          <div style="font-size:12px;color:#909399;margin-top:4px">密码至少6位</div>
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="registerForm.confirmPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="所属区域">
          <el-select v-model="registerForm.region" placeholder="请选择区域">
            <el-option label="安徽" value="安徽" />
            <el-option label="芜湖" value="芜湖" />
            <el-option label="合肥" value="合肥" />
            <el-option label="蚌埠" value="蚌埠" />
            <el-option label="阜阳" value="阜阳" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
// 导入接口
import { loginApi } from '@/api/login'
import { registerApi } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册弹窗
const registerVisible = ref(false)
// ✅ 改用reactive，不再使用ref({})
const registerForm = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  region: ''
})

const openRegisterDialog = () => {
  registerForm.username = ''
  registerForm.name = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  registerForm.region = ''
  registerVisible.value = true
}

const handleRegister = async () => {
  // 直接解构，不需要 .value
  const { username, name, password, confirmPassword, region } = registerForm

  if (!username || !name || !password || !confirmPassword || !region) {
    ElMessage.warning('请完整填写所有信息')
    return
  }
  if (password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  if (password !== confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  await registerApi({
    username,
    name,
    password,
    region
  })
  ElMessage.success('注册成功！请使用新账号登录')
  registerVisible.value = false
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const res = await loginApi(loginForm)
    const token = res.data.token
    const userInfo = res.data.user
    userStore.setUserInfo(userInfo, token)
    ElMessage.success('欢迎回来，' + userInfo.name)
    router.push('/home')
  } catch (err) {
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  width: 900px;
  height: 500px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}
.login-left {
  width: 45%;
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  color: #fff;
  padding: 60px 40px;
}
.sys-title {
  font-size: 28px;
  margin-bottom: 20px;
}
.sys-desc {
  font-size: 14px;
  line-height: 2;
  opacity: 0.9;
}
.login-right {
  width: 55%;
  padding: 60px 50px;
}
.login-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}
.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin-bottom: 30px;
}
.demo-accounts {
  margin-top: 20px;
  font-size: 12px;
  color: #909399;
  line-height: 1.8;
}
</style>
