import { createRouter, createWebHistory } from 'vue-router'
import TaskManage from '@/views/task/TaskManage.vue'
import DefectManage from '@/views/defect/DefectManage.vue'
import SystemManager from '@/views/systemManager/SystemManager.vue'
import Login from '@/views/login/Login.vue'
import TaskList from '@/views/task/TaskList.vue'

const routes = [
  {
    path: '/defects',
    name: 'DefectManage',
    component: DefectManage
  },
  {
    path: '/system',
    name: 'SystemManager',
    component: SystemManager
  },
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/Register.vue')
  },
  {
    path: '/tasks',
    name: 'TaskManage',
    component: TaskManage
  },
  {
    path: '/tasks/list',
    name: 'TaskList',
    component: TaskList
  },
  {
    path: '/tasks/:id',
    name: 'TaskDetail',
    component: () => import('@/views/task/TaskDetail.vue'),
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router