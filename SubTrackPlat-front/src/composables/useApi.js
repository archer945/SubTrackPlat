import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASEURL,
  timeout: 10000
});

// 大屏数据接口
export const useDashboardApi = () => {
  const getDefectType  = () => api.get('/defect-stats/overview');
  const getDefectStats = () => api.get('/defect-stats/summary');
  const getInspectionStats = () => api.get('/inspection-stats/summary');
  
  return {
    getDefectType,
    getDefectStats,
    getInspectionStats
  };
};

// 请求拦截器示例
api.interceptors.response.use(response => {
  return response.data; // 直接返回data数据
}, error => {
  console.error('API Error:', error);
  return Promise.reject(error);
});