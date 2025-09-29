<template>
  <section class="card" style="max-width: 480px">
    <h2>Create account</h2>
    <form @submit.prevent="onSubmit">
      <div class="row">
        <div>
          <label>First name</label>
          <input v-model="firstName" />
        </div>
        <div>
          <label>Last name</label>
          <input v-model="lastName" />
        </div>
      </div>
      <div class="row">
        <div>
          <label>Username</label>
          <input v-model="username" />
        </div>
        <div>
          <label>Email</label>
          <input type="email" v-model="email" />
        </div>
      </div>
      <div>
        <label>Password</label>
        <input type="password" v-model="password" />
      </div>
      <div class="actions">
        <button type="submit">Register</button>
        <RouterLink to="/login">Back to login</RouterLink>
      </div>
      <p v-if="message">{{ message }}</p>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "../stores/auth";
import { RouterLink, useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();
const firstName = ref("");
const lastName = ref("");
const username = ref("");
const email = ref("");
const password = ref("");
const message = ref("");

async function onSubmit() {
  message.value = "";
  try {
    await auth.register({
      username: username.value,
      email: email.value,
      password: password.value,
      firstName: firstName.value,
      lastName: lastName.value,
    });
    message.value = "Registered. You can now login.";
    setTimeout(() => router.push("/login"), 700);
  } catch (e: any) {
    message.value = e?.response?.data || "Registration failed";
  }
}
</script>
