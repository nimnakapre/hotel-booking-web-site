import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Dashboard from "../views/Dashboard.vue";
import NewBooking from "../views/NewBooking.vue";
import { useAuthStore } from "../stores/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "dashboard",
      component: Dashboard,
      meta: { requiresAuth: true },
    },
    { path: "/login", name: "login", component: Login },
    { path: "/register", name: "register", component: Register },
    {
      path: "/dashboard",
      name: "dashboard-alt",
      component: Dashboard,
      meta: { requiresAuth: true },
    },
    {
      path: "/bookings/new",
      name: "new-booking",
      component: NewBooking,
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: "login", query: { redirect: to.fullPath } };
  }
});

export default router;
