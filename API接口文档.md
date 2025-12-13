# 校园社团管理系统 API 接口文档

## 目录
- [1. 用户管理模块](#1-用户管理模块)
- [2. 社团管理模块](#2-社团管理模块)
  - [2.1 用户端接口](#21-用户端接口)
  - [2.2 社团负责人接口](#22-社团负责人接口)
  - [2.3 系统管理员接口](#23-系统管理员接口)
- [3. 活动管理模块](#3-活动管理模块)
  - [3.1 学生端接口](#31-学生端接口)
  - [3.2 社团管理员接口](#32-社团管理员接口)
  - [3.3 系统管理员接口](#33-系统管理员接口)

---

## 1. 用户管理模块

**控制器**: `UserController`  
**基础路径**: `/user`  
**功能描述**: 提供用户注册、登录、信息查询和更新等基础功能

### 1.1 用户注册
- **接口路径**: `POST /user/register`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 用户自助注册账号
- **请求体**: `RegisterRequest`
- **响应**: 注册成功提示


### 1.2 用户登录
- **接口路径**: `POST /user/login`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 用户登录获取Token
- **请求体**: `LoginRequest`
- **响应**: `LoginResponse`（包含Token和用户信息）

### 1.3 获取当前用户信息
- **接口路径**: `GET /user/info`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 获取当前登录用户的详细信息
- **响应**: `UserInfoVO`

### 1.4 更新用户信息
- **接口路径**: `POST /user/update`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 更新当前用户的个人资料
- **请求体**: `UpdateUserRequest`
- **响应**: 更新成功提示

---

## 2. 社团管理模块

### 2.1 用户端接口

**控制器**: `ClubController`  
**基础路径**: `/club`  
**功能描述**: 提供社团浏览、查询、申请加入等用户端功能

#### 2.1.1 分页查询社团列表
- **接口路径**: `GET /club/list`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 支持关键词搜索的分页社团列表查询
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
  - `keyword` (String, 可选): 搜索关键词
- **响应**: `Page<ClubVO>`

#### 2.1.2 查询社团详情
- **接口路径**: `GET /club/{id}`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 查看社团详细信息
- **路径参数**: `id` (Long): 社团ID
- **响应**: `ClubDetailVO`

#### 2.1.3 查询我加入的社团
- **接口路径**: `GET /club/my`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 查看当前用户已加入的社团列表
- **响应**: `List<ClubVO>`

#### 2.1.4 申请加入社团
- **接口路径**: `POST /club/apply`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 提交加入社团的申请
- **请求体**: `ApplyJoinClubRequest`
- **响应**: 申请提交成功提示

#### 2.1.5 查询社团成员列表
- **接口路径**: `GET /club/{id}/members`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 查看社团的成员列表
- **路径参数**: `id` (Long): 社团ID
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
- **响应**: `Page<ClubMemberVO>`

#### 2.1.6 查询我的申请记录
- **接口路径**: `GET /club/my/applications`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 查看当前用户的所有社团申请记录
- **响应**: `List<ClubApplicationVO>`

### 2.2 社团负责人接口

**控制器**: `ClubManagementController`  
**基础路径**: `/club/management`  
**功能描述**: 提供社团负责人管理社团的相关功能

#### 2.2.1 查询待审核申请列表
- **接口路径**: `GET /club/management/{clubId}/applications/pending`
- **权限要求**: 需要登录（`@SaCheckLogin`），且需要社团负责人权限
- **功能说明**: 查看自己管理的社团的待审核申请
- **路径参数**: `clubId` (Long): 社团ID
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
- **响应**: `Page<ClubApplicationVO>`

#### 2.2.2 审核社团申请
- **接口路径**: `POST /club/management/{clubId}/applications/review`
- **权限要求**: 需要登录（`@SaCheckLogin`），且需要社团负责人权限
- **功能说明**: 审核通过或拒绝用户的加入申请
- **路径参数**: `clubId` (Long): 社团ID
- **请求体**: `ReviewApplicationRequest`
- **响应**: 审核完成提示

#### 2.2.3 更新社团信息
- **接口路径**: `PUT /club/management/{clubId}`
- **权限要求**: 需要登录（`@SaCheckLogin`），且需要社团负责人权限
- **功能说明**: 修改自己管理的社团的基本信息
- **路径参数**: `clubId` (Long): 社团ID
- **请求体**: `UpdateClubRequest`
- **响应**: 更新成功提示

### 2.3 系统管理员接口

**控制器**: `AdminClubController`  
**基础路径**: `/admin/club`  
**功能描述**: 提供系统管理员对社团的创建、审核、管理等完整功能

#### 2.3.1 创建社团
- **接口路径**: `POST /admin/club/create`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 创建新的社团
- **请求体**: `CreateClubRequest`
- **响应**: 社团创建成功提示

#### 2.3.2 更新社团信息
- **接口路径**: `PUT /admin/club/update`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 修改社团基本信息
- **请求体**: `UpdateClubRequest`
- **响应**: 更新成功提示

#### 2.3.3 删除社团
- **接口路径**: `DELETE /admin/club/{id}`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 删除社团及相关数据
- **路径参数**: `id` (Long): 社团ID
- **响应**: 删除成功提示

#### 2.3.4 查询待审核申请列表
- **接口路径**: `GET /admin/club/applications/pending`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 查看所有待审核的社团加入申请
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
- **响应**: `Page<ClubApplicationVO>`

#### 2.3.5 审核社团申请
- **接口路径**: `POST /admin/club/applications/review`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 审核通过或拒绝用户的加入申请
- **请求体**: `ReviewApplicationRequest`
- **响应**: 审核完成提示

#### 2.3.6 设置社团负责人
- **接口路径**: `POST /admin/club/{clubId}/leader/{userId}`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 将指定用户设置为社团负责人
- **路径参数**:
  - `clubId` (Long): 社团ID
  - `userId` (Long): 用户ID
- **响应**: 设置成功提示

#### 2.3.7 取消社团负责人
- **接口路径**: `DELETE /admin/club/{clubId}/leader/{userId}`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 取消指定用户的社团负责人身份
- **路径参数**:
  - `clubId` (Long): 社团ID
  - `userId` (Long): 用户ID
- **响应**: 取消成功提示

---

## 3. 活动管理模块

### 3.1 学生端接口

**控制器**: `ActivityController`  
**基础路径**: `/activity`  
**功能描述**: 提供活动浏览、报名、取消报名等学生端功能

#### 3.1.1 浏览活动列表
- **接口路径**: `GET /activity/list`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 支持关键词搜索和社团筛选的活动列表查询
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
  - `keyword` (String, 可选): 搜索关键词
  - `clubId` (Long, 可选): 社团ID
- **响应**: `Page<ActivityVO>`

#### 3.1.2 查看活动详情
- **接口路径**: `GET /activity/{id}`
- **权限要求**: 公开接口（无需登录）
- **功能说明**: 查看活动详细信息
- **路径参数**: `id` (Long): 活动ID
- **响应**: `ActivityDetailVO`

#### 3.1.3 报名活动
- **接口路径**: `POST /activity/{id}/signup`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 用户报名参加活动
- **路径参数**: `id` (Long): 活动ID
- **响应**: 报名成功提示

#### 3.1.4 取消报名
- **接口路径**: `DELETE /activity/{id}/signup`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 用户取消活动报名
- **路径参数**: `id` (Long): 活动ID
- **响应**: 取消报名成功提示

#### 3.1.5 查看我的报名记录
- **接口路径**: `GET /activity/my-signups`
- **权限要求**: 需要登录（`@SaCheckRole("user")`）
- **功能说明**: 查看当前用户的所有活动报名记录
- **响应**: `List<ActivitySignupVO>`

### 3.2 社团管理员接口

**控制器**: `ClubActivityController`  
**基础路径**: `/club-admin/activity`  
**功能描述**: 提供社团负责人创建、管理活动等功能

#### 3.2.1 创建活动
- **接口路径**: `POST /club-admin/activity/create`
- **权限要求**: 需要登录（`@SaCheckRole("user")`），具体权限在Service层验证
- **功能说明**: 社团负责人创建活动，创建后等待系统管理员审核
- **请求体**: `CreateActivityRequest`
- **响应**: 活动创建成功提示（提示等待审核）

#### 3.2.2 更新活动
- **接口路径**: `PUT /club-admin/activity/{id}`
- **权限要求**: 需要登录（`@SaCheckRole("user")`），具体权限在Service层验证
- **功能说明**: 社团负责人更新活动信息
- **路径参数**: `id` (Long): 活动ID
- **请求体**: `UpdateActivityRequest`
- **响应**: 活动更新成功提示

#### 3.2.3 取消活动
- **接口路径**: `PUT /club-admin/activity/{id}/cancel`
- **权限要求**: 需要登录（`@SaCheckRole("user")`），具体权限在Service层验证
- **功能说明**: 社团负责人取消活动
- **路径参数**: `id` (Long): 活动ID
- **响应**: 活动已取消提示

#### 3.2.4 查看活动报名列表
- **接口路径**: `GET /club-admin/activity/{id}/signups`
- **权限要求**: 需要登录（`@SaCheckRole("user")`），具体权限在Service层验证
- **功能说明**: 社团负责人查看活动的所有报名人员
- **路径参数**: `id` (Long): 活动ID
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
- **响应**: `Page<ActivitySignupVO>`

#### 3.2.5 活动签到/标记缺席
- **接口路径**: `POST /club-admin/activity/{id}/checkin`
- **权限要求**: 需要登录（`@SaCheckRole("user")`），具体权限在Service层验证
- **功能说明**: 社团负责人为报名用户签到或标记缺席
- **路径参数**: `id` (Long): 活动ID
- **请求体**: `CheckinRequest`
- **响应**: 操作成功提示

### 3.3 系统管理员接口

**控制器**: `AdminActivityController`  
**基础路径**: `/admin/activity`  
**功能描述**: 提供系统管理员审核、管理所有活动的功能

#### 3.3.1 查看所有活动
- **接口路径**: `GET /admin/activity/list`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 系统管理员查看所有活动，支持关键词和状态筛选
- **请求参数**:
  - `pageNum` (Integer, 默认值: 1): 页码
  - `pageSize` (Integer, 默认值: 10): 每页数量
  - `keyword` (String, 可选): 搜索关键词
  - `status` (String, 可选): 活动状态
- **响应**: `Page<ActivityVO>`

#### 3.3.2 审核活动
- **接口路径**: `PUT /admin/activity/{id}/review`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 系统管理员审核活动（通过/拒绝）
- **路径参数**: `id` (Long): 活动ID
- **请求体**: `ReviewActivityRequest`
- **响应**: 审核完成提示

#### 3.3.3 删除活动
- **接口路径**: `DELETE /admin/activity/{id}`
- **权限要求**: 需要系统管理员角色（`@SaCheckRole("system_admin")`）
- **功能说明**: 系统管理员删除活动
- **路径参数**: `id` (Long): 活动ID
- **响应**: 删除成功提示

---

## 权限说明

### 角色类型
- **公开接口**: 无需登录即可访问
- **user**: 普通用户角色，需要登录
- **system_admin**: 系统管理员角色，拥有最高权限

### 权限验证机制
- 使用 Sa-Token 框架进行权限控制
- `@SaCheckRole("user")`: 需要用户角色
- `@SaCheckRole("system_admin")`: 需要系统管理员角色
- `@SaCheckLogin`: 需要登录（不限制角色）
- 部分接口在Service层进行额外的权限验证（如社团负责人权限）

---

## 通用响应格式

所有接口统一使用 `Result<T>` 作为响应格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

---

## 分页参数说明

所有分页接口使用以下参数：
- `pageNum`: 页码，从1开始，默认值为1
- `pageSize`: 每页数量，默认值为10

返回的分页数据格式为 MyBatis-Plus 的 `Page<T>` 对象，包含：
- `records`: 数据列表
- `total`: 总记录数
- `size`: 每页大小
- `current`: 当前页码
- `pages`: 总页数

---

*文档生成时间: 2025-01-22*

