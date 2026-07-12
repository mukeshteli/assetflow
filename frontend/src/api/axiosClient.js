import axios from 'axios';

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach JWT to every outgoing request if the user is logged in.
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('assetflow_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Centralized handling for auth failures so every screen doesn't
// need its own 401 handling logic.
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('assetflow_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  },
);

export default axiosClient;
