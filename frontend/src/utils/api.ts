import axios from "axios";
import { useAuthStore } from "../stores/auth";
import API_CONFIG from "../config/api";

const baseURL = API_CONFIG.getBaseURL();
console.log("API Base URL:", baseURL);

const api = axios.create({
  baseURL,
});

api.interceptors.request.use((config) => {
  const auth = useAuthStore();
  if (auth.token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error);

    // Only logout on authentication errors, not on other errors
    if (error.response?.status === 401) {
      // 401 means unauthorized - token is invalid or expired
      const auth = useAuthStore();
      auth.logout();
      // Redirect to login page
      window.location.href = "/login";
    }
    // Don't logout on 403 - this might be a permission issue, not authentication
    // Let the component handle 403 errors appropriately

    return Promise.reject(error);
  }
);

export default api;
