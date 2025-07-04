import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // API的基础URL，会被代理到后端服务器
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    console.log('发送请求:', config.method.toUpperCase(), config.url, config.params || config.data)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 如果响应类型是blob（文件下载），直接返回响应数据
    if (response.config.responseType === 'blob') {
      // 检查是否是空的blob或错误响应
      if (response.data.size === 0) {
        ElMessage.error('导出失败：返回的文件为空')
        return Promise.reject(new Error('返回的文件为空'))
      }
      return response.data
    }
    
    const res = response.data
    console.log('接收响应:', response.config.url, JSON.stringify(res))
    
    // 检查响应中是否包含错误信息，即使状态码是200
    if (res.data && typeof res.data === 'string' && (res.data.includes('错误') || res.data.includes('失败') || res.data.includes('不能'))) {
      console.error('请求返回错误信息:', res.data)
      ElMessage.error(res.data)
      return {
        error: true,
        code: res.code,
        message: res.message || '请求失败',
        data: res.data
      }
    }
    
    // 根据后端接口规范判断请求是否成功
    if (res.code === 10000) {
      // 直接返回数据，不做过多处理，让组件自己处理数据格式
      console.log('响应成功，返回原始数据')
      
      // 如果是分页数据，返回标准格式
      if (res.data && res.data.rows !== undefined) {
        // 处理状态字段，确保是数字类型
        const records = res.data.rows.map(item => {
          // 处理状态字段
          if (item.status !== undefined && typeof item.status === 'string') {
            item.status = parseInt(item.status, 10);
          }
          // 处理visible字段
          if (item.visible !== undefined && typeof item.visible === 'string') {
            item.visible = parseInt(item.visible, 10);
          }
          return item;
        });
        
        return {
          ...res,
          data: {
            ...res.data,
            rows: records
          }
        };
      }
      
      // 处理菜单树数据
      if (res.data && Array.isArray(res.data) && res.data.length > 0 && res.data[0].menuId !== undefined) {
        // 菜单树数据处理
        function processChildren(children) {
          if (!children || children.length === 0) return children;
          
          return children.map(item => {
            const newItem = { ...item };
            
            // 处理状态字段
            if (newItem.status !== undefined && typeof newItem.status === 'string') {
              newItem.status = parseInt(newItem.status, 10);
            }
            
            // 处理visible字段
            if (newItem.visible !== undefined && typeof newItem.visible === 'string') {
              newItem.visible = parseInt(newItem.visible, 10);
            }
            
            // 递归处理子菜单
            if (newItem.children && newItem.children.length > 0) {
              newItem.children = processChildren(newItem.children);
            }
            
            return newItem;
          });
        }
        
        // 处理顶层菜单
        const processedData = processChildren(res.data);
        
        return {
          ...res,
          data: processedData
        };
      }
      
      return res
    }
    
    // 如果响应码不是10000，说明请求出错
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    console.error('响应错误:', error)
    
    // 网络错误处理
    if (!error.response) {
      ElMessage.error('网络错误，请检查您的网络连接')
      return Promise.reject(error)
    }
    
    // 根据HTTP状态码处理不同的错误
    const { status, statusText, data } = error.response
    
    switch (status) {
      case 400:
        ElMessage.error(`请求错误: ${data.message || statusText}`)
        break
      case 401:
        ElMessage.error('未授权，请重新登录')
        // 清除token并跳转到登录页
        localStorage.removeItem('token')
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
        break
      case 403:
        ElMessage.error('拒绝访问')
        break
      case 404:
        ElMessage.error(`请求的资源不存在: ${error.config.url}`)
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error(`请求失败: ${statusText}`)
    }
    
    return Promise.reject(error)
  }
)

export default request 