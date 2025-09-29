<template>
  <div class="register-container">
    <div class="register-form-wrapper">
      <div class="register-header">
        <h1 style="color: #dadada">Create Your Account</h1>
        <p>Join us today and start booking amazing hotel stays</p>
      </div>

      <div class="register-form-container">
        <form @submit.prevent="onSubmit" class="register-form">
          <div class="form-section">
            <h3>Personal Information</h3>
            <div class="form-row">
              <div class="form-group">
                <label for="firstName">First Name</label>
                <input
                  id="firstName"
                  v-model="firstName"
                  type="text"
                  placeholder="Enter your first name"
                  required
                />
              </div>
              <div class="form-group">
                <label for="lastName">Last Name</label>
                <input
                  id="lastName"
                  v-model="lastName"
                  type="text"
                  placeholder="Enter your last name"
                  required
                />
              </div>
            </div>
          </div>

          <div class="form-section">
            <h3>Account Details</h3>
            <div class="form-row">
              <div class="form-group">
                <label for="username">Username</label>
                <input
                  id="username"
                  v-model="username"
                  type="text"
                  placeholder="Choose a unique username"
                  @input="validateUsername"
                  required
                />
                <div v-if="usernameError" class="field-error">
                  {{ usernameError }}
                </div>
                <div v-if="isCheckingUsername" class="field-checking">
                  Checking availability...
                </div>
              </div>
              <div class="form-group">
                <label for="email">Email Address</label>
                <input
                  id="email"
                  v-model="email"
                  type="email"
                  placeholder="Enter your email address"
                  @input="validateEmail"
                  required
                />
                <div v-if="emailFormatError" class="field-error">
                  {{ emailFormatError }}
                </div>
                <div v-if="emailError" class="field-error">
                  {{ emailError }}
                </div>
                <div v-if="isCheckingEmail" class="field-checking">
                  Checking availability...
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <h3>Security</h3>
            <div class="form-row">
              <div class="form-group">
                <label for="password">Password</label>
                <div class="password-input">
                  <input
                    id="password"
                    v-model="password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="Create a strong password"
                    required
                  />
                  <button
                    type="button"
                    @click="togglePassword"
                    class="password-toggle"
                  >
                    {{ showPassword ? "👁️" : "👁️‍🗨️" }}
                  </button>
                </div>
              </div>
              <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <div class="password-input">
                  <input
                    id="confirmPassword"
                    v-model="confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    placeholder="Confirm your password"
                    required
                  />
                  <button
                    type="button"
                    @click="toggleConfirmPassword"
                    class="password-toggle"
                  >
                    {{ showConfirmPassword ? "👁️" : "👁️‍🗨️" }}
                  </button>
                </div>
                <div v-if="passwordError" class="field-error">
                  {{ passwordError }}
                </div>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button
              type="submit"
              class="register-button"
              :disabled="!isFormValid || isLoading"
            >
              <span v-if="!isLoading">Create Account</span>
              <span v-else>Creating Account...</span>
            </button>

            <div
              v-if="message"
              class="message"
              :class="{
                'error-message':
                  message.includes('failed') || message.includes('error'),
              }"
            >
              {{ message }}
            </div>
          </div>
        </form>

        <div class="register-footer">
          <p>
            Already have an account?
            <RouterLink to="/login" class="login-link">Sign in here</RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useAuthStore } from "../stores/auth";
import { RouterLink, useRouter } from "vue-router";
import api from "../utils/api";

const auth = useAuthStore();
const router = useRouter();
const firstName = ref("");
const lastName = ref("");
const username = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const message = ref("");
const isLoading = ref(false);
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const usernameError = ref("");
const emailError = ref("");
const isCheckingUsername = ref(false);
const isCheckingEmail = ref(false);

// Email format validation
const emailFormatError = computed(() => {
  if (!email.value) return "";
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email.value)
    ? ""
    : "Please enter a valid email address";
});

// Password validation
const passwordError = computed(() => {
  if (!confirmPassword.value) return "";
  return password.value === confirmPassword.value
    ? ""
    : "Passwords do not match";
});

// Form validation
const isFormValid = computed(() => {
  return (
    firstName.value &&
    lastName.value &&
    username.value &&
    email.value &&
    password.value &&
    confirmPassword.value &&
    !emailFormatError.value &&
    !passwordError.value &&
    !usernameError.value &&
    !emailError.value &&
    !isCheckingUsername.value &&
    !isCheckingEmail.value
  );
});

function togglePassword() {
  showPassword.value = !showPassword.value;
}

function toggleConfirmPassword() {
  showConfirmPassword.value = !showConfirmPassword.value;
}

// Real-time validation functions
let usernameTimeout: number | null = null;
let emailTimeout: number | null = null;

async function checkUsernameAvailability() {
  if (!username.value || username.value.length < 3) {
    usernameError.value = "";
    return;
  }

  isCheckingUsername.value = true;
  usernameError.value = "";

  try {
    const { data } = await api.get("/auth/check-username", {
      params: { username: username.value },
    });

    if (!data.available) {
      usernameError.value = "Username is already taken";
    }
  } catch (error) {
    console.error("Error checking username:", error);
    usernameError.value = "Error checking username availability";
  } finally {
    isCheckingUsername.value = false;
  }
}

async function checkEmailAvailability() {
  if (!email.value || emailFormatError.value) {
    emailError.value = "";
    return;
  }

  isCheckingEmail.value = true;
  emailError.value = "";

  try {
    const { data } = await api.get("/auth/check-email", {
      params: { email: email.value },
    });

    if (!data.available) {
      emailError.value = "Email is already in use";
    }
  } catch (error) {
    console.error("Error checking email:", error);
    emailError.value = "Error checking email availability";
  } finally {
    isCheckingEmail.value = false;
  }
}

// Debounced validation functions
function validateUsername() {
  if (usernameTimeout) {
    clearTimeout(usernameTimeout);
  }
  usernameTimeout = setTimeout(checkUsernameAvailability, 500);
}

function validateEmail() {
  if (emailTimeout) {
    clearTimeout(emailTimeout);
  }
  emailTimeout = setTimeout(checkEmailAvailability, 500);
}

async function onSubmit() {
  message.value = "";
  isLoading.value = true;

  if (password.value !== confirmPassword.value) {
    message.value = "Passwords do not match";
    isLoading.value = false;
    return;
  }

  try {
    await auth.register({
      username: username.value,
      email: email.value,
      password: password.value,
      firstName: firstName.value,
      lastName: lastName.value,
    });
    message.value = "Account created successfully! Redirecting to login...";
    setTimeout(() => router.push("/login"), 2000);
  } catch (e: any) {
    message.value = e?.response?.data || "Registration failed";
  } finally {
    isLoading.value = false;
  }
}
</script>
