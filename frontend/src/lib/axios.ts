import axios from 'axios';
import Cookies from 'js-cookie';

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// Request interceptor: attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = Cookies.get('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: unwrap ApiResponse wrapper + handle 401
api.interceptors.response.use(
  (response) => {
    // Backend wraps all responses in ApiResponse<T> { status, message, data, timestamp }
    // Unwrap automatically so callers get `response.data` = the actual payload
    if (response.data && typeof response.data === 'object' && 'data' in response.data && 'status' in response.data && 'message' in response.data) {
      response.data = response.data.data;
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid — clear and dispatch auth error event
      Cookies.remove('token');
      if (typeof window !== 'undefined') {
        // Dispatch custom event for auth error - components can listen to this
        window.dispatchEvent(new CustomEvent('auth:unauthorized'));
      }
    }
    return Promise.reject(error);
  }
);

export default api;
