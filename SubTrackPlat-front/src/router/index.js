import { createRouter, createWebHistory } from 'vue-router'
import TaskManage from '@/views/task/TaskManage.vue'
import DefectManage from '@/views/defect/DefectManage.vue'
import SystemManager from '@/views/systemManager/SystemManager.vue'
import Login from '@/views/login/Login.vue'
import TaskList from '@/views/task/TaskList.vue'
import { usePermissionStore } from '@/stores/permissionStore'
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/userStore';

// 路由白名单，不需要权限验证的路由
const whiteList = ['/login', '/register', '/404', '/403'];

// 普通用户可访问的路由（已登录即可访问，无需额外权限）
const commonRoutes = ['/tasks', '/tasks/list', '/defects', '/dashboard'];

// 基础路由
const constantRoutes = [
  {
    path: '/dashboard',
    name: 'DashBoard',
    component: () => import('@/views/dashboard/DashBoard.vue'),
    props: true
  },
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
  },
  // 权限错误页面
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue')
  },
  // 页面不存在
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue')
  },
  // 通配符路由，必须放在最后
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
];

// 创建路由
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes
})

// 动态添加路由方法
export function addRoutes(routes) {
  if (!routes || routes.length === 0) return;
  
  // 遍历并添加路由
  routes.forEach(route => {
    if (!router.hasRoute(route.name)) {
      router.addRoute(route);
    }
  });
}

// 重置路由
export function resetRouter() {
  // 获取所有命名路由
  const routeNames = router.getRoutes()
    .filter(route => route.name)
    .map(route => route.name);
    
  // 移除所有动态添加的路由
  routeNames.forEach(name => {
    if (name && !constantRoutes.find(r => r.name === name)) {
      router.removeRoute(name);
    }
  });
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 白名单直接放行
  if (whiteList.includes(to.path)) {
    return next();
  }
  
  // 检查是否有token
  const token = sessionStorage.getItem('token');
  if (!token) {
    // 没有token，重定向到登录页
    return next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
  }
  
  // 普通路由直接放行（任务、缺陷等页面任何已登录用户都能访问）
  if (commonRoutes.some(route => to.path.startsWith(route))) {
    return next();
  }
  
  // 获取用户信息和权限store
  const userStore = useUserStore();
  const permissionStore = usePermissionStore();
  
  // 特殊路径权限控制（例如系统管理页面仅管理员可访问）
  if (to.path === '/system') {
    // 判断是否为管理员
    if (userStore.username !== 'admin') {
      // 不是管理员，重定向到403页面
      ElMessage.error('您没有权限访问系统管理页面');
      return next('/403');
    }
  }
  
  // 检查是否已加载权限数据
  if (permissionStore.permissions.length === 0) {
    try {
      // 加载用户权限和菜单
      const success = await permissionStore.loadUserMenus();
      if (!success) {
        // 加载失败，跳转到登录页
        sessionStorage.removeItem('token');
        return next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
      }
      
      // 如果是管理员才需要添加动态路由
      if (userStore.username === 'admin') {
        // 添加动态路由
        addRoutes(permissionStore.routes);
        
        // 重新导航到当前路由（确保能匹配到新添加的路由）
        return next({ ...to, replace: true });
      }
    } catch (error) {
      console.error('路由守卫处理异常', error);
      return next('/login');
    }
  }
  
  // 权限验证通过
  next();
});

export default router