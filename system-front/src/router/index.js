import { createRouter, createWebHistory } from 'vue-router'
import SystemManager from '@/views/SystemManager.vue'
import TestDataView from '@/views/TestDataView.vue'

const routes = [
  {
    path: '/',
    redirect: '/system'
  },
  {
    path: '/system',
    name: 'SystemManager',
    component: SystemManager
  },
  {
    path: '/test-data',
    name: 'TestData',
    component: TestDataView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 