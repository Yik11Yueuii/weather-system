# Weather System

个人天气微服务全栈项目，整合 Vue 3 前端与 Spring Cloud 后端。

## 目录

- `frontend/`：Vue 3、Vite、Element Plus 天气界面
- `backend/`：Spring Boot / Spring Cloud 微服务

## 天气服务

| 服务 | 端口 | 用途 |
| --- | ---: | --- |
| Eureka | 9001 | 服务注册中心 |
| Zuul | 6001 | 前端 API 网关 |
| Query Data | 9200 | 天气查询与 Redis 缓存 |
| City List | 9300 | 城市列表、MySQL 与 Redis |
| Cache Job | 9100 | 定时刷新天气缓存 |

## 环境要求

- JDK 8（项目基于 Spring Boot 2.2.6）
- Maven 3.6+
- Node.js 22.18+（以 `frontend/package.json` 为准）
- MySQL，数据库名 `weather`
- Redis，默认 `127.0.0.1:6379`

后端通过环境变量读取天行 API Key、数据库凭据和内部调用令牌。变量名及示例见 `backend/.env.example`。

## 启动顺序

1. 启动 MySQL 和 Redis。
2. 启动 `backend/eureka-server9001`。
3. 启动 `backend/hanshidemo-weather-citylist9300`。
4. 启动 `backend/hanshidemo-weather-querydata9200`。
5. 启动 `backend/hanshidemo-service-zuul6001`。
6. 可选：启动 `backend/hanshidemo-weather-cache9100`。
7. 进入 `frontend`，运行 `npm install` 和 `npm run dev`。

启动后端前至少需要配置 `TIANAPI_KEY` 和 `WEATHER_DB_PASSWORD`。环境变量示例见 `backend/.env.example`。

如需启用 AI 聊天组件，将 `frontend/.env.example` 复制为 `frontend/.env.local`，再填写自己的 Coze Bot ID 和 Token。不要提交 `.env.local`。

前端通过 `/weatherhanshi` 请求本地 Vite 代理，再由 Zuul `6001` 转发到天气微服务。

## GitHub

生成目录、依赖、环境变量文件和 IDE 文件已由根目录 `.gitignore` 排除。提交前请检查第三方 API 密钥、数据库密码和后端内部调用令牌等敏感信息。原前端中曾硬编码的 Coze Token 已从整合副本移除；为安全起见，应在 Coze 控制台撤销并重新生成该 Token。

原后端中曾硬编码的天行天气 API Key 也已从整合副本移除。该密钥应在天行数据控制台撤销并重新生成，之后仅通过 `TIANAPI_KEY` 环境变量配置。
