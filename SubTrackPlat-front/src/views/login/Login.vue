<template>
  <div class="login-container" :style="backgroundStyle">
    <div class="login-box">
      <h2>地铁隧道巡线大数据仿真和分析平台</h2>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="验证码" prop="captcha">
          <div class="captcha-container">
            <el-input v-model="loginForm.captcha" placeholder="请输入验证码"></el-input>
            <div class="captcha-img" @click="refreshCaptcha">
              <img :src="captchaUrl" alt="验证码" />
            </div>
          </div>
        </el-form-item>
        
        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
            <el-button type="text" @click="goToRegister">注册账号</el-button>
            <el-button type="text" @click="goToResetPassword">忘记密码</el-button>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="login-button" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 存储当前正确的验证码值
const currentCaptchaCode = ref('')

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '', // 用户输入的验证码
  remember: false,
})

// 自定义验证规则 - 验证码
const validateCaptcha = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入验证码'))
  } else if (value.toString() !== currentCaptchaCode.value.toString()) {
    callback(new Error('验证码不正确'))
    // 验证码错误时自动刷新
    refreshCaptcha()
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 6, message: '请输入正确的验证码', trigger: 'blur' }
  ]
}

// 验证码URL
const captchaUrl = ref('https://dummyimage.com/120x40/e0e0e0/333&text=1234')

// 背景图片样式
const backgroundStyle = {
  backgroundImage: 'url(https://img.picui.cn/free/2025/07/01/6863a68ef212a.jpeg)',
  backgroundSize: 'cover',
  backgroundPosition: 'center'
}

// 刷新验证码
const refreshCaptcha = () => {
  // 实际项目中应该调用后端API获取新的验证码
  // 这里使用模拟的验证码图片
  const randomCode = Math.floor(1000 + Math.random() * 9000)
  currentCaptchaCode.value = randomCode.toString()
  captchaUrl.value = `https://dummyimage.com/120x40/e0e0e0/333&text=${randomCode}`
  // 清空用户输入的验证码
  loginForm.captcha = ''
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    
    if (valid) {
      // 显示加载状态
      loading.value = true

      // 验证码前端验证
      if (loginForm.captcha.toString() !== currentCaptchaCode.value.toString()) {
        ElMessage.error('验证码不正确')
        refreshCaptcha()
        loading.value = false
        return
      }

      // 准备提交的数据
      const submitData = {
        username: loginForm.username,
        password: loginForm.password,
      }
      
      try {
        const response = await axios.post('http://localhost:8080/api/login', submitData)
        
        // 处理响应
        if (response.data.code === 200) {
          ElMessage.success(response.data.message || '登录成功')
          
          // 如果记住密码，可以将用户名和密码保存到localStorage
          if (loginForm.remember) {
            localStorage.setItem('username', loginForm.username)
            localStorage.setItem('password', loginForm.password)
            // 注意：实际项目中不应该直接存储密码，这里仅作演示
            // 可以考虑使用加密方式或token机制
          } else {
            // 如果不记住密码，清除之前可能存储的信息
            localStorage.removeItem('username')
            localStorage.removeItem('password')
          }
          
          // 登录成功后跳转到首页或其他页面
          router.push('/tasks')
        } else {
          // 显示错误信息
          ElMessage.error(response.data.message || '登录失败')
          // 登录失败时刷新验证码
          refreshCaptcha()
        }
      } catch (error) {
        console.error('登录请求错误:', error)
        if (error.response) {
          // 服务器返回了错误状态码
          ElMessage.error(error.response.data.message || `登录失败: ${error.response.status}`)
        } else if (error.request) {
          // 请求发出但没有收到响应
          ElMessage.error('服务器无响应，请检查网络连接')
        } else {
          // 请求设置时出错
          ElMessage.error(`请求错误: ${error.message}`)
        }
        // 出错时刷新验证码
        refreshCaptcha()
      }
    } else {
      ElMessage.error('请正确填写登录信息')
      return false
    }
  } catch (error) {
    console.error('表单验证错误:', error)
  } finally {
    // 无论成功或失败，都关闭加载状态
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  // ElMessage.info('注册功能暂未实现')
  // 实际项目中应该跳转到注册页面
  router.push('/register')
}

const goToResetPassword = async () => {
  try {    
    if (loginForm.username) {
      // 显示加载状态
      loading.value = true
      
      try {
        const response = await axios.post('http://localhost:8080/api/reset-password?username=' + loginForm.username)

        // 处理响应
        if (response.data.code === 200) {
          ElMessage.success(response.data.message || '重置成功')
        } else {
          // 显示错误信息
          ElMessage.error(response.data.message || '重置失败')
          // 登录失败时刷新验证码
          refreshCaptcha()
        }
      } catch (error) {
        console.error('登录请求错误:', error)
        if (error.response) {
          // 服务器返回了错误状态码
          ElMessage.error(error.response.data.message || `失败: ${error.response.status}`)
        } else if (error.request) {
          // 请求发出但没有收到响应
          ElMessage.error('服务器无响应，请检查网络连接')
        } else {
          // 请求设置时出错
          ElMessage.error(`请求错误: ${error.message}`)
        }
        // 出错时刷新验证码
        refreshCaptcha()
      }
    } else {
      ElMessage.error('请正确填写登录信息')
      return false
    }
  } catch (error) {
    console.error('错误', error)
  } finally {
    // 无论成功或失败，都关闭加载状态
    loading.value = false
  }
}

// 页面加载时检查是否有保存的用户名
onMounted(() => {
  const savedUsername = localStorage.getItem('username')
  const savedPassword = localStorage.getItem('password')
  if (savedUsername) {
    // loginForm.username = savedUsername
    loginForm.password = savedPassword
    loginForm.remember = true
  }
  
  // 初始化验证码
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
  font-weight: bold;
}

.captcha-container {
  display: flex;
  align-items: center;
}

.captcha-img {
  margin-left: 10px;
  cursor: pointer;
  height: 40px;
  width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-options .el-checkbox {
  margin-right: 0;
}

.login-options .el-button {
  padding: 0;
  margin: 0;
}

.login-button {
  width: 100%;
  margin-top: 10px;
}
</style>