import { createRouter, createWebHistory } from 'vue-router'
import WeatherView from '@/views/WeatherView.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'weather',
      component: WeatherView,
    },
  ],
})

export default router
