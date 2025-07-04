import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/userStore'
import { usePermissionStore } from '@/stores/permissionStore'
import router from '@/router'

// 处理未授权错误（token过期或无效）
export const handleUnauthorized = () => {
  ElMessageBox.confirm(
    '登录状态已过期，请重新登录',
    '系统提示',
    {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 获取store实例
    const userStore = useUserStore()
    const permissionStore = usePermissionStore()
    
    // 清除用户数据和权限数据
    userStore.logout()
    permissionStore.clearPermissions()
    
    // 跳转到登录页，并携带当前页面地址作为重定向参数
    router.push(`/login?redirect=${encodeURIComponent(location.pathname)}`)
  }).catch(() => {
    // 用户取消操作
  })
}

// 处理无权限错误
export const handleForbidden = () => {
  ElMessageBox.alert('您没有权限执行此操作', '权限不足', {
    confirmButtonText: '确定',
    type: 'warning',
    callback: () => {
      // 可以选择跳转到403页面
      router.push('/403')
    }
  })
}

export default {
  handleUnauthorized,
  handleForbidden
} 