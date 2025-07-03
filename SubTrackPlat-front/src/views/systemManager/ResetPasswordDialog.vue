<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="emit('update:visible', $event)"
    title="重置密码"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      :rules="passwordRules"
      label-width="100px"
    >
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        />
        <div class="password-tips">
          <div :class="['tip-item', passwordStrength.length >= 8 ? 'passed' : '']">
            <el-icon><Check v-if="passwordStrength.length >= 8" /><Close v-else /></el-icon>
            <span>长度不少于8个字符</span>
          </div>
          <div :class="['tip-item', passwordStrength.hasUpperCase ? 'passed' : '']">
            <el-icon><Check v-if="passwordStrength.hasUpperCase" /><Close v-else /></el-icon>
            <span>包含大写字母</span>
          </div>
          <div :class="['tip-item', passwordStrength.hasLowerCase ? 'passed' : '']">
            <el-icon><Check v-if="passwordStrength.hasLowerCase" /><Close v-else /></el-icon>
            <span>包含小写字母</span>
          </div>
          <div :class="['tip-item', passwordStrength.hasNumber ? 'passed' : '']">
            <el-icon><Check v-if="passwordStrength.hasNumber" /><Close v-else /></el-icon>
            <span>包含数字</span>
          </div>
          <div :class="['tip-item', passwordStrength.hasSpecial ? 'passed' : '']">
            <el-icon><Check v-if="passwordStrength.hasSpecial" /><Close v-else /></el-icon>
            <span>包含特殊字符</span>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emit('update:visible', false)">取 消</el-button>
        <el-button type="primary" :disabled="!isPasswordValid" @click="submitForm">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'
import { resetUserPassword } from '@/api/systemManager/user'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userId: {
    type: [String, Number],
    default: ''
  },
  username: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 密码强度检查
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  return {
    length: password.length,
    hasUpperCase: /[A-Z]/.test(password),
    hasLowerCase: /[a-z]/.test(password),
    hasNumber: /[0-9]/.test(password),
    hasSpecial: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
  }
})

// 密码有效性检查
const isPasswordValid = computed(() => {
  const strength = passwordStrength.value
  return strength.length >= 8 && 
         strength.hasUpperCase && 
         strength.hasLowerCase && 
         strength.hasNumber && 
         strength.hasSpecial &&
         passwordForm.newPassword === passwordForm.confirmPassword
})

// 表单验证规则
const validatePassword = (rule, value, callback) => {
  const strength = passwordStrength.value
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8个字符'))
  } else if (!strength.hasUpperCase) {
    callback(new Error('密码必须包含大写字母'))
  } else if (!strength.hasLowerCase) {
    callback(new Error('密码必须包含小写字母'))
  } else if (!strength.hasNumber) {
    callback(new Error('密码必须包含数字'))
  } else if (!strength.hasSpecial) {
    callback(new Error('密码必须包含特殊字符'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 关闭对话框
const handleClose = () => {
  passwordFormRef.value?.resetFields()
  emit('update:visible', false)
}

// 提交表单
const submitForm = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await resetUserPassword(props.userId, {
          password: passwordForm.newPassword
        })
        ElMessage.success(`用户 ${props.username} 的密码已重置成功`)
        handleClose()
        emit('success')
      } catch (error) {
        console.error('重置密码失败:', error)
        ElMessage.error('重置密码失败，请重试')
      }
    }
  })
}
</script>

<style scoped>
.password-tips {
  margin-top: 10px;
  font-size: 12px;
}

.tip-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  color: #909399;
}

.tip-item.passed {
  color: #67c23a;
}

.tip-item .el-icon {
  margin-right: 5px;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 