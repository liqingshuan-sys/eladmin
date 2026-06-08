# EnterprisePlatform 企业级全栈管理平台

> 基于 Spring Boot 2.7 + JPA + Spring Security + Vue 的企业级快速开发平台。
> 
> 在原 eladmin 基础上新增：**工作流引擎**、**BI 数据大屏**、**多租户 SAAS** 三大核心模块。

---

## 🚀 技术栈

| 后端 | 前端 | 数据库 | 中间件 |
|------|------|--------|--------|
| Spring Boot 2.7 | Vue 2 + Element UI | MySQL 8 | Redis |
| Spring Security | Vue Router | JPA(Hibernate) | RabbitMQ |
| JWT | Axios | Elasticsearch | WebSocket |
| **Flowable 7.1** | ECharts | MongoDB | Docker |

## 🧩 新增核心功能

### 1. 📋 工作流引擎 (`eladmin-workflow`)
- 基于 **Flowable 7.1** 的 BPMN2.0 流程引擎
- 流程定义管理：部署、挂起、删除 BPMN 文件
- 流程实例控制：启动、挂起、终止
- 任务管理：待办/已办、认领、审批、驳回、委托
- 完整的 REST API 和 Vue 前端页面

### 2. 📊 BI 报表 & 数据大屏 (`eladmin-report`)
- 可视化仪表盘：拖拽布局、多图表组合
- 图表类型：折线图、柱状图、饼图、表格、数字卡片
- 动态数据源：支持 MySQL/PostgreSQL/Oracle/SQLServer
- SQL 查询编辑器 + 缓存控制
- 仪表盘公开分享

### 3. 🏢 多租户 SAAS 支持 (`eladmin-system:modules/tenant`)
- 租户管理：创建、配置、套餐限制
- 用户-租户关联
- 租户上下文切换（ThreadLocal）
- 用户上限、存储上限控制
- 过期时间管理

## 📦 项目模块

| 模块 | 说明 |
|------|------|
| `eladmin-common` | 通用工具类、异常定义 |
| `eladmin-system` | 核心系统（用户、角色、菜单、权限）|
| `eladmin-logging` | 操作日志、审计 |
| `eladmin-tools` | 工具模块（邮件、存储、支付）|
| `eladmin-generator` | 代码生成器 |
| **`eladmin-workflow`** | **工作流引擎** ✨ |
| **`eladmin-report`** | **BI 数据大屏** ✨ |
| **`eladmin-system (tenant)`** | **多租户管理** ✨ |

## 🔧 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8+
- Redis
- Node.js (前端)

### 启动步骤

```bash
# 1. 导入数据库
mysql -uroot -p < sql/eladmin.sql
mysql -uroot -p < eladmin-workflow/src/main/resources/sql/eladmin-workflow-init.sql
mysql -uroot -p < eladmin-report/src/main/resources/sql/eladmin-report-init.sql

# 2. 启动后端
mvn clean install -DskipTests
cd eladmin-system
mvn spring-boot:run

# 3. 启动前端（需另拉前端项目）
cd eladmin-web
npm install
npm run dev
```

## 📄 许可证

本项目基于 **Apache License 2.0** 开源。
原项目 fork 自 [elunez/eladmin](https://github.com/elunez/eladmin)，感谢原作者的杰出工作。
