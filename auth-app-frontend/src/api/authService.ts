import apiClient from "./axiosInstance";
import axios from "axios";

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
    image?: string | null;
    enable?: boolean;
}

export interface LoginRequest {
    email: string;
    password: string;
}


// No interceptors — must stay clean so it never sends an expired access token
const refreshClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  withCredentials: true,
});

export const authService = {
  register: async (userData: RegisterRequest) => {
    const response = await apiClient.post("/auth/register", userData);
    return response.data;
  },

  login: async (loginData: LoginRequest) => {
    const response = await apiClient.post("/auth/login", loginData);
    return response.data;
  },

  logout: async () => {
    const response = await apiClient.post("/auth/logout");
    return response.data;
  },

  refreshToken: async () => {
    const response = await refreshClient.post("/auth/refresh-token", {});
    return response.data;
  },

  testInterceptor: async (email: string) => {
    const response = await apiClient.get("/users/email/" + email);
    return response.data;
  },
};