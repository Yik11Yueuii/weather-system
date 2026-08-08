<template>
  <!-- 悬浮按钮 -->
  <div class="ai-float-btn" @click="toggleChat" :class="{ active: visible }">
    <span v-if="!visible">🤖</span>
    <span v-else>✕</span>
  </div>

  <!-- 聊天面板 -->
  <div class="ai-chat-panel" v-show="visible">
    <div class="chat-header">
      <span>🌦️ AI 天气小助手</span>
      <span class="chat-subtitle">Powered by Coze</span>
    </div>

    <div class="chat-messages" ref="msgBox">
      <div class="msg bot">
        👋 你好！我是天气小顾问，可以问我关于天气的任何问题～
      </div>
      <div v-for="(m, i) in messages" :key="i" :class="'msg ' + m.role">
        {{ m.content }}
      </div>
      <div v-if="loading" class="msg bot typing">思考中…</div>
    </div>

    <!-- 快捷提问 -->
    <div class="quick-asks">
      <button v-for="q in quickQuestions" :key="q" @click="quickSend(q)">{{ q }}</button>
    </div>

    <div class="chat-input">
      <input v-model="input" @keyup.enter="send" placeholder="输入问题…" :disabled="loading" />
      <button @click="send" :disabled="loading || !input.trim()">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'

const props = defineProps({
  currentWeather: { type: Object, default: () => ({}) },
  forecastData: { type: Array, default: () => [] }
})

const visible = ref(false)
const input = ref('')
const messages = ref([])
const loading = ref(false)
const msgBox = ref(null)

// Coze 配置（演示前替换为你自己的Bot ID和Token）
const COZE_CONFIG = {
  botId: import.meta.env.VITE_COZE_BOT_ID || '',
  token: import.meta.env.VITE_COZE_TOKEN || '',
  userId: 'weather-demo-user'
}

// 把当前天气数据拼成一段上下文文本
const weatherContext = computed(() => {
  const w = props.currentWeather
  const list = props.forecastData
  if (!w || !w.city) return ''
  let ctx = `【当前天气数据】城市:${w.city}，天气:${w.weather}，`
  ctx += `温度:${w.lowest}~${w.highest}，风力:${w.wind}${w.windsc}，`
  ctx += `湿度:${w.humidity}%，紫外线:${w.uv_index}，降雨:${w.pcpn}mm，`
  ctx += `穿衣建议:${w.tips}`
  if (list && list.length > 1) {
    ctx += ` | 【未来预报】`
    list.slice(1, 4).forEach((d, i) => {
      ctx += `第${i+2}天(${d.week}):${d.weather},${d.lowest}~${d.highest}; `
    })
  }
  return ctx
})

const quickQuestions = [
  '今天适合出去玩吗？',
  '明天天气怎么样？',
  '出门穿什么衣服？',
  '这个周末适合旅游吗？'
]

function toggleChat() {
  visible.value = !visible.value
}

function quickSend(q) {
  messages.value.push({ role: 'user', content: q })
  input.value = ''
  autoReply(q)
}

// ---------- 智能回复：Coze在线优先，失败则本地回答 ----------
async function autoReply(msg) {
  // 先尝试Coze
  const cozeOk = await tryCoze(msg)
  // Coze失败就用本地逻辑生成回答
  if (!cozeOk) {
    const reply = localReply(msg)
    messages.value.push({ role: 'bot', content: reply })
  }
}

function localReply(msg) {
  const w = props.currentWeather
  const list = props.forecastData
  if (!w || !w.city) return '我还没拿到天气数据哦，等页面加载完再问我吧～'

  const city = w.city; const weather = w.weather
  const low = w.lowest?.replace('℃','') || '--'; const high = w.highest?.replace('℃','') || '--'
  const wind = (w.wind||'') + (w.windsc||''); const uv = w.uv_index || '--'
  const pcpn = parseFloat(w.pcpn) || 0
  const tomorrow = list && list[1]

  if (msg.includes('穿') || msg.includes('衣服')) {
    const t = parseInt(low) || 25
    if (t > 30) return `${city}今天${weather}，${low}°~${high}°，非常热！短袖短裤+防晒衣，帽子墨镜防晒霜安排上 🕶️`
    if (t > 20) return `${city}今天${weather}，${low}°~${high}°，体感舒适。短袖+薄长裤刚好，怕空调冷带件薄衬衫～`
    if (t > 10) return `${city}今天${weather}，${low}°~${high}°，微凉。长袖+薄外套，洋葱穿搭法最稳 🧥`
    return `${city}今天${weather}，${low}°~${high}°，偏冷。厚外套+毛衣，围巾帽子看风大小～`
  }
  if (msg.includes('玩') || msg.includes('出门') || msg.includes('出去') || msg.includes('适合')) {
    if (pcpn > 0) return `${city}今天${weather}，有降雨，不太适合户外活动。非要出门记得带伞！☔`
    if (parseInt(uv) > 8) return `${city}今天${weather}，紫外线${uv}偏高。建议上午10点前或下午4点后出门 ☀️`
    if (parseInt(high) > 35) return `${city}今天${weather}，最高${high}°，热得冒烟！室内活动优先，多喝水防中暑 🥵`
    return `${city}今天${weather}，${low}°~${high}°，天气不错适合出门。${tomorrow ? '明天'+tomorrow.weather+'也可以期待～' : ''} 🎉`
  }
  if (msg.includes('明') || msg.includes('未来') || msg.includes('周末')) {
    return tomorrow
      ? `${city}明天${tomorrow.week}：${tomorrow.weather}，${tomorrow.lowest}~${tomorrow.highest}，风力${tomorrow.wind}。${tomorrow.weather.includes('雨')?'记得带伞！':'天气可以～'}`
      : `${city}未来几天：${list.slice(1,5).map(d=>d.week+d.weather).join('、')}`
  }
  if (msg.includes('旅游') || msg.includes('旅行')) {
    return `${city}当前${weather}，${low}°~${high}°。${parseInt(high)>33?'有点热，建议去山里或水边避暑～':'温度适中，周边一日游不错！'} 🗺️`
  }
  return `${city}当前${weather}，${low}°~${high}°，风力${wind}。${pcpn>0?'有降雨带伞。':'天气还行～'} ${parseInt(high)>33?'注意防暑！':''}`
}

async function tryCoze(msg) {
  loading.value = true
  await nextTick()
  scrollBottom()

  const fullQuery = weatherContext.value
    ? `${weatherContext.value}\n\n用户提问：${msg}`
    : msg

  try {
    const res = await fetch('/coze-api/v1/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${COZE_CONFIG.token}`
      },
      body: JSON.stringify({
        bot_id: COZE_CONFIG.botId,
        user: COZE_CONFIG.userId,
        query: fullQuery,
        stream: false
      })
    })

    const data = await res.json()
    console.log('Coze返回:', JSON.stringify(data))

    let replyContent = null
    if (data?.messages) {
      const answer = data.messages.find(m => m.type === 'answer' || m.role === 'assistant')
      if (answer) replyContent = answer.content
    }
    if (!replyContent && data?.data?.content) replyContent = data.data.content
    if (!replyContent && data?.content) replyContent = data.content

    if (replyContent) {
      messages.value.push({ role: 'bot', content: replyContent })
      loading.value = false
      await nextTick()
      scrollBottom()
      return true
    }
  } catch (e) {
    console.log('Coze未连通，使用本地模式:', e.message)
  }

  loading.value = false
  await nextTick()
  scrollBottom()
  return false
}

async function send() {
  const msg = input.value.trim()
  if (!msg || loading.value) return
  messages.value.push({ role: 'user', content: msg })
  input.value = ''
  autoReply(msg)
}

function scrollBottom() {
  if (msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight
}
</script>

<style scoped>
.ai-float-btn {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  z-index: 1000;
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  user-select: none;
}
.ai-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.5);
}
.ai-float-btn.active {
  background: #e74c3c;
}

.ai-chat-panel {
  position: fixed;
  bottom: 96px;
  right: 24px;
  width: 360px;
  height: 480px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 999;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.chat-header {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  padding: 16px 20px;
  font-weight: 700;
  font-size: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chat-subtitle {
  font-size: 11px;
  font-weight: 400;
  opacity: 0.7;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.msg {
  max-width: 85%;
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.msg.user {
  align-self: flex-end;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.msg.bot {
  align-self: flex-start;
  background: #f0f2f5;
  color: #333;
  border-bottom-left-radius: 4px;
}
.msg.typing {
  color: #999;
}

.quick-asks {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 16px;
}
.quick-asks button {
  font-size: 12px;
  background: rgba(102, 126, 234, 0.08);
  color: #667eea;
  border: 1px solid rgba(102, 126, 234, 0.15);
  border-radius: 16px;
  padding: 6px 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}
.quick-asks button:hover {
  background: rgba(102, 126, 234, 0.18);
  border-color: rgba(102, 126, 234, 0.3);
}

.chat-input {
  display: flex;
  padding: 12px 16px;
  gap: 8px;
  border-top: 1px solid #eee;
}
.chat-input input {
  flex: 1;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  outline: none;
}
.chat-input input:focus {
  border-color: #667eea;
}
.chat-input button {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 8px 18px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}
.chat-input button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
