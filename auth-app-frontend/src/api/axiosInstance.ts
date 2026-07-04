import axios from "axios";
import type { InternalAxiosRequestConfig, AxiosResponse, AxiosError } from "axios";
import { useAuthStore } from "@/store";
import { authService } from "./authService";

interface SpringErrorPayload {
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  timestamp?: string;
}

interface FailedRequest {
  resolve: (token: string) => void;
  reject: (error: unknown) => void;
}

let isRefreshing = false;
let failedQueue: FailedRequest[] = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else if (token) resolve(token);
  });
  failedQueue = [];
};

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const apiClient = axios.create({
  baseURL: `${apiBaseUrl}/api/v1`,
  timeout: 10000,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = useAuthStore.getState().accessToken;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    const status = error.response?.status;

    if (status === 401 && originalRequest && !originalRequest._retry) {
      // Another request is already refreshing — queue this one until it resolves
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({
            resolve: (token: string) => {
              if (originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${token}`;
              }
              resolve(apiClient(originalRequest));
            },
            reject,
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshResponse = await authService.refreshToken();
        const newAccessToken = refreshResponse.data.accessToken;

        console.log('newAccessToken ', newAccessToken);

        useAuthStore.getState().updateAccessToken(newAccessToken);
        processQueue(null, newAccessToken);

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        }
        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        await useAuthStore.getState().logout();
        // window.location.href = "/login";
        return Promise.reject(error.response?.data || error);
      } finally {
        isRefreshing = false;
      }
    }

    let standardizedError: SpringErrorPayload = {
      status: status || 500,
      message: "Something went wrong on our end. Please refresh the page and try again.",
    };

    if (error.response?.data) {
      const backendData = error.response.data as SpringErrorPayload;
      standardizedError = {
        ...standardizedError,
        ...backendData,
        message: backendData.message || error.message,
      };
    } else if (error.request) {
      standardizedError.message = "We're having trouble connecting to our servers. Please check your internet connection and try again.";
    } else {
      standardizedError.message = error.message;
    }

    return Promise.reject(standardizedError);
  }
);

export default apiClient;