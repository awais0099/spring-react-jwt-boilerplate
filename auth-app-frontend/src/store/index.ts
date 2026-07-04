import { create } from "zustand";
import { authService } from "@/api/authService";
import type User from "@/models/User";

interface ApiError {
    status: number;
    message: string;
    error: string;
    path: string;
    timestamp: string;
}

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isHydrated: boolean;
  login: (user: User, token: string) => void;
  logout: () => Promise<void>;
  updateAccessToken: (token: string) => void;
  initializeAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  accessToken: null,
  isAuthenticated: false,
  isHydrated: false,

  login: (user, token) => {
    localStorage.setItem("accessToken", token);
    localStorage.setItem("userEmail", user.email);
    set({ user, accessToken: token, isAuthenticated: true });
  },

  logout: async () => {
    try {
      await authService.logout();
    } catch {
      // Proceed with local cleanup even if the server call fails
    }
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userEmail");
    set({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isHydrated: true,
    });
  },

  initializeAuth: async () => {
    // Guard: already hydrated, nothing to do
    if (get().isHydrated) return;

    const cachedToken = localStorage.getItem("accessToken");
    const cachedEmail = localStorage.getItem("userEmail");

    // Scenario A: No session signature exists — boot as public guest
    if (!cachedToken || !cachedEmail) {
      set({ isHydrated: true, isAuthenticated: false });
      return;
    }

    try {
      // Scenario B: Session token found — verify against Spring Security
      set({ accessToken: cachedToken }); // Let Axios interceptor pick this up

      const response = await authService.testInterceptor(cachedEmail);
      set({ user: response.data, isAuthenticated: true, isHydrated: true });
    } catch (error: unknown) {
      const apiError = error as ApiError;
      console.log('error ', apiError); 
      
      if (apiError.status === 401) {
        // Scenario C-1: Try to refresh before giving up
        try {
          const refreshed = await authService.refreshToken();
          set({ accessToken: refreshed.data.accessToken });
          const retryResponse = await authService.testInterceptor(cachedEmail);
          set({ user: retryResponse.data, isAuthenticated: true, isHydrated: true });
          localStorage.setItem("accessToken", refreshed.data.accessToken);
          return;
        } catch {
          // Refresh also failed — fall through to purge
        }
      }

      // Scenario C-2: Token expired/malformed or refresh failed — purge session
      console.error("Session integrity verification failed. Clearing credentials.");
      localStorage.removeItem("accessToken");
      localStorage.removeItem("userEmail");
      set({
        user: null,
        accessToken: null,
        isAuthenticated: false,
        isHydrated: true, // Release mounting lock so App.tsx shows Login
      });
    }
  },

  updateAccessToken: (token) => {
    localStorage.setItem("accessToken", token);
    set({ accessToken: token });
  },
}));