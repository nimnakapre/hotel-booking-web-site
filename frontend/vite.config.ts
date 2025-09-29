import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
  },
  publicDir: "public",
  assetsInclude: ["**/*.jpg", "**/*.jpeg", "**/*.png", "**/*.gif", "**/*.webp"],
});
