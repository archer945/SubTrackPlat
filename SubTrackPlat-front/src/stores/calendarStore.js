// src/stores/calendarStore.js
import { defineStore } from 'pinia'
import { fetchTasks } from '@/api/taskManager/taskService'

export const useCalendarStore = defineStore('calendar', {
  state: () => ({
    monthCache: new Map()
  }),
  actions: {
    async loadMonthTasks(monthStr, params) {
      if (this.monthCache.has(monthStr)) return this.monthCache.get(monthStr)
      const res = await fetchTasks(params)
      this.monthCache.set(monthStr, res.data.list || [])
      return this.monthCache.get(monthStr)
    },
    clearCache() {
      this.monthCache.clear()
    }
  }
})