// src/stores/userStore.js
import { defineStore } from 'pinia'
import { fetchUserList } from '@/api/taskManager/userService'

export const useUserStore = defineStore('user', {
  state: () => ({
    userCache: new Map()
  }),
  actions: {
    async getUserList(query) {
      if (this.userCache.has(query)) return this.userCache.get(query)
      const res = await fetchUserList({ name: query })
      this.userCache.set(query, res.data.list || [])
      return this.userCache.get(query)
    },
    clearCache() {
      this.userCache.clear()
    }
  }
})