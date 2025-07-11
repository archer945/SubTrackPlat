import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import permissionDirective from '@/directives/permission'
import './style.css'

// 创建应用实例
const app = createApp(App)
// 创建Pinia实例
const pinia = createPinia()

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(pinia)    // 先注册 Pinia，确保 store 可用
app.use(router)
app.use(ElementPlus)
app.use(permissionDirective)  // 注册权限指令

// 挂载应用
app.mount('#app')