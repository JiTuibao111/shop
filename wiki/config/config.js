/**
 * 应用配置
 */
const config = {
  // API基础地址
  baseURL: process.env.NODE_ENV === 'development' 
    ? 'http://localhost:8080' 
    : 'https://your-production-domain.com',
  
  // 请求超时时间（毫秒）
  timeout: 10000,
  
  // 分页默认配置
  pagination: {
    defaultPageSize: 6,
    maxPageSize: 100
  },
  
  // 应用信息
  appInfo: {
    name: '海洋生物用户端',
    version: '1.0.0'
  }
};

export default config;

