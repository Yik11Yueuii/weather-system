# 天气微服务项目运行指南

本项目推荐使用：

- IntelliJ IDEA：运行 Spring Boot 后端微服务
- VS Code：运行 Vue 3 前端
- MySQL：存储城市数据
- Redis：缓存城市和天气数据

项目根目录：`E:\myproject\weather-system`

## 一、运行前准备

### 1. 软件环境

请确认已安装：

- JDK 8：`E:\jdk\jdk1.8`
- Maven 3.6+
- Node.js 22.18+
- MySQL 8
- Redis
- IntelliJ IDEA
- VS Code

### 2. 数据库配置

城市服务当前使用以下配置：

```text
地址：localhost:3306
数据库：weather
用户名：root
密码：通过环境变量 `WEATHER_DB_PASSWORD` 配置
数据表：tab_city_copy
```

配置文件位置：

```text
backend/hanshidemo-weather-citylist9300/src/main/resources/application.yml
```

请确保 `weather` 数据库和 `tab_city_copy` 表已经存在，并且表中有城市数据。

### 后端环境变量

在 IDEA 的每个天气服务 Run Configuration 中打开 `Environment variables`，按需配置：

```text
TIANAPI_KEY=你的天行天气API密钥
WEATHER_DB_USERNAME=root
WEATHER_DB_PASSWORD=你的MySQL密码
WEATHER_INTERNAL_TOKEN=你自行生成的内部调用令牌
```

其中城市服务需要数据库变量，天气查询服务需要 `TIANAPI_KEY`。不要把真实值写回源码或提交到 Git。

建议的表结构：

```sql
CREATE DATABASE IF NOT EXISTS weather
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE weather;

CREATE TABLE IF NOT EXISTS tab_city_copy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cityid VARCHAR(32) NOT NULL,
    city VARCHAR(100) NOT NULL,
    father VARCHAR(100)
);
```

如果已有该表，不要重复创建或覆盖原数据。

### 3. 启动 MySQL

在 PowerShell 中检查 MySQL：

```powershell
Get-Service MySQL80
```

如果未运行：

```powershell
Start-Service MySQL80
```

### 4. 启动 Redis

在 PowerShell 中执行：

```powershell
& "D:\redis\redis-server.exe"
```

如果该路径不可用，可以执行：

```powershell
& "D:\Redis-x64-3.2.100_20250721_130816\redis-server.exe"
```

验证 Redis：

```powershell
& "D:\redis\redis-cli.exe" ping
```

返回 `PONG` 表示正常。

## 二、在 IntelliJ IDEA 中运行后端

### 1. 导入后端工程

1. 打开 IntelliJ IDEA。
2. 选择 `File -> Open`。
3. 选择目录：`E:\myproject\weather-system\backend`。
4. 选择 `Open as Project`。
5. 等待 IDEA 加载父 `pom.xml` 和 Maven 依赖。

不要将每个微服务分别作为互不相关的项目导入。应打开 `backend` 父工程。

### 2. 配置项目 JDK

打开：

```text
File -> Project Structure -> Project
```

设置：

```text
Project SDK：JDK 1.8（E:\jdk\jdk1.8）
Project language level：8
```

再打开：

```text
File -> Settings -> Build, Execution, Deployment
-> Build Tools -> Maven
```

设置：

```text
JDK for importer：JDK 1.8
Runner -> JRE：JDK 1.8
```

### 3. Maven 全局 JDK 21 配置注意事项

电脑上的 Maven 全局配置包含一个会强制使用 Java 21 的 `jdk-21` Profile。IDEA 如果出现以下错误：

```text
无效的目标发行版：21
```

在 IDEA 的 Maven Profiles 面板中取消勾选 `jdk-21`。

命令行编译时使用：

```powershell
cd E:\myproject\weather-system\backend
$env:JAVA_HOME = "E:\jdk\jdk1.8"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn.cmd '-P!jdk-21' clean install -DskipTests
```

天气系统不需要运行 `hanshidemo-service-provider-hystrix9500` 等用户服务示例模块。如果完整父工程被无关示例模块阻塞，可以只编译天气模块和网关。

### 4. 后端启动顺序

在 IDEA Project 窗口中找到对应启动类，右键选择 `Run`。

#### 第一步：Eureka 注册中心

启动类：

```text
backend/eureka-server9001/src/main/java/com/huawei/pro/EurekaServer9001Application.java
```

端口：`9001`

启动后访问：<http://localhost:9001>

#### 第二步：城市服务

启动类：

```text
backend/hanshidemo-weather-citylist9300/src/main/java/com/hanshi/weather/city/CityListApplication.java
```

端口：`9300`

该服务依赖 MySQL 和 Redis。

#### 第三步：天气查询服务

启动类：

```text
backend/hanshidemo-weather-querydata9200/src/main/java/com/hanshi/weather/query/QueryDataApplication.java
```

端口：`9200`

该服务依赖 Redis，并调用第三方天气 API。

#### 第四步：Zuul 网关

启动类：

```text
backend/hanshidemo-service-zuul6001/src/main/java/com/huawei/pro/HanshidemoServiceZuulApplication.java
```

端口：`6001`

#### 第五步：定时缓存服务（可选）

启动类：

```text
backend/hanshidemo-weather-cache9100/src/main/java/com/hanshi/weather/cache/CacheJobApplication.java
```

端口：`9100`

基础天气查询不需要启动该模块。需要定时为所有城市刷新天气缓存时再启动。

### 5. 检查 Eureka 注册状态

打开：<http://localhost:9001>

至少应看到：

```text
HANSHIDEMO-WEATHER-CITYLIST9300
HANSHIDEMO-WEATHER-QUERYDATA9200
HANSHIDEMO-SERVICE-ZUUL6001
```

## 三、在 VS Code 中运行前端

### 1. 打开前端工程

1. 打开 VS Code。
2. 选择 `File -> Open Folder`。
3. 打开：`E:\myproject\weather-system\frontend`。
4. 选择 `Terminal -> New Terminal`。

### 2. 安装依赖

首次运行或 `package.json` 发生变化后执行：

```powershell
npm.cmd install
```

### 3. 启动前端

```powershell
npm.cmd run dev
```

打开终端显示的地址，通常为：

<http://127.0.0.1:5173>

前端请求流程：

```text
Vue 5173
  -> Vite 本地代理
  -> Zuul 6001
  -> 城市服务 9300 / 天气查询服务 9200
```

## 四、最小启动清单

正常使用天气页面至少需要：

1. MySQL `3306`
2. Redis `6379`
3. Eureka `9001`
4. 城市服务 `9300`
5. 天气查询服务 `9200`
6. Zuul 网关 `6001`
7. Vue 前端 `5173`

缓存定时服务 `9100` 是可选项。

## 五、接口验证

### Eureka

```text
http://localhost:9001
```

### 通过网关获取城市列表

```text
http://localhost:6001/weatherhanshi/tabCity/CityList
```

### 查询天气

将城市代码替换为实际 `cityid`：

```text
http://localhost:6001/weatherhanshi/weather/101020100
```

### 前端

```text
http://127.0.0.1:5173
```

## 六、停止项目

### IDEA 中停止后端

在 IDEA 底部 `Run` 或 `Services` 窗口中，逐个点击红色停止按钮，停止：

- Eureka
- City List
- Query Data
- Zuul
- Cache Job（如果启动）

### VS Code 中停止前端

在运行 `npm.cmd run dev` 的终端按：

```text
Ctrl + C
```

### 停止 Redis

如果 Redis 在单独窗口运行，在该窗口按 `Ctrl + C`。

也可以执行：

```powershell
& "D:\redis\redis-cli.exe" shutdown
```

### 强制停止项目端口

仅在正常停止方式无效时使用：

```powershell
$ports = 5173, 6001, 9001, 9100, 9200, 9300, 6379

Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
  Where-Object { $_.LocalPort -in $ports } |
  Select-Object -ExpandProperty OwningProcess -Unique |
  ForEach-Object { Stop-Process -Id $_ -Force }
```

MySQL 是系统服务，不会被上述命令停止。如需停止：

```powershell
Stop-Service MySQL80
```

## 七、常见问题

### 1. 无效的目标发行版 21

原因：Maven 全局 `jdk-21` Profile 覆盖项目 Java 8 配置。

处理方法：

- IDEA Maven Profiles 中取消勾选 `jdk-21`。
- IDEA Project SDK 和 Maven Runner JRE 都选择 JDK 8。
- 命令行 Maven 增加 `'-P!jdk-21'`。

### 2. 城市列表为空或请求 500

检查：

- MySQL 是否启动。
- 数据库是否为 `weather`。
- `tab_city_copy` 是否存在并包含数据。
- 用户名和密码是否与 `application.yml` 一致。

### 3. Redis 连接失败

检查：

```powershell
Test-NetConnection 127.0.0.1 -Port 6379
```

或：

```powershell
& "D:\redis\redis-cli.exe" ping
```

### 4. 前端接口请求失败

依次确认：

1. Eureka `9001` 可访问。
2. Eureka 页面显示城市、查询和网关服务。
3. 网关 `6001` 已启动。
4. `http://localhost:6001/weatherhanshi/tabCity/CityList` 可访问。
5. 前端通过 `npm.cmd run dev` 启动，而不是直接双击 `index.html`。

### 5. 端口被占用

查看占用进程：

```powershell
Get-NetTCPConnection -LocalPort 9001 -State Listen |
  Select-Object LocalPort, OwningProcess
```

将 `9001` 替换为发生冲突的端口。

## 八、上传 GitHub 前的安全检查

不要上传真实密钥或本地密码。重点检查：

- 天行天气 API Key
- Coze Token 和 Bot ID
- MySQL 密码
- 后端内部调用 Token
- `.env`、`.env.local` 等环境变量文件

前端 Coze 配置应放入：

```text
frontend/.env.local
```

可以复制示例文件：

```powershell
Copy-Item frontend\.env.example frontend\.env.local
```

`.env.local` 已由 `.gitignore` 排除，不要强制提交。
