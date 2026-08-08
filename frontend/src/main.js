import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'//导入的其实是js文件 名字叫index.js
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)//这已经是一个页面了

app.use(router)//使用路由（配置路由的意思
app.use(ElementPlus)

app.mount('#app')//把这个页面挂载到 id叫做App的div
