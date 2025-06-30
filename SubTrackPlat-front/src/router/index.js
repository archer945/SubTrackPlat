import { createRouter, createWebHistory } from 'vue-router'
import TaskManage from '@/views/task/TaskManage.vue'
import DefectManage from '@/views/defect/DefectList.vue'
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
  },
  {
    path: '/defects',
    name: 'DefectManage',
    component: DefectManage
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router