# 质量分层系统（Quality Layer System）

面向质量/测试部门的需求管理平台，按区域分层管理测试需求，覆盖需求台账维护、Excel 批量导入导出、多维统计分析、用户与角色权限管理，前端采用 Vue3 + Element Plus，后端采用 Spring Boot + MyBatis。

## 功能特性

- **需求管理**：需求分页查询、新增、编辑、删除、详情查看（区域 / 各负责人 / 任务名称 / 触发项 / 结论 / 截止日期 / 状态）
- **Excel 导入导出**：批量导入需求（EasyExcel 文件上传）、按条件导出 Excel、下载导入模板
- **统计分析**：按区域统计、月度大盘折线图、首页汇总统计、多维度分组统计、最近需求列表（ECharts 可视化）
- **系统管理**：用户管理、角色管理、区域管理、操作日志
- **认证权限**：JWT 登录鉴权 + Redis 会话管理（登出/踢人即时失效）、BCrypt 密码加密、测试环境免密登录、管理员角色路由拦截、操作日志自动记录
- **Redis 缓存**：登录用户信息缓存（拦截器免查库）、需求详情缓存、首页统计缓存（Cache Aside 模式，写操作自动失效）

## 技术栈

### 前端（quality_layer_frontend）
- Vue 3.4.21 + Vite 5.1.6 + Element Plus 2.6.0 + Pinia 2.1.7 + Vue Router 4.3.0
- Axios 1.6.7 请求封装（含 JWT 拦截）
- ECharts 5.5.0 数据可视化 + dayjs 时间处理

### 后端（quality-layer-backend）
- Spring Boot 4.0.7 + Java 17 + Maven
- MyBatis 4.0.1（XML 方式）+ MySQL 8.0 + PageHelper 1.4.6 分页
- Spring Data Redis（Lettuce 客户端）+ StringRedisTemplate + Hutool JSONUtil（登录会话与业务缓存，JSON 序列化，无 Jackson 版本冲突）
- JJWT 0.9.1 登录鉴权 + BCrypt 密码加密（spring-security-crypto）
- EasyExcel 3.3.2 批量导入导出
- Hutool 5.8.40 工具库 + Lombok + Validation 参数校验

## 目录结构

```
quality-layer-system/
├─ quality_layer_frontend/   # 前端工程（Vue3 + Vite + Element Plus）
├─ quality-layer-backend/    # 后端工程（Spring Boot + MyBatis + Redis）
├─ quality_layer.sql         # 数据库初始化脚本
└─ .gitignore
```

## 环境要求

- Node.js ≥ 16
- JDK 17
- MySQL 8.0
- Redis 6.0+（默认连接 127.0.0.1:6379，可无密码）
- Maven 3.6+

## 快速启动

### 1. 启动 Redis

确保本机 Redis 已运行：

```bash
redis-cli ping
```

返回 `PONG` 即正常。Windows 下若注册为系统服务，可用管理员终端执行 `net start Redis` 启动。

### 2. 初始化数据库

创建数据库并导入脚本（脚本会自动创建 `quality_layer` 库及全部表，编码 UTF-8）：

```bash
mysql -uroot -p < quality_layer.sql
```

### 3. 启动后端

1. 修改 `quality-layer-backend/src/main/resources/application.yml` 中的数据库连接信息：
   - 密码已改为占位符 `xxx`，请填入自己的数据库密码，或通过环境变量注入：`SPRING_DATASOURCE_PASSWORD`
2. 确认 Redis 配置（`spring.data.redis.host` / `port` / `password`）与本地一致
3. 启动服务，默认端口 **8081**：

```bash
cd quality-layer-backend
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd quality_layer_frontend
npm install
npm run dev
```

浏览器访问 http://localhost:5173 ，开发代理已配置 `/api` → `http://localhost:8081`。

## 默认账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 超级管理员 |

> 密码为 BCrypt 密文存储，首次登录后请尽快修改。

## 接口说明

- 认证：`POST /api/login`（JWT 签发）、`GET /api/test/freeLogin`（测试环境免密登录）
- 需求管理：`/api/requirement/*`（分页查询、详情、新增、编辑、删除、批量导入、导出、统计）
- 系统管理：`/api/system/*`（用户、角色、区域、操作日志）

## 项目说明

- 数据库统一使用 UTF-8 编码（非 utf8mb4），与 `application.yml` 中 `characterEncoding=utf8` 保持一致
- 前端路由含 `SKIP_LOGIN_DEBUG` 调试开关（当前已关闭），正式环境请保持 `false`
- 数据库密码通过环境变量 `SPRING_DATASOURCE_PASSWORD` 注入或直接修改配置文件，禁止提交真实密码
- Redis 缓存 key 规范：`login:token:{username}` / `login:user:{username}`（登录会话，TTL 2h）、`requirement:detail:{id}`（需求详情，TTL 10min）、`stat:*`（首页统计，TTL 5min）；增删改操作后自动删除缓存（Cache Aside 模式）
- Redis 序列化方案：`StringRedisTemplate` + Hutool `JSONUtil` 手动 JSON 转换，规避 Spring Boot 4 / Jackson 3 的序列化兼容问题
