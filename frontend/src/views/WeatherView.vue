<template>
  <div class="weather-app">
    <WeatherEffect :weather="currentWeather.weather" />
    <div class="bg-decoration">
      <div class="circle c1"></div>
      <div class="circle c2"></div>
    </div>

    <div class="weather-container">
      <!-- 卡片1: 主要天气 -->
      <div class="card hero-card">
        <header class="card-header">
          <div class="city-wrap">
            <h1>🌆 {{ currentWeather.city || '上海' }}</h1>
            <span class="switch-btn" @click="changeCity">切换</span>
          </div>
        </header>
        <div class="hero-body">
          <div class="hero-left">
            <img :src="getWeatherImage(currentWeather.weatherimg)" class="hero-icon" @error="handleImageError" />
            <div class="hero-temp">
              <span class="temp-low">{{ currentWeather.lowest?.replace('℃','') }}</span>
              <span class="temp-range">~</span>
              <span class="temp-high">{{ currentWeather.highest?.replace('℃','') }}</span>
              <span class="temp-unit">°C</span>
            </div>
            <div class="hero-status">{{ currentWeather.weather }}</div>
          </div>
          <div class="hero-right">
            <div class="meta-list">
              <div class="meta">{{ currentWeather.wind }} {{ currentWeather.windsc }}</div>
              <div class="meta aqi-dot">空气质量 · 优</div>
              <div class="meta">💧 湿度 {{ currentWeather.humidity }}%</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 卡片2: 7日预报 -->
      <div class="card forecast-card">
        <div class="card-title">📊 7日天气预报</div>
        <div class="forecast-list">
          <div v-for="(day, i) in forecastData" :key="i" class="forecast-day" :class="{ today: i===0 }">
            <span class="f-date">{{ formatDate(day.date) }}</span>
            <span class="f-week">{{ day.week }}</span>
            <img :src="getWeatherImage(day.weatherimg)" class="f-icon" @error="handleImageError" />
            <span class="f-desc">{{ day.weather }}</span>
            <span class="f-temps">{{ day.lowest?.replace('℃','') }}° ~ {{ day.highest?.replace('℃','') }}°</span>
          </div>
        </div>
      </div>

      <!-- 卡片3: 贴士 + 笑话 -->
      <div class="card tips-card">
        <div class="tips-row">
          <span class="tip">☂️ {{ currentWeather.pcpn > 0 ? '今日有雨，记得带伞' : '今日无雨，适宜出行' }}</span>
          <span class="tip">🧥 {{ currentWeather.tips?.substring(0, 15) || '体感舒适' }}...</span>
        </div>
        <div class="joke-line">
          <span class="joke-icon">😄</span>
          <span class="joke-text">{{ currentJoke }}</span>
          <button class="joke-refresh" @click="refreshJoke">换一个</button>
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="dialogFormVisible" title="请选择城市：" width="500">
    <el-form :model="form">
      <el-form-item label="城市" :label-width="100" label-position="right">
        <el-select v-model="form.cityid" placeholder="请选择一个城市">
          <el-option v-for="city in cityArr" :label="city.city" :value="city.cityid" :key="city.cityid" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="queryWeather(form.cityid)">查询天气</el-button>
      </div>
    </template>
  </el-dialog>

  <AiChatWidget :currentWeather="currentWeather" :forecastData="forecastData" />
</template>

<script>
import axios from "axios";
import { reactive, toRefs } from "vue";
import WeatherEffect from "@/components/WeatherEffect.vue";
import AiChatWidget from "@/components/AiChatWidget.vue";

export default {
  components: { WeatherEffect, AiChatWidget },
  setup() {
    const imageModules = import.meta.glob("@/assets/images/*.png", { eager: true });

    function getWeatherImage(imageName) {
      if (!imageName || imageName.startsWith("http") || imageName.startsWith("/")) return imageName || "/images/default.png";
      const m = imageModules[`/src/assets/images/${imageName}`];
      return m?.default || m || "/images/default.png";
    }
    function handleImageError(e) {
      e.target.style.display = 'none';
      const p = e.target.parentElement;
      if (p) { const s = document.createElement('span'); s.className = 'icon-emoji'; s.textContent = '🌤️'; s.style.fontSize = '30px'; p.appendChild(s); }
    }
    function formatDate(dateStr) {
      if (!dateStr) return '--/--';
      if (dateStr.includes('-') && dateStr.length <= 5) return dateStr;
      const parts = dateStr.split('-');
      return parts.length === 3 ? `${parts[1]}/${parts[2]}` : dateStr;
    }

    const jokeList = [
      '天气预报说今天有雨，结果下了个寂寞——原来是「局部」地区有雨。',
      '问：为什么气象局不准？答：因为天机不可泄露！',
      '下雨天，最适合睡觉，因为「雨」你无关。',
      '气象局说今天有台风，我赶紧去买了袋盐，怕它不咸（嫌）风大。',
      '天气预报就像爱情，明明说要来，结果放鸽子。'
    ];

    const state = reactive({
      forecastData: [],
      currentWeather: {},
      currentJoke: jokeList[0],
      jokeList,
      dialogFormVisible: false,
      form: { cityid: '' },
      formLabelWidth: '100px',
      cityArr: []
    });

    function refreshJoke() {
      const ci = state.jokeList.indexOf(state.currentJoke);
      let ni = Math.floor(Math.random() * state.jokeList.length);
      while (ni === ci && state.jokeList.length > 1) ni = Math.floor(Math.random() * state.jokeList.length);
      state.currentJoke = state.jokeList[ni];
    }
    function changeCity() { state.dialogFormVisible = true; }
    function queryWeather(cityid) {
      if (!cityid) { alert('请先选择一个城市'); return; }
      state.dialogFormVisible = false;
      loadWeather(cityid);
    }
    function loadCityArr() {
      axios.get("/weatherhanshi/tabCity/CityList").then(r => { state.cityArr = r.data; });
    }
    function loadWeather(cityId) {
      const cityCode = cityId || '101020100';
      axios.get("/weatherhanshi/weather/" + cityCode).then(r => {
        const result = r.data.result;
        if (result && result.list) {
          state.forecastData = result.list;
          state.currentWeather = state.forecastData[0];
          state.currentWeather.city = result.area || '上海';
          state.currentWeather.date = state.currentWeather.date.substring(0, 10);
        }
      }).catch(() => setDefaultData());
    }
    function setDefaultData() {
      const dl = [
        { date: '2026-07-24', week: '星期五', weather: '多云', weatherimg: 'duoyun.png', real: '32℃', lowest: '29℃', highest: '37℃', wind: '东南风', windsc: '1-3级', humidity: '78', pcpn: '0.0', tips: '天气极热，适宜着短衣短裤等夏季服装。' }
      ];
      state.forecastData = dl;
      state.currentWeather = dl[0];
      state.currentWeather.city = '上海';
      state.currentWeather.date = dl[0].date;
    }

    loadWeather();
    loadCityArr();

    return { ...toRefs(state), loadWeather, getWeatherImage, handleImageError, formatDate, refreshJoke, changeCity, loadCityArr, queryWeather };
  },
};
</script>

<style scoped>
* { margin: 0; padding: 0; box-sizing: border-box; }

.weather-app {
  min-height: 100vh;
  padding: 32px 24px;
  background: linear-gradient(160deg, #f5f0eb 0%, #e8eaf6 25%, #fce4ec 50%, #e3f2fd 75%, #f1f8e9 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", sans-serif;
}
.bg-decoration { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.circle { position: absolute; border-radius: 50%; }
.c1 { width: 400px; height: 400px; top: -80px; right: -60px; background: radial-gradient(circle, rgba(149,117,205,0.08), transparent 70%); }
.c2 { width: 300px; height: 300px; bottom: -60px; left: -60px; background: radial-gradient(circle, rgba(255,183,77,0.08), transparent 70%); }

.weather-container {
  max-width: 720px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* 卡片通用 */
.card {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 24px;
  padding: 24px 28px;
  margin-bottom: 18px;
  border: 1px solid rgba(255,255,255,0.8);
  box-shadow: 0 2px 16px rgba(0,0,0,0.04);
}

/* 主卡片 */
.hero-card { padding-bottom: 20px; }
.card-header { margin-bottom: 16px; }
.city-wrap { display: flex; align-items: center; gap: 14px; }
.city-wrap h1 { font-size: 22px; font-weight: 700; color: #3e3562; }
.switch-btn {
  font-size: 12px; color: #7c6faa; background: rgba(124,111,170,0.08);
  padding: 3px 14px; border-radius: 20px; cursor: pointer;
}
.switch-btn:hover { background: rgba(124,111,170,0.16); }

.hero-body { display: flex; align-items: center; gap: 32px; flex-wrap: wrap; }
.hero-left { display: flex; align-items: center; gap: 16px; }
.hero-icon { width: 64px; height: 64px; object-fit: contain; }
.hero-temp { display: flex; align-items: baseline; gap: 3px; }
.temp-low { font-size: 36px; font-weight: 400; color: #5c7da8; }
.temp-range { font-size: 22px; color: #c0c0c0; margin: 0 4px; }
.temp-high { font-size: 44px; font-weight: 350; color: #e85d3a; }
.temp-unit { font-size: 18px; color: #999; }
.hero-status { font-size: 16px; color: #5c4d7a; background: rgba(124,111,170,0.08); padding: 2px 16px; border-radius: 16px; font-weight: 500; }
.hero-right { flex: 1; min-width: 180px; }
.meta-list { display: flex; flex-direction: column; gap: 10px; }
.meta { font-size: 14px; color: #6b7280; }
.aqi-dot::before { content: '● '; color: #81c784; }

/* 预报卡片 */
.card-title { font-size: 15px; font-weight: 700; color: #3e3562; margin-bottom: 16px; }
.forecast-list { display: flex; gap: 8px; overflow-x: auto; padding-bottom: 4px; }
.forecast-day {
  flex: 0 0 85px; text-align: center; padding: 12px 6px; border-radius: 16px;
  background: rgba(255,255,255,0.4); display: flex; flex-direction: column; gap: 4px; align-items: center;
}
.forecast-day.today { background: rgba(124,111,170,0.12); box-shadow: 0 2px 12px rgba(124,111,170,0.1); }
.f-date { font-size: 12px; color: #999; font-weight: 500; }
.f-week { font-size: 11px; color: #aaa; }
.f-icon { width: 28px; height: 28px; object-fit: contain; }
.f-desc { font-size: 12px; color: #666; font-weight: 500; }
.f-temps { font-size: 11px; color: #888; }
.forecast-day.today .f-date, .forecast-day.today .f-desc { color: #5c4d7a; font-weight: 700; }

/* 贴士 + 笑话 */
.tips-row { display: flex; gap: 12px; margin-bottom: 14px; flex-wrap: wrap; }
.tip { font-size: 14px; color: #555; background: rgba(255,255,255,0.6); padding: 8px 16px; border-radius: 20px; }
.joke-line { display: flex; align-items: center; gap: 10px; padding-top: 12px; border-top: 1px solid rgba(0,0,0,0.04); }
.joke-icon { font-size: 18px; }
.joke-text { flex: 1; font-size: 14px; color: #777; }
.joke-refresh { font-size: 12px; color: #7c6faa; background: none; border: none; cursor: pointer; opacity: 0.7; }
.joke-refresh:hover { opacity: 1; }

@media (max-width: 640px) {
  .weather-app { padding: 16px 10px; }
  .card { padding: 16px 14px; border-radius: 18px; }
  .hero-body { flex-direction: column; align-items: flex-start; }
  .hero-icon { width: 48px; height: 48px; }
  .temp-low { font-size: 28px; }
  .temp-high { font-size: 34px; }
  .forecast-list { gap: 4px; }
  .forecast-day { flex: 0 0 68px; padding: 8px 4px; }
}
</style>
