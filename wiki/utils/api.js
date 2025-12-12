/**
 * API请求封装
 */
import config from '@/config/config.js';

const BASE_URL = config.baseURL;

/**
 * 统一请求方法
 */
function request(options) {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = uni.getStorageSync('token') || '';
    
    // 显示加载提示
    if (options.loading !== false) {
      uni.showLoading({
        title: options.loadingText || '加载中...',
        mask: true
      });
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'content-type': options.contentType || 'application/json;charset=utf-8',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        // 隐藏加载提示
        if (options.loading !== false) {
          uni.hideLoading();
        }

        // 处理响应
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data);
          } else {
            // 业务错误
            const errorMsg = res.data.message || '请求失败';
            
            // token过期或未授权处理
            if (res.data.code === 401 || res.statusCode === 401) {
              uni.removeStorageSync('token');
              uni.removeStorageSync('userInfo');
              uni.reLaunch({
                url: '/pages/login/login'
              });
              return;
            }
            
            if (options.showError !== false) {
              uni.showToast({
                icon: 'none',
                title: errorMsg,
                duration: 2000
              });
            }
            reject(new Error(errorMsg));
          }
        } else if (res.statusCode === 401) {
          // HTTP 401 未授权
          uni.removeStorageSync('token');
          uni.removeStorageSync('userInfo');
          uni.reLaunch({
            url: '/pages/login/login'
          });
          reject(new Error('未授权，请先登录'));
        } else {
          // 其他HTTP错误
          const errorMsg = `请求失败 (${res.statusCode})`;
          if (options.showError !== false) {
            uni.showToast({
              icon: 'none',
              title: errorMsg,
              duration: 2000
            });
          }
          reject(new Error(errorMsg));
        }
      },
      fail: (err) => {
        // 隐藏加载提示
        if (options.loading !== false) {
          uni.hideLoading();
        }

        console.error('请求失败:', err);
        const errorMsg = err.errMsg || '网络异常，请检查网络连接';
        
        if (options.showError !== false) {
          uni.showToast({
            icon: 'none',
            title: errorMsg.includes('timeout') ? '请求超时，请重试' : errorMsg,
            duration: 2000
          });
        }
        reject(err);
      },
      complete: () => {
        // 确保加载提示被隐藏
        if (options.loading !== false) {
          uni.hideLoading();
        }
      }
    });
  });
}

/**
 * GET请求
 */
export function get(url, data = {}, options = {}) {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  });
}

/**
 * POST请求
 */
export function post(url, data = {}, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  });
}

/**
 * PUT请求
 */
export function put(url, data = {}, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  });
}

/**
 * DELETE请求
 */
export function del(url, data = {}, options = {}) {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  });
}

export default {
  get,
  post,
  put,
  delete: del,
  request
};

