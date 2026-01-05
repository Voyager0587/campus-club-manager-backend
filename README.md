# 校园社团管理平台 - 后端项目

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.9-blue.svg)](https://baomidou.com/)
[![Sa-Token](https://img.shields.io/badge/Sa--Token-1.44.0-orange.svg)](https://sa-token.cc/)
[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📑 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [核心功能](#核心功能)
- [数据模型](#数据模型)
- [API 路由总览](#api-路由总览)
- [开发规范](#开发规范)

## 项目简介

基于 Spring Boot 3 + MyBatis Plus + Sa-Token 构建的**校园社团管理系统后端**，实现了完整的用户管理、社团管理、活动管理等核心功能，支持基于角色的权限控制（RBAC）。

**核心特性：**
- ✨ 完整的 RBAC 权限体系（普通用户、社团负责人、系统管理员）
- 🏛️ 社团全生命周期管理（创建、审核、成员管理）
- 🎯 活动发布、报名、签到完整流程
- 🔔 完善的通知系统（站内通知 + 邮件通知）
- 📁 阿里云 OSS 文件上传
- 🤖 AI智能问答助手
- 🔐 基于 Sa-Token 的认证授权
- 📊 分页查询、关键词搜索、状态筛选
- 📝 完善的 Swagger API 文档

## 技术栈

### 核心框架
- **Spring Boot**: 3.3.10
- **MyBatis Plus**: 3.5.9（ORM框架 + 分页插件）
- **Sa-Token**: 1.44.0（权限认证框架）

### 数据库与存储
- **MySQL**: 8.0+
- **Aliyun OSS**: 3.18.1（对象存储）

### 其他依赖
- **SpringDoc OpenAPI**: 2.6.0（Swagger文档）
- **Spring Mail**: 邮件发送服务
- **Spring AI**: 1.0.3 + Alibaba Cloud AI 1.0.0.2（AI集成）
- **Lombok**: 简化Java代码
- **Validation**: 参数校验

### 构建工具
- **Maven**: 3.8+
- **JDK**: Java 21

## 核心功能

### 1. RBAC 权限系统

- **三种角色**：
  - `user` - 普通用户（学生）
  - `club_admin` - 社团管理员（社团负责人）
  - `system_admin` - 系统管理员
- 基于注解的权限验证：`@SaCheckRole`、`@SaCheckLogin`
- 自动化的登录状态管理
- Token 自动生成与验证

### 2. 用户管理模块

| 功能 | 说明 |
|------|------|
| 用户注册 | 新用户自助注册 |
| 用户登录 | 获取访问令牌 |
| 获取用户信息 | 查询当前登录用户详细信息 |
| 更新用户信息 | 修改个人资料 |

### 3. 社团管理模块

**学生端功能：**
- ✅ 分页查询社团列表（支持关键词搜索）
- ✅ 查询社团详情
- ✅ 申请加入社团
- ✅ 查看我加入的社团
- ✅ 查看社团成员列表
- ✅ 查看我的申请记录

**社团负责人功能：**
- ✅ 审核社团加入申请
- ✅ 更新社团基本信息
- ✅ 查看待审核申请列表

**系统管理员功能：**
- ✅ 创建社团
- ✅ 更新社团信息
- ✅ 删除社团
- ✅ 审核社团申请
- ✅ 设置/取消社团负责人
- ✅ 移除社团成员
- ✅ 修改社团成员角色

### 4. 活动管理模块

**学生端功能：**
- ✅ 浏览活动列表（支持关键词和社团筛选）
- ✅ 查看活动详情
- ✅ 报名参加活动
- ✅ 取消活动报名
- ✅ 查看我的报名记录

**社团负责人功能：**
- ✅ 创建活动（需审核）
- ✅ 更新活动信息
- ✅ 取消活动
- ✅ 查看活动报名列表
- ✅ 活动签到/标记缺席

**系统管理员功能：**
- ✅ 查看所有活动（支持状态筛选）
- ✅ 审核活动（通过/拒绝）
- ✅ 创建活动（直接发布）
- ✅ 更新活动信息
- ✅ 取消活动
- ✅ 删除活动
- ✅ 查看活动报名列表
- ✅ 活动签到/标记缺席

### 5. 通知系统

**通知功能：**
- ✅ 分页查询我的通知（支持类型和已读状态筛选）
- ✅ 获取未读通知数量
- ✅ 标记单条/全部通知为已读
- ✅ 删除通知
- ✅ 获取/更新通知偏好设置
- ✅ 系统管理员批量发布通知
- ✅ 社团管理员发布社团通知

**通知类型：**
- `system` - 系统通知
- `audit` - 审核消息
- `activity` - 活动提醒
- `club` - 社团通知

**通知优先级：**
- 0 - 低
- 1 - 普通
- 2 - 高
- 3 - 紧急

### 6. 文件上传模块

- ✅ 图片上传（JPG、PNG、GIF等格式，最大10MB）
- ✅ 支持分类上传（avatar/cover/content）
- ✅ 支持关联社团ID
- ✅ 健康检查接口
- ✅ 快速上传测试接口

### 7. AI 问答模块

- ✅ AI智能对话接口
- ✅ 支持自然语言交互
- ✅ 集成阿里云AI服务

## 数据模型

### 核心数据表

#### 1. user（用户表）
```sql
- id: 用户ID
- username: 用户名（唯一）
- password: 密码（加密）
- real_name: 真实姓名
- student_id: 学号（唯一）
- email: 邮箱（唯一）
- phone: 手机号
- avatar: 头像URL
- role: 用户角色（user/club_admin/system_admin）
- status: 账户状态（0-禁用，1-正常）
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 2. club（社团表）
```sql
- id: 社团ID
- name: 社团名称（唯一）
- description: 社团简介
- logo: 社团图标URL
- status: 社团状态（pending/normal/disabled）
- create_user: 创建者用户ID
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 3. club_member（社团成员表）
```sql
- id: 成员关系ID
- club_id: 社团ID
- user_id: 用户ID
- role: 成员角色（member/leader）
- join_time: 加入时间
- memo: 备注信息
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 4. club_application（社团申请表）
```sql
- id: 申请ID
- club_id: 社团ID
- user_id: 申请用户ID
- status: 申请状态（pending/approved/rejected）
- reason: 申请理由
- review_note: 审核备注
- review_time: 审核时间
- reviewer_id: 审核人ID
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 5. activity（活动表）
```sql
- id: 活动ID
- club_id: 所属社团ID
- title: 活动标题
- content: 活动内容详情
- cover: 活动封面图URL
- location: 活动地点
- start_time: 活动开始时间
- end_time: 活动结束时间
- signup_start_time: 报名开始时间
- signup_end_time: 报名截止时间
- status: 活动状态（draft/pending/published/cancelled/completed）
- max_members: 最大报名人数（NULL表示不限制）
- current_members: 当前报名人数（冗余字段）
- create_user: 创建者用户ID
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 6. activity_signup（活动报名表）
```sql
- id: 报名记录ID
- activity_id: 活动ID
- user_id: 用户ID
- status: 报名状态（registered/cancelled/checked_in/absent）
- signup_time: 报名时间
- checkin_time: 签到时间
- remark: 备注信息
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 7. notification（通知表）
```sql
- id: 通知ID
- user_id: 接收用户ID
- title: 通知标题
- content: 通知内容
- type: 通知类型（system/audit/activity/club）
- related_type: 关联类型（club/activity/announcement等）
- related_id: 关联ID
- priority: 优先级（0-低，1-普通，2-高，3-紧急）
- read_flag: 阅读标记（0-未读，1-已读）
- read_time: 阅读时间
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 8. user_notification_setting（用户通知设置表）
```sql
- id: 设置ID
- user_id: 用户ID
- email_enabled: 是否启用邮件通知（0-禁用，1-启用）
- in_app_enabled: 是否启用站内消息（0-禁用，1-启用）
- audit_notification: 审核消息通知（0-禁用，1-启用）
- activity_notification: 活动提醒通知（0-禁用，1-启用）
- club_notification: 社团通知（0-禁用，1-启用）
- system_notification: 系统通知（0-禁用，1-启用）
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

#### 9. announcement（公告表）
```sql
- id: 公告ID
- title: 公告标题
- content: 公告内容
- type: 公告类型（system/club/activity）
- related_id: 关联ID（社团ID或活动ID）
- priority: 优先级（0-普通，1-重要，2-紧急）
- status: 状态（draft/published/archived）
- create_user_id: 创建者用户ID
- publish_time: 发布时间
- expire_time: 过期时间
- create_time: 创建时间
- update_time: 更新时间
- is_deleted: 逻辑删除标记
```

## API 路由总览

### 用户管理（/user）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 用户注册 | POST | `/user/register` | 公开 | 用户自助注册 |
| 用户登录 | POST | `/user/login` | 公开 | 获取访问令牌 |
| 获取用户信息 | GET | `/user/info` | user | 查询当前用户信息 |
| 更新用户信息 | POST | `/user/update` | user | 修改个人资料 |

### 社团管理 - 学生端（/club）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 分页查询社团列表 | GET | `/club/list` | 公开 | 支持关键词搜索 |
| 查询社团详情 | GET | `/club/{id}` | 公开 | 查看社团详细信息 |
| 查询我加入的社团 | GET | `/club/my` | user | 查看当前用户已加入的社团列表 |
| 申请加入社团 | POST | `/club/apply` | user | 提交加入社团申请 |
| 查询社团成员列表 | GET | `/club/{id}/members` | 公开 | 查看社团的成员列表（分页） |
| 查询我的申请记录 | GET | `/club/my/applications` | user | 查看当前用户的所有社团申请记录 |

### 社团管理 - 负责人（/club/management）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 查询待审核申请列表 | GET | `/club/management/{clubId}/applications/pending` | 负责人 | 查看自己管理的社团的待审核申请 |
| 审核社团申请 | POST | `/club/management/{clubId}/applications/review` | 负责人 | 审核通过或拒绝用户的加入申请 |
| 更新社团信息 | PUT | `/club/management/{clubId}` | 负责人 | 修改自己管理的社团的基本信息 |

### 社团管理 - 管理员（/admin/club）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 创建社团 | POST | `/admin/club/create` | system_admin | 创建新的社团 |
| 更新社团信息 | PUT | `/admin/club/update` | system_admin | 修改社团基本信息 |
| 删除社团 | DELETE | `/admin/club/{id}` | system_admin | 删除社团及相关数据 |
| 查询待审核申请列表 | GET | `/admin/club/applications/pending` | system_admin | 查看所有待审核的社团加入申请 |
| 审核社团申请 | POST | `/admin/club/applications/review` | system_admin | 审核通过或拒绝用户的加入申请 |
| 设置社团负责人 | POST | `/admin/club/{clubId}/leader/{userId}` | system_admin | 将指定用户设置为社团负责人 |
| 取消社团负责人 | DELETE | `/admin/club/{clubId}/leader/{userId}` | system_admin | 取消指定用户的社团负责人身份 |
| 移除社团成员 | DELETE | `/admin/club/{clubId}/member/{userId}` | system_admin | 将指定成员从社团中移除 |
| 修改社团成员角色 | PUT | `/admin/club/{clubId}/member/{userId}/role` | system_admin | 修改成员在社团中的角色 |

### 活动管理 - 学生端（/activity）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 浏览活动列表 | GET | `/activity/list` | 公开 | 支持关键词搜索和社团筛选 |
| 查看活动详情 | GET | `/activity/{id}` | 公开 | 查看活动详细信息 |
| 报名活动 | POST | `/activity/{id}/signup` | user | 用户报名参加活动 |
| 取消报名 | DELETE | `/activity/{id}/signup` | user | 用户取消活动报名 |
| 查看我的报名记录 | GET | `/activity/my-signups` | user | 查看当前用户的所有活动报名记录 |

### 活动管理 - 社团负责人（/club-admin/activity）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 创建活动 | POST | `/club-admin/activity/create` | user | 社团负责人创建活动（需审核） |
| 更新活动 | PUT | `/club-admin/activity/{id}` | user | 社团负责人更新活动信息 |
| 取消活动 | PUT | `/club-admin/activity/{id}/cancel` | user | 社团负责人取消活动 |
| 查看活动报名列表 | GET | `/club-admin/activity/{id}/signups` | user | 社团负责人查看活动的所有报名人员 |
| 活动签到/标记缺席 | POST | `/club-admin/activity/{id}/checkin` | user | 社团负责人为报名用户签到或标记缺席 |

### 活动管理 - 系统管理员（/admin/activity）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 查看所有活动 | GET | `/admin/activity/list` | system_admin | 查看所有活动，支持关键词和状态筛选 |
| 审核活动 | PUT | `/admin/activity/{id}/review` | system_admin | 审核活动（通过/拒绝） |
| 删除活动 | DELETE | `/admin/activity/{id}` | system_admin | 删除活动 |
| 创建活动 | POST | `/admin/activity/create` | system_admin | 直接创建并发布活动 |
| 更新活动 | PUT | `/admin/activity/{id}` | system_admin | 更新活动信息 |
| 取消活动 | PUT | `/admin/activity/{id}/cancel` | system_admin | 取消活动 |
| 查看活动报名列表 | GET | `/admin/activity/{id}/signups` | system_admin | 查看任意活动的所有报名人员 |
| 活动签到/标记缺席 | POST | `/admin/activity/{id}/checkin` | system_admin | 为报名用户签到或标记缺席 |

### 通知管理（/notification）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 分页查询我的通知 | GET | `/notification/list` | 登录用户 | 支持按类型和已读状态筛选 |
| 获取未读数量 | GET | `/notification/unread-count` | 登录用户 | 获取当前用户的未读通知数量 |
| 标记已读 | PUT | `/notification/mark-read` | 登录用户 | 标记单条或全部通知为已读 |
| 删除通知 | DELETE | `/notification/{id}` | 登录用户 | 删除指定的通知 |
| 获取通知设置 | GET | `/notification/settings` | 登录用户 | 获取当前用户的通知偏好设置 |
| 更新通知设置 | PUT | `/notification/settings` | 登录用户 | 更新当前用户的通知偏好设置 |
| 系统发布通知 | POST | `/notification/systempublish` | system_admin | 批量发送通知给指定用户 |
| 社团发布通知 | POST | `/notification/clubpublish` | club_admin | 社团管理员发布社团通知 |

### 文件上传（/v1/upload）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 上传单张图片 | POST | `/v1/upload/image` | 公开 | 支持JPG、PNG、GIF等格式，最大10MB |
| 快速上传测试 | POST | `/v1/upload/test` | 公开 | 简化版上传，仅用于测试 |
| 健康检查 | GET | `/v1/upload/health` | 公开 | 检查上传服务是否正常 |

### AI 问答（/ai）

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| AI聊天 | POST | `/ai/chat` | 公开 | AI智能对话，接收用户消息并返回AI回复 |

## 开发规范

### 1. 代码规范
- 使用 Lombok 简化代码
- 使用 `@Valid` 进行参数校验
- 统一使用 `Result<T>` 进行响应封装
- Controller层只做路由和参数校验，业务逻辑在Service层实现

### 2. 权限控制
- 使用 `@SaCheckLogin` 验证登录状态
- 使用 `@SaCheckRole("角色")` 验证角色权限
- 细粒度权限验证在Service层实现

### 3. 异常处理
- 统一异常处理机制
- 业务异常抛出自定义异常
- 返回友好的错误提示

### 4. API 文档
- 使用 SpringDoc OpenAPI 自动生成文档
- 每个接口都需添加 `@Operation` 注解
- 参数使用 `@Parameter` 注解说明

### 5. 数据库规范
- 所有表使用逻辑删除（is_deleted字段）
- 统一使用 create_time 和 update_time 时间戳
- 合理使用索引优化查询性能
- 外键约束确保数据一致性

## 快速开始

### 1. 环境准备
- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- 阿里云 OSS 账号（文件上传功能）
- 阿里云 AI 服务（AI问答功能）

### 2. 数据库初始化
执行 `src/main/resources/db/migration/campus_club.sql` 文件初始化数据库。

### 3. 配置文件
修改 `application.yaml` 或 `application-dev.yaml` 中的数据库连接、OSS配置、AI配置等。

### 4. 启动项目
```bash
mvn clean install
mvn spring-boot:run
```

### 5. 访问文档
启动后访问 Swagger 文档：`http://localhost:8080/swagger-ui.html`

---

**最后更新时间**: 2026-01-04
