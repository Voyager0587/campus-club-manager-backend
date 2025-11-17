# RBAC 权限系统使用指南

## 1. 概述

本系统基于 **Sa-Token** 实现了 RBAC（基于角色的访问控制）权限模型，包含三种角色和完整的用户认证授权体系。

## 2. 角色定义

| 角色代码 | 角色名称 | 权限范围 |
|---------|---------|---------|
| `user` | 普通用户（学生） | 浏览社团/活动、报名、加入社团 |
| `club_admin` | 社团管理员 | 管理自己社团的活动与成员 |
| `system_admin` | 系统管理员 | 后台管理所有数据 |

## 3. API 接口权限

### 3.1 用户模块接口

| 接口路径 | 请求方式 | 描述 | 权限要求 |
|---------|---------|------|---------|
| `/api/user/register` | POST | 用户自助注册 | **公开接口，无需权限** |
| `/api/user/login` | POST | 用户登录，获取 Token | **公开接口，无需权限** |
| `/api/user/info` | GET | 获取当前登录用户信息 | `user` |
| `/api/user/update` | POST | 更新个人资料 | `user` |

### 3.2 接口示例

#### 注册接口
```bash
POST /api/user/register
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456",
  "realName": "张三",
  "studentId": "20230001",
  "email": "zhangsan@campus.com",
  "phone": "13800138000"
}
```

#### 登录接口
```bash
POST /api/user/login
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456"
}

# 响应示例
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
    "userInfo": {
      "id": 1,
      "username": "zhangsan",
      "realName": "张三",
      "studentId": "20230001",
      "email": "zhangsan@campus.com",
      "role": "user",
      "status": 1
    }
  }
}
```

#### 获取用户信息（需要登录）
```bash
GET /api/user/info
Authorization: Bearer 6afff885-4e8d-43d2-bf32-25858b0d496e
```

#### 更新用户信息（需要登录）
```bash
POST /api/user/update
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGc...
Content-Type: application/json

{
  "realName": "张三丰",
  "email": "newemail@campus.com",
  "phone": "13900139000",
  "avatar": "https://example.com/avatar.jpg"
}
```

## 4. 权限验证注解使用

### 4.1 角色验证

在 Controller 方法上使用 `@SaCheckRole` 注解进行角色验证：

```java
/**
 * 要求当前用户具有 user 角色
 */
@SaCheckRole("user")
@GetMapping("/info")
public Result<UserInfoVO> getCurrentUserInfo() {
    // ...
}

/**
 * 要求当前用户具有 club_admin 角色
 */
@SaCheckRole("club_admin")
@PostMapping("/club/manage")
public Result<Void> manageClub() {
    // ...
}

/**
 * 要求当前用户具有 system_admin 角色
 */
@SaCheckRole("system_admin")
@DeleteMapping("/user/{id}")
public Result<Void> deleteUser(@PathVariable Long id) {
    // ...
}
```

### 4.2 多角色验证

```java
/**
 * 要求当前用户具有 club_admin 或 system_admin 角色（满足任一即可）
 */
@SaCheckRole(value = {"club_admin", "system_admin"}, mode = SaMode.OR)
@GetMapping("/club/list")
public Result<List<Club>> getClubList() {
    // ...
}

/**
 * 要求当前用户同时具有 user 和 club_admin 角色
 */
@SaCheckRole(value = {"user", "club_admin"}, mode = SaMode.AND)
@PostMapping("/advanced/operation")
public Result<Void> advancedOperation() {
    // ...
}
```

### 4.3 权限验证（细粒度控制）

```java
/**
 * 要求当前用户具有特定权限
 */
@SaCheckPermission("club:create")
@PostMapping("/club")
public Result<Void> createClub() {
    // ...
}
```

## 5. 编程式权限验证

在代码中可以使用 `StpUtil` 进行编程式权限验证：

```java
// 检查是否登录
if (StpUtil.isLogin()) {
    // 已登录
}

// 获取当前登录用户ID
Long userId = StpUtil.getLoginIdAsLong();

// 检查角色
if (StpUtil.hasRole("user")) {
    // 具有 user 角色
}

// 检查权限
if (StpUtil.hasPermission("club:create")) {
    // 具有 club:create 权限
}

// 退出登录
StpUtil.logout();
```

## 6. Token 配置说明

当前 Token 配置（在 `application.yaml` 中）：

```yaml
sa-token:
  token-name: Authorization        # Token 名称
  token-prefix: Bearer             # Token 前缀
  is-print: false                  # 是否打印操作日志
  is-read-cookie: false            # 是否从 Cookie 中读取 Token
  timeout: 2592000                 # Token 有效期（秒），30天
  activity-timeout: -1             # Token 最低活跃频率（秒），-1 表示不限制
  is-concurrent: true              # 是否允许同一账号并发登录
```

## 7. 数据库初始化

执行 SQL 脚本创建用户表：

```sql
-- 位置：src/main/resources/db/migration/V1__Create_User_Table.sql
```

测试账号（密码均为：`123456`）：

| 用户名 | 角色 | 学号 | 邮箱 |
|-------|------|------|------|
| admin | system_admin | ADMIN001 | admin@campus.com |
| test_user | user | 20230001 | test@campus.com |
| club_admin | club_admin | 20230002 | club@campus.com |

## 8. 异常处理

系统会自动捕获以下权限相关异常：

- `NotLoginException` - 未登录异常（返回 401）
- `NotRoleException` - 角色不足异常（返回 403）
- `NotPermissionException` - 权限不足异常（返回 403）

## 9. 开发建议

### 9.1 新增需要权限的接口

1. 在 Controller 方法上添加 `@SaCheckRole` 注解
2. 根据业务需求选择合适的角色
3. 在接口文档中明确标注权限要求

### 9.2 扩展角色

如需添加新角色：

1. 在 [UserRole.java](../../../src/main/java/com/club/campusclubmanager/enums/UserRole.java) 中添加新的枚举值
2. 更新数据库表的 role 字段注释
3. 在接口上使用新的角色代码

### 9.3 添加细粒度权限

如需使用权限（Permission）而非角色：

1. 设计权限码规范（如：`resource:action`）
2. 修改 `SaTokenConfig.getPermissionList()` 方法，从数据库查询用户权限
3. 使用 `@SaCheckPermission` 注解进行验证

## 10. 安全注意事项

1. **密码加密**：系统使用 MD5 + 盐值加密密码，生产环境建议使用 BCrypt
2. **Token 安全**：请通过 HTTPS 传输 Token，避免明文传输
3. **角色提升**：普通用户无法自行提升角色，只能由管理员操作
4. **会话管理**：Token 默认30天有效，可根据业务需求调整

## 11. API 测试

可以使用 Swagger UI 进行接口测试：

- 访问地址：`http://localhost:8080/api/doc.html`
- 登录后复制 Token
- 点击 "Authorize" 按钮，输入 `Bearer {token}`
- 即可测试需要权限的接口

## 12. 故障排查

### 12.1 一直提示未登录

- 检查 Token 是否正确传递
- 检查 Token 格式：`Authorization: Bearer {token}`
- 检查 Token 是否过期

### 12.2 权限验证失败

- 检查用户角色是否正确
- 检查注解中的角色代码是否与数据库一致
- 查看 `SaTokenConfig.getRoleList()` 方法是否正确返回角色

### 12.3 拦截器配置问题

- 确认 `context-path` 为 `/api`
- 拦截器路径不包含 `/api` 前缀
- 检查 `excludePathPatterns` 配置
