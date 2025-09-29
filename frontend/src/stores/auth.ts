import { defineStore } from "pinia";
import api from "../utils/api";

interface UserInfo {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  roles?: string[];
}

interface AuthState {
  token: string | null;
  user: UserInfo | null;
}

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    token: localStorage.getItem("token"),
    user: localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user") as string)
      : null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    async login(username: string, password: string) {
      const { data } = await api.post("/auth/signin", { username, password });
      this.token = data.token;
      this.user = {
        id: data.id,
        username: data.username,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        roles: data.roles,
      };
      localStorage.setItem("token", this.token as string);
      localStorage.setItem("user", JSON.stringify(this.user));
    },
    async register(payload: {
      username: string;
      email: string;
      password: string;
      firstName: string;
      lastName: string;
    }) {
      await api.post("/auth/signup", payload);
    },
    logout(router?: any) {
      this.token = null;
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      // Redirect to login page after logout
      if (router) {
        router.push("/login");
      } else {
        window.location.href = "/login";
      }
    },
  },
});
