import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.NODE_ENV === 'production' ? '/' : 'http://localhost:8085', // 修改为后端正确的端口8080
  timeout: 10000, // 请求超时时间
  withCredentials: true, // 允许携带cookie
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 可在此处添加请求头等设置
    console.log('发送请求:', config.baseURL + config.url); // 添加日志便于调试
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    console.log('响应数据:', res); // 添加日志便于调试
    if (res.code !== 10000) {
      console.error('API错误:', res.message || '未知错误')
      return Promise.reject(new Error(res.message || '未知错误'))
    } else {
      return res
    }
  },
  (error) => {
    console.error('响应错误:', error)
    return Promise.reject(error)
  }
)

export default service