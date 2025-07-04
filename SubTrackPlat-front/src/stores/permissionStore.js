// src/stores/permissionStore.js
import { defineStore } from 'pinia'
import { getCurrentUserMenus } from '@/api/systemManager/menu'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    // 菜单列表
    menus: [],
    // 权限标识列表
    permissions: [],
    // 路由列表
    routes: [],
    // 加载状态
    loading: false
  }),

  getters: {
    // 获取菜单树
    getMenus: (state) => state.menus,
    // 获取权限标识列表
    getPermissions: (state) => state.permissions,
    // 获取动态路由
    getRoutes: (state) => state.routes
  },

  actions: {
    // 加载用户菜单和权限
    async loadUserMenus() {
      try {
        this.loading = true;
        
        // 在非测试模式下或测试模式下没有找到权限信息时，正常请求API
        const res = await getCurrentUserMenus();
        if (res.data) {
          // 设置菜单
          this.menus = res.data;
          // 提取权限标识
          this.permissions = this.extractPermissions(res.data);
          // 生成路由配置
          this.routes = this.generateRoutes(res.data);
          return true;
        }
        return false;
      } catch (error) {
        console.error('加载用户菜单失败', error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    // 判断是否有权限
    hasPermission(permission) {
      if (!permission || !this.permissions || this.permissions.length === 0) return false;
      // 检查是否包含通配符权限
      if (this.permissions.includes('*:*:*')) {
        return true;
      }
      return this.permissions.includes(permission);
    },

    // 判断是否有任一权限
    hasAnyPermission(permissions) {
      if (!permissions || permissions.length === 0) return false;
      return permissions.some(perm => this.hasPermission(perm));
    },

    // 判断是否有所有权限
    hasAllPermissions(permissions) {
      if (!permissions || permissions.length === 0) return true;
      return permissions.every(perm => this.hasPermission(perm));
    },

    // 清除权限数据
    clearPermissions() {
      this.menus = [];
      this.permissions = [];
      this.routes = [];
    },

    // 从菜单树中提取所有权限标识
    extractPermissions(menus) {
      const perms = [];
      
      const extractPerms = (menuList) => {
        menuList.forEach(menu => {
          if (menu.perms) {
            perms.push(menu.perms);
          }
          
          if (menu.children && menu.children.length > 0) {
            extractPerms(menu.children);
          }
        });
      };
      
      extractPerms(menus);
      return perms;
    },

    // 生成动态路由配置
    generateRoutes(menus) {
      const routes = [];
      
      const generateRoute = (menu) => {
        // 只处理类型为 'M'(目录) 或 'C'(菜单) 的菜单项
        if (!menu.path || !['M', 'C'].includes(menu.menuType)) {
          return null;
        }
        
        const route = {
          path: menu.path,
          name: menu.menuName,
          meta: {
            title: menu.menuName,
            icon: menu.icon,
            keepAlive: true
          }
        };
        
        // 如果是菜单类型且有组件路径
        if (menu.menuType === 'C' && menu.component) {
          route.component = menu.component;
        } else {
          // 目录类型使用布局组件
          route.component = 'Layout';
        }
        
        // 处理子路由
        if (menu.children && menu.children.length > 0) {
          const childRoutes = [];
          menu.children.forEach(child => {
            const childRoute = generateRoute(child);
            if (childRoute) {
              childRoutes.push(childRoute);
            }
          });
          
          if (childRoutes.length > 0) {
            route.children = childRoutes;
          }
        }
        
        return route;
      };
      
      // 处理顶层菜单
      menus.forEach(menu => {
        const route = generateRoute(menu);
        if (route) {
          routes.push(route);
        }
      });
      
      return routes;
    }
  }
}) 