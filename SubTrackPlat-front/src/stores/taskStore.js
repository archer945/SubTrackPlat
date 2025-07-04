import { defineStore } from 'pinia'
import { fetchTasks } from '@/api/taskManager/taskService'

export const useTaskStore = defineStore('task', {
  state: () => ({
    taskList: [],
    total: 0,
    lastQueryParams: null,
    cacheTime: 0, // 毫秒时间戳
  }),
  actions: {
    async loadTasks(params) {
      const now = Date.now()
      const cacheValid = now - this.cacheTime < 5 * 60 * 1000 // 缓存5分钟
      if (
          JSON.stringify(params) === JSON.stringify(this.lastQueryParams) &&
          cacheValid
      ) {
        // 命中缓存，返回列表和总数
        return { list: this.taskList, total: this.total }
      }
      // 拉取新数据
      const res = await fetchTasks(params)
      this.taskList = res.data.list || []
      this.total = res.data.total || 0
      this.lastQueryParams = params
      this.cacheTime = now
      return { list: this.taskList, total: this.total }
    },
    clearCache() {
      this.taskList = []
      this.total = 0
      this.lastQueryParams = null
      this.cacheTime = 0
    }
  }
})