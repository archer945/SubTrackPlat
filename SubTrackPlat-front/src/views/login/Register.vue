<template>
  <div class="register-container" :style="backgroundStyle">
    <div class="register-box">
      <h2>用户注册</h2>
      
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" prefix-icon="el-icon-lock" placeholder="请再次输入密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" prefix-icon="el-icon-user" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" prefix-icon="el-icon-message" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="registerForm.phone" prefix-icon="el-icon-phone" placeholder="请输入电话号码"></el-input>
        </el-form-item>
        
        <el-form-item>
          <div class="register-options">
            <el-button type="text" @click="goToLogin">返回登录</el-button>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="register-button" @click="handleRegister">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: ''
})

// 自定义验证规则 - 确认密码
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 自定义验证规则 - 邮箱
const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (value === '') {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

// 自定义验证规则 - 电话
const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (value === '') {
    callback(new Error('请输入电话号码'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入有效的手机号码'))
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
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ]
}

// 背景图片样式
const backgroundStyle = {
  backgroundImage: 'url(https://img.freepik.com/free-photo/subway-tunnel-with-lights_23-2148942350.jpg)',
  backgroundSize: 'cover',
  backgroundPosition: 'center'
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    // 表单验证
    const valid = await registerFormRef.value.validate()
    
    if (valid) {
      // 显示加载状态
      loading.value = true
      
      // 准备提交的数据（移除确认密码字段）
      const submitData = {
        username: registerForm.username,
        password: registerForm.password,
        realName: registerForm.realName,
        email: registerForm.email,
        tel: registerForm.phone
      }
      
      // 调用后端API
      const response = await axios.post('http://localhost:8083/api/register', submitData)
      
      // 处理响应
      if (response.data.code === 200) {
        ElMessage.success(response.data.message || '注册成功，请登录')
        // 注册成功后跳转到登录页面
        router.push('/login')
      } else {
        // 显示错误信息
        ElMessage.error(response.data.message || '注册失败，请稍后重试')
      }
    }
  } catch (error) {
    // 处理错误
    console.error('注册请求错误:', error)
    if (error.response) {
      // 服务器返回了错误状态码
      ElMessage.error(error.response.data.message || `注册失败: ${error.response.status}`)
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      // 请求设置时出错
      ElMessage.error(`请求错误: ${error.message}`)
    }
  } finally {
    // 无论成功或失败，都关闭加载状态
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  position: relative;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.register-box {
  width: 450px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
  max-height: 90vh;
  overflow-y: auto;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
  font-weight: bold;
}

.register-options {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
}

.register-options .el-button {
  padding: 0;
  margin: 0;
}

.register-button {
  width: 100%;
  margin-top: 10px;
}
</style>