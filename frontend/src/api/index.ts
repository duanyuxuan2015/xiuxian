import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data;
    if (code === 200) {
      return { data, message } as any;
    } else {
      ElMessage.error(message || '请求失败');
      return Promise.reject(new Error(message));
    }
  },
  error => {
    if (error.response) {
      const { status } = error.response;
      switch (status) {
        case 400:
          ElMessage.error('请求参数错误');
          break;
        case 401:
          ElMessage.error('未授权，请登录');
          break;
        case 403:
          ElMessage.error('拒绝访问');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器错误');
          break;
        default:
          ElMessage.error(error.message || '网络错误');
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接');
    }
    return Promise.reject(error);
  }
);

export default request;
