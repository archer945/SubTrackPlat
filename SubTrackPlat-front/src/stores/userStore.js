// src/stores/userStore.js
import { defineStore } from 'pinia'
import { fetchUserList } from '@/api/taskManager/userService'
import { usePermissionStore } from './permissionStore'
import { resetRouter } from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    userCache: new Map(),
    token: sessionStorage.getItem('token') || '',
    userInfo: JSON.parse(sessionStorage.getItem('userInfo') || '{}'),
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.userInfo?.userId,
    username: (state) => state.userInfo?.username,
    realName: (state) => state.userInfo?.realName
  },
  actions: {
    // 获取用户列表（已有功能）
    async getUserList(query) {
      if (this.userCache.has(query)) return this.userCache.get(query)
      const res = await fetchUserList({ name: query })
      this.userCache.set(query, res.data.list || [])
      return this.userCache.get(query)
    },
    
    // 设置用户登录信息
    setUserInfo(token, userInfo) {
      // 保存到store
      this.token = token;
      this.userInfo = userInfo;
      
      // 保存到会话存储（关闭浏览器即清除）
      sessionStorage.setItem('token', token);
      sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
      
      // 记住密码功能可以保留在localStorage（如果需要）
    },
    
    // 退出登录
    async logout() {
      // 清除权限信息
      const permissionStore = usePermissionStore();
      permissionStore.clearPermissions();
      
      // 清除用户数据
      this.token = '';
      this.userInfo = {};
      this.userCache.clear();
      
      // 清除会话存储
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('userInfo');
      
      // 重置路由
      resetRouter();
    },
    
    // 清除缓存（原有方法）
    clearCache() {
      this.userCache.clear()
    }
  }
})