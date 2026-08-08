<template>
  <div class="weather-effect-layer">
    <!-- 雨滴 -->
    <template v-if="effectType === 'rain'">
      <div v-for="i in 60" :key="'r'+i" class="raindrop"
        :style="{ left: Math.random()*100+'%', animationDelay: Math.random()*2+'s', animationDuration: (0.5+Math.random()*0.8)+'s' }" />
    </template>

    <!-- 雪花 -->
    <template v-if="effectType === 'snow'">
      <div v-for="i in 40" :key="'s'+i" class="snowflake"
        :style="{ left: Math.random()*100+'%', animationDelay: Math.random()*4+'s', animationDuration: (3+Math.random()*4)+'s', fontSize: (8+Math.random()*12)+'px' }">
        ❄
      </div>
    </template>

    <!-- 阳光光晕 -->
    <template v-if="effectType === 'sun'">
      <div v-for="i in 30" :key="'u'+i" class="sun-particle"
        :style="{ left: (20+Math.random()*60)+'%', top: (5+Math.random()*40)+'%', animationDelay: Math.random()*3+'s', animationDuration: (2+Math.random()*3)+'s' }" />
    </template>

    <!-- 云朵 -->
    <template v-if="effectType === 'cloud'">
      <div v-for="i in 5" :key="'c'+i" class="cloud"
        :style="{ top: (5+i*15+Math.random()*10)+'%', animationDelay: Math.random()*8+'s', animationDuration: (12+Math.random()*10)+'s' }" />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  weather: { type: String, default: '多云' }
})

const effectType = computed(() => {
  if (!props.weather) return 'cloud'
  // TODO: 演示完后删掉下面这行，恢复正常逻辑
  // return 'rain' // 临时测试：强制显示雨滴效果
  if (props.weather.includes('雪')) return 'snow'
  if (props.weather.includes('雨')) return 'rain'
  if (props.weather.includes('晴')) return 'sun'
  if (props.weather.includes('云') || props.weather.includes('阴')) return 'cloud'
  return 'cloud'
})
</script>

<style scoped>
.weather-effect-layer {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

/* ===== 雨滴 ===== */
.raindrop {
  position: absolute;
  top: -20px;
  width: 2px;
  height: 16px;
  background: linear-gradient(transparent, rgba(160, 196, 232, 0.5));
  border-radius: 1px;
  animation: rain-fall linear infinite;
}
@keyframes rain-fall {
  0%   { transform: translateY(0); opacity: 0; }
  10%  { opacity: 1; }
  100% { transform: translateY(105vh); opacity: 0; }
}

/* ===== 雪花 ===== */
.snowflake {
  position: absolute;
  top: -30px;
  opacity: 0.7;
  animation: snow-fall linear infinite;
}
@keyframes snow-fall {
  0%   { transform: translateY(0) translateX(0) rotate(0deg); }
  50%  { transform: translateY(50vh) translateX(30px) rotate(180deg); }
  100% { transform: translateY(105vh) translateX(-20px) rotate(360deg); }
}

/* ===== 阳光粒子 ===== */
.sun-particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 224, 130, 0.6);
  border-radius: 50%;
  animation: sun-float ease-in-out infinite;
  box-shadow: 0 0 8px rgba(255, 224, 130, 0.3);
}
@keyframes sun-float {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.6; }
  50%      { transform: translateY(-20px) scale(1.5); opacity: 0.2; }
}

/* ===== 云朵飘动 ===== */
.cloud {
  position: absolute;
  left: -150px;
  width: 140px;
  height: 55px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 50px;
  animation: cloud-drift linear infinite;
  box-shadow: 0 4px 40px rgba(255, 255, 255, 0.5);
}
.cloud::before {
  content: '';
  position: absolute;
  width: 50px; height: 50px;
  background: inherit; border-radius: 50%;
  top: -25px; left: 20px;
}
.cloud::after {
  content: '';
  position: absolute;
  width: 40px; height: 40px;
  background: inherit; border-radius: 50%;
  top: -15px; left: 50px;
}
@keyframes cloud-drift {
  0%   { transform: translateX(0); }
  100% { transform: translateX(calc(100vw + 200px)); }
}
</style>
