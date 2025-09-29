<template>
  <div class="app">
    <header class="main-header">
      <div class="container header-content">
        <div class="brand-logo">
          <div class="logo-icon">🏨</div>
          <div class="brand-text">
            <h1 class="brand-name">Serenity</h1>
            <span class="brand-tagline">Hotel & Resort</span>
          </div>
        </div>
        <nav class="main-nav">
          <RouterLink to="/" class="nav-link" v-if="isAuthenticated"
            >Dashboard</RouterLink
          >
          <RouterLink to="/login" class="nav-link" v-if="!isAuthenticated"
            >Login</RouterLink
          >
          <button v-if="isAuthenticated" @click="logout" class="logout-btn">
            Logout
          </button>
        </nav>
      </div>
    </header>
    <main class="container">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from "./stores/auth";
import { storeToRefs } from "pinia";
import { RouterLink, RouterView, useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();
const { isAuthenticated } = storeToRefs(auth);
const logout = () => auth.logout(router);
</script>

<style scoped>
.main-header {
  background-color: var(--surface);
  border-bottom: 1px solid var(--border);
  padding: 1rem 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  transition: transform 0.2s ease;
}

.brand-logo:hover {
  transform: translateY(-1px);
}

.logo-icon {
  font-size: 2rem;
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(
    135deg,
    var(--primary) 0%,
    var(--primary-dark) 100%
  );
  border-radius: 50%;
  color: white;
  box-shadow: var(--shadow);
}

.brand-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.brand-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  line-height: 1;
  letter-spacing: -0.025em;
}

.brand-tagline {
  font-size: 0.75rem;
  color: var(--text-secondary);
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-top: 0.125rem;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link {
  padding: 0.5rem 1rem;
  border-radius: var(--radius-sm);
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.nav-link:hover {
  background-color: var(--accent);
  color: var(--text-primary);
}

.nav-link.router-link-active {
  background-color: var(--secondary);
  color: var(--text-primary);
}

.logout-btn {
  background-color: var(--primary);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-sm);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.logout-btn:hover {
  background-color: var(--primary-dark);
  transform: translateY(-1px);
}

.logout-btn:active {
  transform: translateY(0);
}

/* Responsive design */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }

  .brand-logo {
    gap: 0.5rem;
  }

  .logo-icon {
    font-size: 1.5rem;
    width: 2.5rem;
    height: 2.5rem;
  }

  .brand-name {
    font-size: 1.25rem;
  }

  .brand-tagline {
    font-size: 0.7rem;
  }

  .main-nav {
    flex-wrap: wrap;
    justify-content: center;
  }

  .nav-link,
  .logout-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .main-header {
    padding: 0.75rem 0;
  }

  .brand-logo {
    gap: 0.4rem;
  }

  .logo-icon {
    font-size: 1.25rem;
    width: 2rem;
    height: 2rem;
  }

  .brand-name {
    font-size: 1.125rem;
  }

  .brand-tagline {
    font-size: 0.65rem;
  }

  .main-nav {
    gap: 0.25rem;
  }

  .nav-link,
  .logout-btn {
    padding: 0.35rem 0.7rem;
    font-size: 0.8rem;
  }
}
</style>
