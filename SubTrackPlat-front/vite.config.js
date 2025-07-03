// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'        // ✅ 这样才能用 __dirname，别删

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],

  // ---------- 模块别名 ----------
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // ❗ 让 @ 指向 src
    },
  },

  // ---------- 本地开发服务器 ----------
  server: {
    proxy: {
      // 只要请求以 /api 开头，就转发到后端 8080 端口
      // 例：/api/defects/page -> http://localhost:8080/defects/page
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      // 系统管理模块
      '/api/systemManager': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/systemManager/, '')
      },
      // 任务模块
      '/api/tasks': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/tasks/, '')
      }
    },
  },

})
