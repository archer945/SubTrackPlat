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
      '@utils': path.resolve(__dirname, './src/utils') // 推荐添加utils专用别名
    },
  },
  
  // ---------- 本地开发服务器 ----------
  server: {
    host: '0.0.0.0',
    port: 5173,
    hmr: true,
    // 确保SPA应用能正常工作
    proxy: {
      // API代理
      '/dashboard/defect-overview': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
      '/dashboard/defect-stats': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
      '/dashboard/inspectSummary': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
      '/dashboard/inspectTrend': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
      '/dashboard/inspection-tasks': {
        target: 'http://localhost:8085',
        changeOrigin: true,
      },
      // 其他API代理保持不变
      '/api/defects': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/systemManager': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/systemManager/, '')
      },
      '/api/tasks': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/tasks/, '')
      },
      '/api/login': {
        target: 'http://localhost:8083',
        changeOrigin: true
      }
    }
  },
})
