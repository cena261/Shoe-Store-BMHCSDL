import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

apiClient.interceptors.request.use(
  (config) => {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('jwt');
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => {
    if (response.data && response.data.status === 'success') {
      return response.data.data !== undefined ? response.data.data : null;
    }
    return response.data;
  },
  (error) => {
    if (error.response?.data) {
      const apiError = error.response.data;
      const errorMessage =
        apiError.message || 'An error occurred while processing your request';
      throw new Error(errorMessage);
    }
    if (error.code === 'ECONNABORTED' || error.message === 'Network Error') {
      throw new Error('Network error. Please check your connection and try again.');
    }
    throw new Error(error.message || 'An unexpected error occurred');
  }
);

const client = {
  get: async <T = any>(url: string, config?: any): Promise<T> => {
    return apiClient.get(url, config);
  },
  post: async <T = any>(url: string, data?: any, config?: any): Promise<T> => {
    return apiClient.post(url, data, config);
  },
  put: async <T = any>(url: string, data?: any, config?: any): Promise<T> => {
    return apiClient.put(url, data, config);
  },
  delete: async <T = any>(url: string, config?: any): Promise<T> => {
    return apiClient.delete(url, config);
  },
};

export default client;
