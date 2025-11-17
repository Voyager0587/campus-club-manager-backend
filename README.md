# 校园社团管理平台 - 后端项目

## 项目简介

基于 Spring Boot 3 + MyBatis Plus + Sa-Token 构建的校园社团管理系统后端，实现了完整的 RBAC 权限模型和用户认证授权体系。

## 技术栈

- **核心框架**: Spring Boot 3.4.4
- **ORM 框架**: MyBatis Plus 3.5.9
- **权限框架**: Sa-Token 1.44.0
- **数据库**: MySQL 8.0+
- **文档工具**: SpringDoc OpenAPI (Swagger)
- **构建工具**: Maven 3.8+
- **JDK 版本**: Java 21

## 快速开始

### 1. 环境准备

- 安装 JDK 21+
- 安装 Maven 3.8+
- 安装 MySQL 8.0+

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE campus_club CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入初始化脚本
USE campus_club;
SOURCE src/main/resources/db/migration/V1__Create_User_Table.sql;
```

### 3. 修改配置

编辑 `src/main/resources/application.yaml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_club
    username: root
    password: 你的密码
```

### 4. 启动项目

```bash
# 方式一：IDE 启动
# 直接运行 CampusClubManagerBackendApplication.main()

# 方式二：命令行启动
mvn clean package -DskipTests
java -jar target/campus-club-manager-backend-0.0.1-SNAPSHOT.jar
```

### 5. 访问接口文档

启动成功后访问：[http://localhost:8080/api/doc.html](http://localhost:8080/api/doc.html)

## 核心功能

### ✅ 已实现功能

#### 1. RBAC 权限系统

- 三种角色：普通用户（user）、社团管理员（club_admin）、系统管理员（system_admin）
- 基于注解的权限验证：`@SaCheckRole`
- 自动化的登录状态管理
- Token 自动生成与验证

#### 2. 用户管理模块

| 接口 | 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|------|
| 用户注册 | POST | `/api/user/register` | 公开 | 新用户自助注册 |
| 用户登录 | POST | `/api/user/login` | 公开 | 获取访问令牌 |
| 获取用户信息 | GET | `/api/user/info` | user | 查询当前用户信息 |
| 更新用户信息 | POST | `/api/user/update` | user | 修改个人资料 |

#### 3. 测试账号

系统预置了三个测试账号（密码均为 `123456`）：

| 用户名 | 角色 | 学号 | 邮箱 |
|-------|------|------|------|
| admin | system_admin | ADMIN001 | admin@campus.com |
| test_user | user | 20230001 | test@campus.com |
| club_admin | club_admin | 20230002 | club@campus.com |

## 项目结构

```
src/main/java/com/club/campusclubmanager/
├── config/          # 配置类（MyBatis Plus、Sa-Token）
├── controller/      # 控制器层
├── dto/            # 数据传输对象（请求参数）
├── entity/         # 实体类（数据库映射）
├── enums/          # 枚举类
├── exception/      # 异常处理
├── mapper/         # MyBatis Mapper
├── service/        # 服务层
├── vo/             # 视图对象（响应数据）
└── common/         # 公共类（统一响应）

src/main/resources/
├── application.yaml           # 配置文件
└── db/migration/             # 数据库脚本
    └── V1__Create_User_Table.sql

docs/
├── RBAC权限系统使用指南.md    # 权限系统详细说明
├── 快速启动指南.md           # 项目启动教程
└── 项目架构说明.md           # 架构设计文档
```

## API 测试示例

### 用户注册

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "123456",
    "realName": "新用户",
    "studentId": "20240001",
    "email": "newuser@campus.com",
    "phone": "13800138000"
  }'
```

### 用户登录

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "123456"
  }'
```

### 获取用户信息

```bash
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer {token}"
```

## 文档导航

- 📖 [RBAC 权限系统使用指南](docs/RBAC权限系统使用指南.md) - 详细的权限体系说明
- 🚀 [快速启动指南](docs/快速启动指南.md) - 从零开始启动项目
- 🏗️ [项目架构说明](docs/项目架构说明.md) - 技术架构与设计模式
- 📝 [后端开发项目文档](docs/学校社团管理平台%20·%20后端开发项目文档.md) - 完整需求与设计

## 开发规范

### 统一响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 业务数据
  }
}
```

### 异常处理

系统自动捕获并处理以下异常：

- `BusinessException` - 业务异常（返回 500）
- `NotLoginException` - 未登录异常（返回 401）
- `NotRoleException` - 角色不足异常（返回 403）
- `MethodArgumentNotValidException` - 参数校验失败（返回 400）

### 权限验证注解

```java
// 要求用户具有 user 角色
@SaCheckRole("user")

// 要求用户具有 club_admin 或 system_admin 角色（满足任一）
@SaCheckRole(value = {"club_admin", "system_admin"}, mode = SaMode.OR)

// 要求用户具有特定权限
@SaCheckPermission("club:create")
```

## 后续规划

- [ ] 社团管理模块
- [ ] 活动管理模块
- [ ] 报名管理模块
- [ ] 文件上传（OSS 集成）
- [ ] 消息通知模块
- [ ] 数据统计与分析
- [ ] Redis 缓存集成
- [ ] 单元测试覆盖

## 常见问题

### 1. 如何添加新的角色？

在 [UserRole.java](src/main/java/com/club/campusclubmanager/enums/UserRole.java) 中添加新的枚举值即可。

### 2. 如何添加细粒度权限？

修改 `SaTokenConfig.getPermissionList()` 方法，从数据库查询用户权限列表。

### 3. Token 如何刷新？

当前 Token 有效期为 30 天，可以通过 `StpUtil.renewTimeout()` 方法刷新。

### 4. 如何切换到 Redis 存储 Token？

添加 `sa-token-redis-jackson` 依赖，配置 Redis 连接即可。

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或查看文档。

---

**项目状态**: 🚧 开发中

**最后更新**: 2025-11-17
