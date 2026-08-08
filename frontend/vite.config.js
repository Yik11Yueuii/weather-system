import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  base: './',
  server: {
    proxy: {
      '/weatherhanshi': {
        target: 'http://localhost:6001',
        changeOrigin: true,
      },
      '/coze-api': {
        target: 'https://api.coze.cn',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/coze-api/, ''),
      },
    },
  },
})
