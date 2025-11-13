import axios from 'axios';

const base = import.meta.env.VITE_API_URL || 'https://clinicboking-deploy.onrender.com';

// Debug: Log base URL để kiểm tra
console.log('API Base URL:', base);

export const http = axios.create({
  baseURL: base,
  withCredentials: false,
  headers: { 'Content-Type': 'application/json' },
});

http.interceptors.response.use(
  (res) => res,
  (err) => {
    const message =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.message ||
      'Đã có lỗi xảy ra';
    // Create error object with status code for better error handling
    const error: any = new Error(message);
    error.response = err?.response;
    error.status = err?.response?.status;
    return Promise.reject(error);
  }
);

  http.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});