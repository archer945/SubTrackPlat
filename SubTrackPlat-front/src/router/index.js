import { createRouter, createWebHistory } from 'vue-router'
import TaskManage from '@/views/task/TaskManage.vue'

const routes = [
  {
    path: '/',
    redirect: '/tasks'
  },
  {
    path: '/tasks',
    name: 'TaskManage',
    component: TaskManage
  },
  {
    path: '/tasks/:id',
    name: 'TaskDetail',
    component: () => import('@/views/task/TaskDetail.vue'),
    props: true
  }


]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
