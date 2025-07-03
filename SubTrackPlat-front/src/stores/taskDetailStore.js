// src/stores/taskDetailStore.js
import { defineStore } from 'pinia'
import { getTaskById } from '@/api/taskManager/taskService'

export const useTaskDetailStore = defineStore('taskDetail', {
  state: () => ({
    cache: new Map()
  }),
  actions: {
    async getTaskDetail(taskId) {
      if (this.cache.has(taskId)) return this.cache.get(taskId)
      const res = await getTaskById(taskId)
      this.cache.set(taskId, res)
      return res
    },
    clearCache() {
      this.cache.clear()
    }
  }
})