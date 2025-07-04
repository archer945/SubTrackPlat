// src/stores/userStore.js
import { defineStore } from 'pinia'
import { fetchUserList } from '@/api/taskManager/userService'
import { usePermissionStore } from './permissionStore'
import { resetRouter } from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    userCache: new Map(),
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
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
      
      // 保存到本地存储
      localStorage.setItem('token', token);
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
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
      
      // 清除本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      
      // 重置路由
      resetRouter();
    },
    
    // 清除缓存（原有方法）
    clearCache() {
      this.userCache.clear()
    }
  }
})