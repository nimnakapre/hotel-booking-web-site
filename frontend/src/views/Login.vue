<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>Welcome Back</h1>
        <p>Sign in to your account</p>
      </div>

      <form @submit.prevent="onSubmit" class="login-form">
        <div class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="Enter your username"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Enter your password"
            required
          />
        </div>

        <button
          type="submit"
          class="login-button"
          :disabled="!username || !password"
        >
          <span v-if="!isLoading">Sign In</span>
          <span v-else>Signing In...</span>
        </button>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </form>

      <div class="login-footer">
        <p>
          Don't have an account?
          <RouterLink to="/register" class="register-link"
            >Create one</RouterLink
          >
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "../stores/auth";
import { useRouter, RouterLink, useRoute } from "vue-router";

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const username = ref("");
const password = ref("");
const error = ref("");
const isLoading = ref(false);

async function onSubmit() {
  error.value = "";
  isLoading.value = true;

  try {
    await auth.login(username.value, password.value);
    router.push((route.query.redirect as string) || "/dashboard");
  } catch (e: any) {
    error.value = e?.response?.data || "Login failed";
  } finally {
    isLoading.value = false;
  }
}
</script>
