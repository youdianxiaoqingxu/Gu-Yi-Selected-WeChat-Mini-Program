# 古艺手作甄选：非遗手工艺品展示订购小程序 设计文档

| 项目 | 内容 |
| --- | --- |
| 系统名称 | 古艺手作甄选 |
| 系统定位 | 非遗手工艺品展示与订购微信小程序 |
| 文档版本 | v1.0 |
| 编写日期 | 2026-10-01 |
| 文档状态 | 待评审 |
| 本期范围 | 小程序端 + 后端服务（不含 Web 管理后台） |

---

## 1. 项目概述

### 1.1 背景

非遗手工艺品长期依赖线下展会、景区门店和熟人渠道销售，存在三个问题：手工艺人缺少稳定的线上展示窗口；消费者难以了解工艺来源与传承人背景；交易链路分散，无法沉淀订单与用户数据。

本系统面向非遗手工艺品的展示与线上订购场景，构建一个微信小程序，让消费者能够浏览非遗品类、了解工艺与传承人信息，并完成加购、下单、查看订单的完整闭环。

### 1.2 设计定位

系统定位不是通用电商，而是"让手艺被看见的订购入口"。每一件商品都必须携带工艺、产地、传承人三项背景信息，展示与订购并重。这一差异决定了页面结构、数据结构与视觉风格，而不是把普通商城的模板套用到非遗题材上。

### 1.3 目标用户

| 角色 | 说明 | 主要诉求 |
| --- | --- | --- |
| 消费者 | 对非遗文化、手工艺品感兴趣的微信用户 | 浏览品类、了解工艺故事、放心下单、查看订单 |
| 运营人员 | 商品与订单维护者（二期通过 Web 管理后台参与） | 上架商品、处理订单 |

### 1.4 设计目标

1. 用户可在一个小程序内完成「浏览品类 -> 查看详情 -> 加入购物车 -> 提交订单 -> 查看订单」全流程。
2. 商品信息完整表达非遗属性：工艺、产地、材质与创作背景。
3. 后端提供统一、稳定的 REST 接口，小程序与管理后台共用同一套业务逻辑。
4. 项目结构清晰可扩展，二期新增 Web 管理后台时不需要重构现有代码。

---

## 2. 需求范围

### 2.1 本期实现

| 模块 | 内容 |
| --- | --- |
| 数据库 | 建库建表、索引、种子数据脚本 |
| 后端基础 | Spring Boot 骨架、统一返回、全局异常、JWT 鉴权拦截器、跨域配置 |
| 微信登录 | `wx.login` 换取 openid、用户建档、自定义 token 签发 |
| 图片存储 | 本地磁盘存储 + 静态资源映射，不引入对象存储 |
| 商品展示 | 首页、分类、搜索、商品详情 |
| 首页宣传视频 | 搜索栏与轮播图之间的非遗宣传片，静音循环播放，视频文件存服务器本地 |
| 交易流程 | 购物车、确认订单、订单创建、模拟支付、订单记录与详情 |
| 个人中心 | 微信头像昵称、收货地址、收藏、订单状态入口 |
| 商品评价 | 详情页展示评价（本期为种子数据只读） |

### 2.2 本期不实现

| 项 | 原因 |
| --- | --- |
| Web 管理后台 | 按用户要求延后，后端预留 `/api/admin/**` 目录与鉴权分支 |
| 真实微信支付 | 需企业主体与商户号，本期用模拟支付替代 |
| 物流轨迹查询 | 依赖第三方物流接口，收益低 |
| 退款与售后 | 交易闭环之外的独立流程 |
| 发布评价 | 依赖已完成的订单，放二期 |
| 优惠券、积分、拼团 | 与非遗展示定位不匹配，避免功能膨胀 |

### 2.3 二期预留

后端按"端"分包的目录结构、`goods_spec` 多规格表、`/api/admin/**` 鉴权分支在本期就落地，二期只需新增管理端接口与前端工程，不改动现有模块。

---

## 3. 技术选型与总体架构

### 3.1 技术选型

| 层次 | 技术 | 版本 | 说明 |
| --- | --- | --- | --- |
| 小程序端 | 原生 WXML / WXSS / JS | 微信开发者工具 Stable | 不引入编译工具链，调试直观 |
| 后端框架 | Spring Boot | 3.5.x | 自动装配、内嵌 Tomcat |
| JDK | Java | 17 及以上（开发环境使用 20） | Spring Boot 3.x 最低要求 17 |
| 构建工具 | Maven | 3.9.x | 依赖管理与打包 |
| ORM | MyBatis-Plus | 3.5.x | 单表 CRUD 自动生成，复杂查询写 XML |
| 数据库 | MySQL | 8.0 | InnoDB、utf8mb4 |
| 连接池 | HikariCP | Spring Boot 内置 | 默认连接池 |
| 鉴权 | JJWT | 0.12.x | 签发与校验自定义 token |
| 密码加密 | BCrypt | Spring Security Crypto | 二期管理员密码使用 |
| 工具库 | Lombok、Hutool | 稳定版 | 简化样板代码与常用工具 |
| 参数校验 | Spring Validation | 内置 | `@Valid` 与注解校验入参 |

小程序端不引入第三方 UI 库，组件全部自建，避免风格失控与包体积膨胀。

### 3.2 总体架构

```
    +---------------------------+
    |       微信小程序端         |
    |   pages / components / api |
    +-------------+-------------+
                  | HTTPS + JSON
                  v
    +---------------------------+
    |     Spring Boot 后端       |
    |   controller -> service   |
    |        -> mapper          |
    +------+-------------+------+
           |             |
           v             v
    +------------+  +-------------------+
    |  MySQL 8.0 |  | 本地磁盘 upload/  |
    |            |  | 静态资源映射       |
    +------------+  +-------------------+
```

分层职责：

- 小程序端只负责界面渲染与交互，不保存业务数据，所有数据通过 `api/` 层请求后端。
- 后端是唯一数据出口，`controller` 负责协议转换，`service` 承载业务逻辑，`mapper` 负责持久化。
- 图片存服务器本地目录，数据库只存相对路径，由静态资源映射对外提供 URL。

### 3.3 微信登录与鉴权方案

登录时序：

```
小程序                    后端                    微信服务器
  |  wx.login() 取 code    |                         |
  |----------------------->|                         |
  |                        | code + AppID + Secret   |
  |                        |------------------------>|
  |                        | openid + session_key    |
  |                        |<------------------------|
  |                        | 按 openid 查询或建档     |
  |                        | 签发自定义 JWT           |
  | token + 用户信息        |<------------------------|
  |<-----------------------|                         |
  | 写入 storage           |                         |
```

要点：

1. `AppID` 与 `AppSecret` 只存在后端 `application.yml`，不能下发到小程序。
2. 头像昵称使用 `button open-type="chooseAvatar"` 与 `input type="nickname"` 采集。`wx.getUserProfile` 已受限，不使用。
3. token 放在请求头 `Authorization: Bearer <token>`，由 `LoginInterceptor` 统一校验。
4. 放行白名单：登录接口、首页、分类、商品列表与详情、图片静态资源。这些接口允许游客访问，便于未登录用户先浏览再决定登录。
5. 需要登录的接口：购物车、订单、地址、收藏、个人资料、上传。

二期管理员鉴权使用独立链路：`/api/admin/**` 校验管理员 token 并检查 `role`，两条链的 token 互不通用。

### 3.4 图片与视频本地存储方案

| 环节 | 做法 |
| --- | --- |
| 上传 | `POST /api/upload/avatar` 接收 `MultipartFile`，按业务与日期分目录写入 `${guyi.upload.path}` |
| 命名 | UUID + 原扩展名，避免中文名与重名冲突 |
| 图片校验 | 限制扩展名（jpg/jpeg/png/webp），单文件不超过 5MB |
| 视频校验 | 限制扩展名（mp4），单文件不超过 50MB；实际投放建议压缩到 20MB 以内 |
| 入库 | 数据库保存相对路径，如 `/static/upload/goods/2026/10/01/xxx.jpg`、`/static/upload/video/promo.mp4` |
| 对外访问 | `WebMvcConfig#addResourceHandlers` 将 `/static/upload/**` 映射到上传目录 |
| 前端使用 | `request.js` 导出 `resolveMediaUrl(path)`，图片与视频共用同一函数，拼接后端域名后交给 `<image>` 或 `<video>` |

开发联调说明：小程序正式环境要求请求域名必须是已备案的 HTTPS 域名。开发阶段在微信开发者工具中勾选"不校验合法域名、web-view、TLS 版本以及 HTTPS 证书"；真机预览时将 `request.js` 中的 `baseURL` 改为局域网 IP。

上传入口按角色划分。商品图片由管理员上传，用户只上传自己的头像与评价图，二者走不同的接口与鉴权链路：

| 场景 | 本期做法（无管理后台） | 二期做法（有管理后台） |
| --- | --- | --- |
| 商品图片 | 开发时把图片文件放入 `upload/goods/`，在 `02_seed.sql` 中直接写入相对路径，不经过接口 | 管理员在后台通过 `/api/admin/upload/goods` 上传 |
| 首页宣传视频 | 把视频文件放入 `upload/video/`，在 `02_seed.sql` 中写入 `home_video` 表 | 管理员在后台通过 `/api/admin/upload/video` 上传 |
| 用户头像 | 小程序调用 `/api/upload/avatar` 上传 `chooseAvatar` 返回的临时文件 | 不变 |
| 评价晒图 | 本期不实现 | 用户在小程序端通过 `/api/upload/review` 上传 |

也就是说，上传接口按用途拆分，而不是一个通用接口包打天下：用户端接口校验用户 token，管理端接口校验管理员 token，权限边界清晰，二期接入后台时直接复用存储与静态映射逻辑，不需要改动文件落盘方式。

---

## 4. 功能设计

### 4.1 功能结构

```
古艺手作甄选
|-- 首页
|   |-- 搜索框
|   |-- 非遗宣传视频（静音循环播放）
|   |-- 专题轮播
|   |-- 分类快捷入口
|   `-- 甄选好物瀑布流
|-- 分类
|   |-- 一级分类切换
|   |-- 二级分类与排序筛选
|   `-- 商品网格
|-- 商品详情
|   |-- 图片轮播与预览
|   |-- 价格、标题、工艺标签
|   |-- 规格与数量选择
|   |-- 匠心故事 / 制作工艺 / 规格参数
|   |-- 商品评价
|   `-- 底部操作栏（客服、收藏、加购、立即购买）
|-- 搜索
|-- 购物车
|-- 订单
|   |-- 确认订单
|   |-- 订单记录（全部 / 待付款 / 待发货 / 待收货 / 已完成）
|   `-- 订单详情
|-- 我的
|   |-- 用户信息
|   |-- 订单状态入口
|   |-- 我的收藏
|   |-- 收货地址
|   `-- 关于与客服
`-- 辅助页面
    |-- 登录授权
    `-- 地址编辑
```

底部 tabBar 设置 4 项：首页、分类、购物车、我的。订单记录从"我的"进入，符合低频但需集中查看的使用特征。

### 4.2 功能点清单

| 编号 | 模块 | 功能点 | 说明 | 优先级 |
| --- | --- | --- | --- | --- |
| F01 | 登录 | 微信一键登录 | code 换 openid，自动建档 | 高 |
| F02 | 登录 | 头像昵称完善 | chooseAvatar + nickname 输入 | 高 |
| F03 | 首页 | 首页聚合数据 | 一次请求返回宣传视频、轮播、分类与热门商品 | 高 |
| F04 | 首页 | 宣传视频循环播放 | 进入首页静音自动播放并持续循环，离开暂停，重新进入小程序重播 | 高 |
| F05 | 首页 | 搜索入口 | 关键词搜索商品 | 中 |
| F06 | 分类 | 分类树浏览 | 一级 / 二级分类切换 | 高 |
| F07 | 分类 | 排序与筛选 | 综合、销量、价格升降、最新 | 中 |
| F08 | 商品 | 商品列表分页 | 支持分类与关键词组合查询 | 高 |
| F09 | 商品 | 商品详情 | 图片、价格、规格、详情富文本、评价 | 高 |
| F10 | 商品 | 图片预览 | 点击大图全屏滑动查看 | 低 |
| F11 | 商品 | 收藏与取消收藏 | 需登录 | 中 |
| F12 | 购物车 | 加入购物车 | 未登录先跳登录，成功后回跳 | 高 |
| F13 | 购物车 | 数量修改 | 步进器加减，校验库存 | 高 |
| F14 | 购物车 | 勾选与合计 | 单选、全选、实时合计 | 高 |
| F15 | 购物车 | 删除与清空 | 左滑删除 | 中 |
| F16 | 订单 | 确认订单 | 地址、商品清单、运费、合计 | 高 |
| F17 | 订单 | 提交订单 | 事务内生成订单、快照、扣库存 | 高 |
| F18 | 订单 | 模拟支付 | 状态由待付款改为待发货 | 高 |
| F19 | 订单 | 订单记录 | 按状态分页查询 | 高 |
| F20 | 订单 | 订单详情 | 商品快照、金额、状态时间轴 | 高 |
| F21 | 订单 | 取消订单 | 仅待付款可取消，回滚库存 | 中 |
| F22 | 我的 | 个人中心 | 用户信息与功能入口 | 高 |
| F23 | 我的 | 收货地址 | 增删改查、设置默认地址 | 高 |
| F24 | 我的 | 我的收藏 | 收藏列表与取消 | 中 |

### 4.3 页面设计

| 页面 | 路径 | 主要内容 | 关键交互 |
| --- | --- | --- | --- |
| 首页 | `pages/index/index` | 搜索、宣传视频、轮播、分类入口、商品瀑布流 | 静音循环播放、离开暂停、静音切换、下拉刷新、上拉加载 |
| 分类 | `pages/category/category` | 左侧一级分类、右侧二级与商品网格 | 分类切换、排序筛选 |
| 搜索 | `pages/search/search` | 搜索框、历史词、结果列表 | 关键词搜索、空结果提示 |
| 商品详情 | `pages/goods/detail` | 轮播、价格、规格、详情、评价、底部操作栏 | 选规格、预览大图、加购、立即购买 |
| 购物车 | `pages/cart/cart` | 商品列表、勾选、步进器、底部结算 | 勾选、改数量、删除、结算 |
| 确认订单 | `pages/order/confirm` | 收货地址、商品清单、备注、金额 | 选地址、提交订单 |
| 订单记录 | `pages/order/list` | 5 个状态 tab、订单卡片与操作按钮 | 切 tab、付款、取消、查看详情 |
| 订单详情 | `pages/order/detail` | 状态、商品快照、金额、收货信息、时间轴 | 付款、取消、复制单号 |
| 我的 | `pages/mine/mine` | 用户卡、订单四宫格、功能列表 | 登录、跳转各功能页 |
| 登录 | `pages/login/login` | 授权说明、微信登录按钮 | 登录并回跳 |
| 收藏 | `pages/favorite/favorite` | 收藏商品列表 | 取消收藏、跳详情 |
| 地址 | `pages/address/list` `pages/address/edit` | 地址列表与编辑表单 | 新增、编辑、设默认、删除 |

关键页面设计要点：

- 首页：图片为主角，双列瀑布流卡片包含封面、标题（最多两行）、价格、一个工艺标签。不做营销式大 banner 堆叠，信息密度紧凑。
- 首页宣传视频：位于搜索栏与轮播图之间，撑满内容区宽度，固定 16:9 比例，圆角与商品卡片保持一致。进入首页静音自动播放并持续循环，离开首页暂停，回到首页继续；右上角提供静音开关，未加载完成或加载失败时显示封面图。
- 商品详情：底部固定操作栏常驻；未选规格时点击加购弹出规格弹层，选择完成后才允许提交。
- 购物车：商品卡片列表，包含封面、标题、规格、单价与步进器；未登录时展示空状态引导登录。
- 订单记录：状态 tab 吸顶，列表项显示商品缩略图、规格、金额、状态与对应操作按钮。

---

## 5. 数据库设计

### 5.1 表清单

| 表名 | 说明 | 本期使用 |
| --- | --- | --- |
| `user` | 微信用户 | 是 |
| `address` | 收货地址 | 是 |
| `category` | 商品分类（树形） | 是 |
| `goods` | 手工艺品 | 是 |
| `goods_spec` | 商品规格（SKU） | 是（本期单规格） |
| `cart` | 购物车 | 是 |
| `orders` | 订单主表 | 是 |
| `order_item` | 订单商品明细 | 是 |
| `favorite` | 收藏 | 是 |
| `review` | 商品评价 | 是（只读展示） |
| `banner` | 首页轮播 | 是 |
| `home_video` | 首页宣传视频 | 是 |

数据库统一使用 InnoDB 引擎与 utf8mb4 字符集。表名避免使用 MySQL 保留字（订单表命名为 `orders`），建表语句中统一使用反引号。

### 5.2 建表语句

```sql
CREATE DATABASE IF NOT EXISTS `guyi_craft`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `guyi_craft`;

-- 用户表
CREATE TABLE `user` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid`      VARCHAR(64)  NOT NULL COMMENT '微信 openid，用户在本小程序内的唯一标识',
  `unionid`     VARCHAR(64)  DEFAULT NULL COMMENT '微信 unionid，同一开放平台账号下唯一',
  `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '用户昵称',
  `avatar_url`  VARCHAR(255) DEFAULT NULL COMMENT '头像图片相对路径',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `gender`      TINYINT      NOT NULL DEFAULT 0 COMMENT '性别：0未知 1男 2女',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：1正常 0禁用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`) COMMENT '同一微信用户只允许一条记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表';

-- 收货地址表
CREATE TABLE `address` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     INT          NOT NULL COMMENT '用户ID（关联 user.id）',
  `name`        VARCHAR(32)  NOT NULL COMMENT '收货人姓名',
  `phone`       VARCHAR(20)  NOT NULL COMMENT '收货人手机号',
  `province`    VARCHAR(32)  DEFAULT NULL COMMENT '省',
  `city`        VARCHAR(32)  DEFAULT NULL COMMENT '市',
  `district`    VARCHAR(32)  DEFAULT NULL COMMENT '区/县',
  `detail`      VARCHAR(255) NOT NULL COMMENT '详细地址（街道、门牌号）',
  `is_default`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认地址：1是 0否',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`) COMMENT '按用户查询地址列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 分类表
CREATE TABLE `category` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id`   INT          NOT NULL DEFAULT 0 COMMENT '父分类ID，0 表示一级分类',
  `name`        VARCHAR(32)  NOT NULL COMMENT '分类名称',
  `icon`        VARCHAR(255) DEFAULT NULL COMMENT '分类图标相对路径',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序号，值越小越靠前',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`) COMMENT '按父分类查询子分类'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 手工艺品表
CREATE TABLE `goods` (
  `id`              INT           NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id`     INT           NOT NULL COMMENT '分类ID（关联 category.id，建议挂在末级分类）',
  `title`           VARCHAR(64)   NOT NULL COMMENT '商品标题',
  `subtitle`        VARCHAR(128)  DEFAULT NULL COMMENT '副标题，一句话亮点',
  `cover_img`       VARCHAR(255)  NOT NULL COMMENT '封面图相对路径，列表页展示',
  `images`          JSON          COMMENT '轮播图相对路径数组，详情页展示',
  `detail_html`     MEDIUMTEXT    COMMENT '详情富文本，含工艺介绍与传承人背景',
  `price`           DECIMAL(10,2) NOT NULL COMMENT '售价（元）',
  `original_price`  DECIMAL(10,2) DEFAULT NULL COMMENT '原价/划线价（元），无折扣时为空',
  `stock`           INT           NOT NULL DEFAULT 0 COMMENT '可售库存，下单减少、取消回滚',
  `sales`           INT           NOT NULL DEFAULT 0 COMMENT '累计销量，只增不减',
  `unit`            VARCHAR(8)    NOT NULL DEFAULT '件' COMMENT '计量单位，如件/套/幅/对',
  `craft`           VARCHAR(32)   DEFAULT NULL COMMENT '工艺，如青花瓷、苏绣',
  `origin`          VARCHAR(64)   DEFAULT NULL COMMENT '产地，如江西景德镇',
  `material`        VARCHAR(64)   DEFAULT NULL COMMENT '材质，如桑蚕丝、红木',
  `status`          TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `sort`            INT           NOT NULL DEFAULT 0 COMMENT '排序号，值越小越靠前',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`) COMMENT '按分类查询商品',
  KEY `idx_status_sort` (`status`, `sort`) COMMENT '列表页按状态过滤并按排序号排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手工艺品表';

-- 商品规格表
CREATE TABLE `goods_spec` (
  `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `goods_id`    INT           NOT NULL COMMENT '商品ID（关联 goods.id）',
  `spec_json`   JSON          COMMENT '规格键值对，如 {"尺寸":"20cm","颜色":"青花"}',
  `price`       DECIMAL(10,2) NOT NULL COMMENT '该规格售价（元）',
  `stock`       INT           NOT NULL DEFAULT 0 COMMENT '该规格可售库存',
  `image`       VARCHAR(255)  DEFAULT NULL COMMENT '规格图片相对路径',
  `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_goods` (`goods_id`) COMMENT '按商品查询规格列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 购物车表
CREATE TABLE `cart` (
  `id`          INT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     INT      NOT NULL COMMENT '用户ID（关联 user.id）',
  `goods_id`    INT      NOT NULL COMMENT '商品ID（关联 goods.id）',
  `spec_id`     INT      DEFAULT NULL COMMENT '规格ID（关联 goods_spec.id），无规格时为空',
  `quantity`    INT      NOT NULL DEFAULT 1 COMMENT '购买数量',
  `checked`     TINYINT  NOT NULL DEFAULT 1 COMMENT '是否勾选：1勾选 0未勾选',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入购物车时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_goods_spec` (`user_id`, `goods_id`, `spec_id`) COMMENT '同一商品同一规格只保留一行'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单主表
CREATE TABLE `orders` (
  `id`             INT           NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no`       VARCHAR(32)   NOT NULL COMMENT '订单号，对外展示且唯一',
  `user_id`        INT           NOT NULL COMMENT '下单用户ID（关联 user.id）',
  `total_amount`   DECIMAL(10,2) NOT NULL COMMENT '商品总额（元）',
  `freight`        DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费（元）',
  `pay_amount`     DECIMAL(10,2) NOT NULL COMMENT '应付金额＝商品总额＋运费（元）',
  `status`         TINYINT       NOT NULL DEFAULT 0 COMMENT '订单状态：0待付款 1待发货 2待收货 3已完成 4已取消',
  `receiver_name`  VARCHAR(32)   NOT NULL COMMENT '收货人姓名，下单时快照',
  `receiver_phone` VARCHAR(20)   NOT NULL COMMENT '收货人手机号，下单时快照',
  `receiver_addr`  VARCHAR(255)  NOT NULL COMMENT '收货地址，下单时拼接快照',
  `remark`         VARCHAR(255)  DEFAULT NULL COMMENT '买家备注',
  `pay_time`       DATETIME      DEFAULT NULL COMMENT '支付时间',
  `ship_time`      DATETIME      DEFAULT NULL COMMENT '发货时间',
  `finish_time`    DATETIME      DEFAULT NULL COMMENT '完成时间',
  `cancel_time`    DATETIME      DEFAULT NULL COMMENT '取消时间',
  `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`) COMMENT '订单号唯一',
  KEY `idx_user_status` (`user_id`, `status`) COMMENT '按用户与订单状态查询订单列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单明细表（商品快照）
CREATE TABLE `order_item` (
  `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id`    INT           NOT NULL COMMENT '订单ID（关联 orders.id）',
  `goods_id`    INT           NOT NULL COMMENT '商品ID（关联 goods.id，仅用于溯源）',
  `spec_id`     INT           DEFAULT NULL COMMENT '规格ID（关联 goods_spec.id），无规格时为空',
  `goods_title` VARCHAR(64)   NOT NULL COMMENT '下单时标题快照',
  `goods_img`   VARCHAR(255)  NOT NULL COMMENT '下单时图片快照',
  `spec_text`   VARCHAR(128)  DEFAULT NULL COMMENT '下单时规格文本快照，如 尺寸:20cm',
  `price`       DECIMAL(10,2) NOT NULL COMMENT '下单时单价（元）',
  `quantity`    INT           NOT NULL COMMENT '购买数量',
  `subtotal`    DECIMAL(10,2) NOT NULL COMMENT '小计＝单价×数量（元）',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`) COMMENT '按订单查询明细'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 收藏表
CREATE TABLE `favorite` (
  `id`          INT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     INT      NOT NULL COMMENT '用户ID（关联 user.id）',
  `goods_id`    INT      NOT NULL COMMENT '商品ID（关联 goods.id）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_goods` (`user_id`, `goods_id`) COMMENT '同一用户同一商品只允许收藏一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 商品评价表
CREATE TABLE `review` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `goods_id`    INT          NOT NULL COMMENT '商品ID（关联 goods.id）',
  `user_id`     INT          NOT NULL COMMENT '评价用户ID（关联 user.id）',
  `rating`      TINYINT      NOT NULL DEFAULT 5 COMMENT '评分：1到5星',
  `content`     VARCHAR(500) DEFAULT NULL COMMENT '评价文字内容',
  `images`      JSON         COMMENT '晒图相对路径数组',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`id`),
  KEY `idx_goods` (`goods_id`) COMMENT '按商品查询评价列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 首页轮播表
CREATE TABLE `banner` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `image`       VARCHAR(255) NOT NULL COMMENT '轮播图相对路径',
  `link_type`   VARCHAR(16)  DEFAULT NULL COMMENT '跳转类型：goods商品 category分类 none不跳转',
  `link_id`     INT          DEFAULT NULL COMMENT '跳转目标ID，配合 link_type 使用',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序号，值越小越靠前',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播表';

-- 首页宣传视频表
CREATE TABLE `home_video` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`       VARCHAR(64)  DEFAULT NULL COMMENT '视频标题',
  `url`         VARCHAR(255) NOT NULL COMMENT '视频文件相对路径',
  `cover_img`   VARCHAR(255) DEFAULT NULL COMMENT '封面图相对路径，播放前展示',
  `duration`    INT          DEFAULT NULL COMMENT '视频时长（秒）',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序号，值越小越靠前',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_sort` (`status`, `sort`) COMMENT '按状态过滤并排序取首条'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页宣传视频表';
```

### 5.3 设计要点

1. 订单明细采用快照设计。商品标题、图片、规格文本、单价在下单瞬间写入 `order_item`，后续商品改价或下架不影响历史订单展示，这是电商系统最基本的一致性要求。
2. 金额统一使用 `DECIMAL(10,2)`，禁止使用浮点类型，避免精度误差。
3. 购物车与收藏使用联合唯一索引，从数据库层面防止重复记录。
4. 库存扣减与订单创建在同一事务内完成，任何一步失败整体回滚。
5. 分类使用 `parent_id` 自关联表示树形结构，本期支持两级，结构上兼容多级。
6. `images` 与 `spec_json` 使用 JSON 类型，由 MyBatis-Plus 的类型处理器负责序列化。
7. 传承人不单独建模。传承人姓名、非遗级别与背景介绍作为商品详情内容写入 `detail_html`，不建独立表、不做独立页面。若后续确需按传承人检索商品，再评估拆分独立表。
8. 首页宣传视频独立成 `home_video` 表，不与 `banner` 混用。视频是单条媒体内容，轮播是多张可跳转卡片，字段与运营节奏都不同，混在一张表里只会引入 `type` 判断分支。首页取 `status = 1` 且 `sort` 最小的一条，没有则前端隐藏视频模块。
9. 视频文件不进小程序包，只存服务器磁盘并在库中保存相对路径。小程序主包有 2MB 限制，视频必须走网络地址。
10. 主键与关联字段统一使用 `INT`。本系统各表数据量预计不超过两亿行，`INT` 有符号上限约 21 亿，留出约十倍余量，同时索引体积比 `BIGINT` 小一半。代价是不兼容雪花算法这类分布式 ID 方案，若将来要分库分表，需先迁移主键类型。所有关联字段的类型必须与所引用主键完全一致，否则 MySQL 会做隐式类型转换，导致索引失效。
11. 字段命名与注释规范：表名、字段名统一使用小写下划线，不用拼音、不用无意义缩写；关联字段以 `_id` 结尾，时间字段以 `_time` 结尾，金额字段以 `_amount` 结尾或明确为价格；每个字段都必须写 `COMMENT`，枚举类字段在注释中列全取值（如 `1上架 0下架`），关联字段在注释中标注来源（如 `关联 goods.id`）。用 `SHOW FULL COLUMNS FROM goods;` 或数据库工具打开表即可读懂含义，不必回翻设计文档。

---

## 6. 接口设计

### 6.1 接口规范

| 项 | 约定 |
| --- | --- |
| 协议 | HTTP/HTTPS，请求与响应均为 JSON |
| 前缀 | 用户端 `/api/**`，管理端二期 `/api/admin/**` |
| 鉴权 | 请求头 `Authorization: Bearer <token>` |
| 统一返回 | `{ "code": 200, "msg": "success", "data": {} }` |
| 分页返回 | `{ "list": [], "total": 0, "page": 1, "size": 10 }` |
| 分页入参 | `page` 从 1 开始，`size` 默认 10，上限 50 |
| 时间格式 | `yyyy-MM-dd HH:mm:ss` |

状态码约定：

| code | 含义 |
| --- | --- |
| 200 | 成功 |
| 400 | 参数校验失败 |
| 401 | 未登录或 token 失效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 业务异常或服务器错误 |

### 6.2 接口清单

登录与用户：

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/login` | 微信登录，传入 code，返回 token 与用户信息 | 否 |
| GET | `/api/user/profile` | 获取当前用户信息 | 是 |
| PUT | `/api/user/profile` | 更新昵称与头像 | 是 |

首页与商品：

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| GET | `/api/home` | 宣传视频、轮播、分类与热门商品聚合数据 | 否 |
| GET | `/api/category/tree` | 分类树 | 否 |
| GET | `/api/goods` | 商品列表，支持 categoryId、keyword、sort、page、size | 否 |
| GET | `/api/goods/{id}` | 商品详情，含轮播、评价、是否已收藏 | 否 |

商品详情接口对游客开放，未登录时 `favorite` 字段固定返回 `false`，前端据此展示为未收藏状态。

首页接口的 `video` 字段返回 `{ title, url, coverImg, duration }`，没有启用中的视频时返回 `null`，前端据此隐藏整个视频模块，不显示空占位。

购物车：

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| GET | `/api/cart` | 购物车列表 | 是 |
| POST | `/api/cart` | 加入购物车 | 是 |
| PUT | `/api/cart/{id}` | 修改数量或勾选状态 | 是 |
| DELETE | `/api/cart/{id}` | 删除单项 | 是 |

订单：

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/order/preview` | 结算预览，返回商品清单与金额 | 是 |
| POST | `/api/order/create` | 创建订单（事务） | 是 |
| POST | `/api/order/{id}/pay` | 模拟支付，状态 0 转 1 | 是 |
| POST | `/api/order/{id}/cancel` | 取消订单并回滚库存 | 是 |
| GET | `/api/order/list` | 订单列表，按 status 筛选 | 是 |
| GET | `/api/order/{id}` | 订单详情 | 是 |

地址、收藏、上传：

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| GET | `/api/address` | 地址列表 | 是 |
| POST | `/api/address` | 新增地址 | 是 |
| PUT | `/api/address/{id}` | 修改地址 | 是 |
| DELETE | `/api/address/{id}` | 删除地址 | 是 |
| PUT | `/api/address/{id}/default` | 设为默认地址 | 是 |
| GET | `/api/favorite` | 收藏列表 | 是 |
| POST | `/api/favorite/{goodsId}` | 收藏 | 是 |
| DELETE | `/api/favorite/{goodsId}` | 取消收藏 | 是 |
| POST | `/api/upload/avatar` | 用户头像上传 | 是 |

---

## 7. 项目结构与目录设计

### 7.1 仓库整体结构

```
基于微信小程序的非遗手工艺品展示订购系统的设计与实现/
|-- miniprogram/        小程序端（本期）
|-- server/             后端服务（本期）
|-- sql/                建表与种子数据脚本
|-- upload/             图片与视频落盘目录（运行期生成，不纳入版本控制）
|-- docs/               设计文档与答辩材料
`-- README.md
```

二期新增 `admin-web/` 时与 `miniprogram/`、`server/` 平级，共用同一个后端服务与数据库，不产生数据同步问题。

### 7.2 后端目录

```
server/
|-- pom.xml
`-- src/main/
    |-- java/com/guyi/
    |   |-- Application.java          启动类
    |   |-- common/
    |   |   |-- Result.java               统一返回
    |   |   |-- PageResult.java           分页返回
    |   |   |-- BusinessException.java    业务异常
    |   |   |-- GlobalExceptionHandler.java
    |   |   |-- LoginInterceptor.java     登录校验
    |   |   `-- PageQuery.java            分页入参基类
    |   |-- config/
    |   |   |-- WebMvcConfig.java         静态资源映射 + 拦截器注册
    |   |   |-- CorsConfig.java           跨域配置
    |   |   |-- MybatisPlusConfig.java    分页插件 + 自动填充
    |   |   `-- WxConfig.java             微信小程序配置读取
    |   |-- controller/
    |   |   |-- LoginController.java
    |   |   |-- HomeController.java
    |   |   |-- GoodsController.java
    |   |   |-- CategoryController.java
    |   |   |-- CartController.java
    |   |   |-- OrderController.java
    |   |   |-- UserController.java
    |   |   |-- AddressController.java
    |   |   |-- FavoriteController.java
    |   |   `-- UploadController.java
    |   |-- service/                      （业务逻辑，每个模块一个类，不抽接口）
    |   |   |-- LoginService.java
    |   |   |-- HomeService.java
    |   |   |-- GoodsService.java
    |   |   |-- CategoryService.java
    |   |   |-- CartService.java
    |   |   |-- OrderService.java
    |   |   |-- UserService.java
    |   |   |-- AddressService.java
    |   |   |-- FavoriteService.java
    |   |   `-- UploadService.java
    |   |-- mapper/                       （接口，每个表一个，MyBatis 动态代理生成实现）
    |   |   |-- UserMapper.java
    |   |   |-- AddressMapper.java
    |   |   |-- CategoryMapper.java
    |   |   |-- GoodsMapper.java
    |   |   |-- GoodsSpecMapper.java
    |   |   |-- CartMapper.java
    |   |   |-- OrderMapper.java
    |   |   |-- OrderItemMapper.java
    |   |   |-- FavoriteMapper.java
    |   |   |-- ReviewMapper.java
    |   |   |-- BannerMapper.java
    |   |   `-- HomeVideoMapper.java
    |   |-- Entity/
    |   |-- Dto/
    |   |-- ViewObject/
    |   `-- util/
    |       |-- JwtUtil.java
    |       |-- WxUtil.java
    |       `-- OrderNoUtil.java
    `-- resources/
        |-- application.yml
        |-- application-dev.yml
        `-- mapper/                      （仅复杂查询需要，简单 CRUD 由 BaseMapper 提供）
            `-- GoodsMapper.xml
```

分层约定：

- `controller` 只做参数接收与结果封装，禁止直接调用 `mapper`。
- `service` 承载业务规则与事务边界，每个模块一个类，`@Transactional` 标注在方法上。只有一个实现时不抽接口，避免产生只有方法声明的空壳文件。
- `mapper` 必须是接口，且每个表对应一个 Mapper 接口。MyBatis 靠"接口 + 动态代理"在运行期生成实现，不需要也不应该手写实现类。SQL 位置按复杂度分层：单表增删改查用 `BaseMapper` 自带方法；单表条件查询与排序用 `LambdaQueryWrapper` 在 Service 中组装；复杂联表、聚合统计与需要优化的查询写同名的 `resources/mapper/*.xml`。个别一行 SQL 可以用 `@Select` 注解，但带动态条件的复杂 SQL 优先 XML，避免在注解里拼 `<script>` 字符串。
- `entity` 与数据库表一一对应，`dto` 只用于入参，`vo` 只用于出参，三者不混用。
- 二期管理端接口放 `controller/admin/`，`service` 层与小程序端共用，避免逻辑重复。

### 7.3 小程序目录

```
miniprogram/
|-- app.js                       全局逻辑（登录、globalData）
|-- app.json                     页面注册 + tabBar + 窗口配置
|-- app.wxss                     全局样式变量与通用类
|-- pages/
|   |-- index/index              首页
|   |-- category/category        分类
|   |-- search/search            搜索
|   |-- goods/detail             商品详情
|   |-- cart/cart                购物车
|   |-- order/confirm            确认订单
|   |-- order/list               订单记录
|   |-- order/detail             订单详情
|   |-- mine/mine                我的
|   |-- login/login              登录
|   |-- favorite/favorite        我的收藏
|   `-- address/list|edit        地址管理
|-- components/
|   |-- goods-card/              商品卡片
|   |-- price-tag/               价格展示
|   |-- stepper/                 数量步进器
|   |-- empty/                   空状态
|   |-- video-player/            首页宣传视频（静音循环播放与控制）
|   `-- nav-bar/                 自定义导航栏
|-- api/
|   `-- home.js  goods.js  cart.js  order.js  user.js  address.js
|-- utils/
|   |-- request.js               wx.request 封装 + token + 统一错误
|   |-- login.js                 登录状态与登录跳转
|   `-- format.js                价格与时间格式化
`-- static/
    |-- icons/                   页面图标
    |-- tabbar/                  tabBar 图标（81x81 PNG）
    `-- images/                  占位图与空状态图
```

每个页面由四个同名文件组成，缺一不可：`.wxml` 结构、`.wxss` 样式、`.js` 逻辑、`.json` 页面配置。页面必须在 `app.json` 的 `pages` 数组中注册，否则无法跳转。

### 7.4 前端分层约定

```
页面 (.js)  ->  api/*.js  ->  utils/request.js  ->  后端接口
     ^                                                    |
     `------------- setData 刷新界面 <--------------------'
```

| 层 | 职责 | 禁止事项 |
| --- | --- | --- |
| `pages/*` | 渲染与交互 | 不直接写 `wx.request` |
| `api/*.js` | 声明后端接口调用函数 | 不写界面逻辑 |
| `utils/request.js` | 统一拼 baseURL、带 token、处理错误码 | 不写业务判断 |
| `components/*` | 可复用 UI 单元 | 不直接请求网络，数据由父页面传入 |

---

## 8. 关键流程设计

### 8.1 微信登录

1. 小程序启动或进入需登录页面时，在 `app.js` 检查 storage 中是否存在 token。
2. 无 token 则调用 `wx.login` 获取 code，请求 `POST /api/login`。
3. 后端调用 `code2Session` 换取 openid，按 openid 查询用户，不存在则建档。
4. 后端签发 JWT，返回 token 与用户信息，小程序写入 storage 与 `globalData`。
5. 进入"我的"页面时，若昵称为默认值，引导用户通过 `chooseAvatar` 与昵称输入框完善资料。

异常处理：`code2Session` 失败时返回明确错误码；token 过期时拦截器返回 401，前端清除本地 token 并重新登录一次，避免死循环。

### 8.2 浏览与选购

首页、分类、搜索、商品详情均为游客可访问。详情页根据登录态决定收藏与加购行为：未登录时点击加购，先跳转登录页，登录成功后回跳到原详情页并自动完成加购。

回跳实现：跳转登录页时把当前页面路径与参数编码进 `redirect` 参数，登录成功后通过 `wx.navigateBack` 或 `wx.reLaunch` 回到目标页。

### 8.3 下单与模拟支付

```
购物车勾选 -> POST /api/order/preview   返回商品清单、运费、应付金额
         -> 选择收货地址、填写备注
         -> POST /api/order/create
              开启事务：
                1. 校验商品状态与库存
                2. 生成 orders 记录（状态 0 待付款）
                3. 写入 order_item 商品快照
                4. 扣减 goods.stock
                5. 清除已下单的购物车记录
              提交事务 -> 返回订单号
         -> POST /api/order/{id}/pay     模拟支付，状态 0 转 1 待发货
         -> 跳转订单详情
```

库存校验必须在事务内使用带条件的更新语句（`UPDATE goods SET stock = stock - ? WHERE id = ? AND stock >= ?`），根据影响行数判断是否成功，避免并发超卖。

### 8.4 取消订单

仅状态为待付款的订单允许取消。取消时在同一事务内将状态置为 4，并把 `order_item` 中的数量回加到对应商品库存，记录 `cancel_time`。

### 8.5 首页宣传视频播放策略

视频位于首页搜索栏与轮播图之间，宽度撑满内容区，固定 16:9 比例（用 `aspect-ratio` 或固定 rpx 高度写死），避免视频加载完成后页面跳动。

**播放与暂停策略**

视频用 `autoplay`、`muted`、`loop` 三个属性实现"进来就播、播个不停"，不需要任何 storage 标记：

```js
// pages/index/index.js
onShow() {
  if (!this.userPaused) {          // 用户没手动暂停过才续播
    this.videoContext.play()
  }
},

onHide() {
  this.pausingBySystem = true      // 标记为系统暂停，不是用户操作
  this.videoContext.pause()
},

onVideoPause() {
  if (!this.pausingBySystem) {
    this.userPaused = true         // 用户手动点暂停，之后不自动续播
  }
  this.pausingBySystem = false
}
```

这里要区分两种暂停：切 tab 导致的暂停是系统行为，回到首页应该继续播；用户主动点暂停是个人意愿，不应该被自动续播覆盖。用一个 `pausingBySystem` 标记区分，标记只存在页面实例上，不写 storage。

播放规则：

1. 进入首页即静音自动播放，`autoplay`、`muted`、`loop` 三个属性同时开启。微信与手机系统普遍拦截带声音的自动播放，静音是自动播放能生效的前提。
2. 视频在首页停留期间持续循环，播完自动从头开始，不会停在最后一帧。
3. 离开首页（切到其他 tab、跳详情页、打开原生页面）时暂停；回到首页后继续播放，不会每次回到首页都从头开始。
4. 每次重新进入小程序（冷启动）都从头播放一次，符合"打开就有宣传片"的预期。
5. 封面用 `poster`。视频未加载完成或加载失败时，用户看到的是封面，而不是黑屏或空白。
6. 使用组件自带的静音开关 `show-mute-btn`，用户想听声音时点一下即可，不强制播放声音；解除静音后切走再回来仍保持解除状态。
7. 用户手动点击暂停时记录一个页面内的标记，之后切走再回到首页不自动续播，避免和用户操作对着干。这个标记只存在页面实例上，重新进入小程序后恢复自动播放。
8. `binderror` 触发时隐藏视频并保留封面图，同时记录一次日志，不让首页出现残缺区块。

组件写法示意：

```xml
<video
  class="promo-video"
  src="{{video.url}}"
  poster="{{video.coverImg}}"
  autoplay
  muted="{{muted}}"
  loop
  controls
  show-center-play-btn
  show-mute-btn
  auto-pause-if-navigate
  auto-pause-if-open-native
  object-fit="cover"
  bindplay="onVideoPlay"
  bindpause="onVideoPause"
  binderror="onVideoError"
/>
```

调试与验收：在首页停留，视频应循环播放不停；切到分类页再回来，视频应继续播放而不是从头或停住；开发者工具点"重新编译"或真机杀掉进程重进，视频应从头重新播放；手动点暂停后切走再回来，应保持暂停。

---

## 9. 界面设计规范

### 9.1 色彩

| 角色 | 色值 | 用途 |
| --- | --- | --- |
| 主色 | `#A4382A` 朱砂红 | 价格、主按钮、选中态 |
| 辅助色一 | `#4E7C6B` 竹青 | 工艺标签、分类标识 |
| 辅助色二 | `#2F4F6F` 靛蓝 | 次要强调、内容模块标识 |
| 页面背景 | `#FAF7F2` 宣纸白 | 全局底色 |
| 主文字 | `#1F1B18` 墨黑 | 标题与正文 |
| 次文字 | `#7A7168` | 辅助说明、次要信息 |
| 分割线 | `#EDE7DF` | 卡片描边、列表分割 |
| 警示 | `#C0392B` | 删除、取消等破坏性操作 |

色彩策略：以低饱和暖白为底，朱砂红只承担价格与主操作，分类与内容模块用竹青、靛蓝区分，避免全屏单一色系，也避免电商常见的橙红大黄配色。

### 9.2 排版与组件

| 项 | 规范 |
| --- | --- |
| 字号 | 大标题 36rpx，页面标题 32rpx，正文 28rpx，辅助 24rpx |
| 圆角 | 卡片统一 8px，按钮 4px 到 8px |
| 间距 | 页面左右内边距 24rpx，模块间距 24rpx，卡片内边距 20rpx |
| 图片 | 商品卡片封面 1:1 或 4:3，统一使用 `mode="aspectFill"` |
| 视频 | 首页宣传视频固定 16:9，宽度撑满内容区，不随内容撑高，圆角与卡片一致 |
| 图标 | 全局一套 24px 线性图标，线宽 1.5px，不混用风格 |
| tabBar | 4 项，图标导出 81x81 PNG（原生 tabBar 不支持 SVG） |
| 卡片 | 只用于重复列表项与弹层，不嵌套卡片，不做页面区块的卡片化堆叠 |

### 9.3 交互反馈

- 所有网络请求展示加载态，超过 300ms 才显示 loading，避免闪烁。
- 空状态区分"未登录""无数据""无搜索结果"三种文案与插图。
- 破坏性操作（删除地址、取消订单）必须二次确认。
- 表单提交时禁用按钮，防止重复提交订单。

---

## 10. 非功能设计

### 10.1 安全

1. `AppSecret` 与数据库密码只保存在后端配置文件，通过环境变量或 `application-dev.yml` 管理，不提交到公开仓库。
2. 所有涉及用户数据的查询都必须带 `user_id` 条件，防止越权访问他人订单与地址。
3. 上传接口校验文件类型与大小，重命名后落盘，禁止使用原始文件名。
4. 参数校验使用 `@Valid` 与注解，异常统一由全局异常处理器转换为标准返回。

### 10.2 性能

1. 列表接口全部分页，避免一次性返回全量数据。
2. 商品列表、分类树等低频变更数据可加本地缓存，二期视情况引入 Redis。
3. 数据库对分类、状态、用户订单等查询字段建立索引。
4. 首页使用聚合接口，减少小程序端请求次数。

### 10.3 异常处理

| 场景 | 处理方式 |
| --- | --- |
| 参数错误 | 返回 400 与具体字段提示 |
| 未登录 | 返回 401，前端跳转登录页 |
| 库存不足 | 返回 500 与"库存不足"业务提示 |
| 微信接口失败 | 记录日志，返回通用错误提示 |
| 服务器异常 | 全局异常处理器记录堆栈，对外只返回通用文案 |

### 10.4 兼容性

- 基础库版本要求 2.30.0 以上，覆盖 `chooseAvatar` 与 `nickname` 输入能力。
- 图片使用 webp 与 jpg 混合时，注意低版本机型的兼容表现。
- 宣传视频自动播放依赖 `muted`，iOS 与部分安卓机型不允许非静音自动播放，需在真机验证封面、自动播放、循环与静音按钮四处表现。
- 视频建议使用 MP4（H.264 + AAC），单个文件控制在 20MB 以内，过大时首屏加载明显变慢。
- 页面布局使用 rpx 自适应，需在 iPhone 与安卓主流机型上验证文字不溢出、不遮挡。

---

## 11. 开发计划

| 阶段 | 内容 | 交付物 |
| --- | --- | --- |
| M1 骨架 | Maven 工程、配置、统一返回与异常、JWT 拦截器、建表脚本 | `/api/home` 返回真实数据 |
| M2 登录与上传 | 微信登录、用户建档、图片上传与静态映射 | 小程序能登录并显示头像昵称，图片可访问 |
| M3 展示模块 | 首页（含宣传视频）、分类、搜索、商品详情 | 浏览链路完整可用 |
| M4 交易模块 | 购物车、确认订单、下单事务、模拟支付、订单记录 | 下单闭环跑通 |
| M5 个人中心 | 我的、地址管理、收藏 | 个人中心功能完整 |
| M6 收尾 | 种子数据补全、UI 打磨、异常与边界处理、真机联调 | 可演示的完整版本 |

每个阶段都有可验证的出口，M1 结束就应看到接口返回真实数据，而不是等全部功能写完才验证。

---

## 12. 二期扩展：Web 管理后台

### 12.1 目录位置

```
项目根/
|-- miniprogram/
|-- admin-web/       二期新增，与小程序端平级
`-- server/
```

推荐技术栈：Vue 3 + Vite + Element Plus + Pinia + Axios。管理后台不引入独立数据库，通过同一后端服务读写数据。

### 12.2 后端改造点

| 改造项 | 说明 |
| --- | --- |
| 新增 `controller/admin/` | 管理端接口，路径统一为 `/api/admin/**` |
| 新增 `admin_user` 表 | 管理员独立账号体系，不复用微信用户表 |
| 新增鉴权分支 | 拦截器区分用户 token 与管理员 token，并校验 `role` |
| 共用上传目录 | 与小程序端共用 `upload/`，图片只存一份 |
| 复用 service 层 | 商品、订单等业务逻辑与小程序端共用，禁止复制实现 |

### 12.3 管理端功能

登录、数据看板、商品管理（多图上传与规格编辑）、分类管理、首页视频管理、订单管理（发货与状态流转）、用户管理、轮播与专题管理、评价管理。

其中商品管理的多图上传与规格编辑工作量最大。若需压缩工期，可在二期第一版先实现单规格商品维护，跑通后再扩展多规格。

---

## 13. 风险与对策

| 风险 | 影响 | 对策 |
| --- | --- | --- |
| 无小程序 AppID | 无法真机预览 | 开发阶段使用测试号，功能不受影响 |
| 请求域名未备案 | 真机请求被拦截 | 开发期勾选不校验域名，使用局域网 IP；上线前完成备案 |
| 电商类目资质 | 小程序无法发布 | 需选择"商家自营"类目并提供营业执照，部分非遗类目需额外资质 |
| 微信支付无商户号 | 无法真实支付 | 本期采用模拟支付，接口结构与真实支付保持一致，便于后续替换 |
| 图片体积过大 | 加载慢、流量高 | 上传时限制大小，列表页使用缩略图策略 |
| 视频自动播放被系统拦截 | 首屏宣传片不自动播放 | 静音自动播放 + 封面兜底 + 保留手动播放按钮 |
| 宣传视频体积过大 | 首页加载慢、消耗用户流量 | 压缩到 20MB 以内并使用 MP4，必要时后续接入 CDN |
| 循环播放持续占用解码资源 | 首页长时间停留时耗电增加 | 离开首页立即暂停，页面隐藏时停止播放，视频控制在 20MB 以内且时长不宜过长 |
| 无管理后台 | 商品数据只能手工入库 | 本期用 SQL 种子脚本维护，二期接入管理后台 |
| 并发下单超卖 | 库存出现负数 | 事务内使用带库存条件的更新语句并判断影响行数 |

---

## 14. 附录

### 14.1 开发环境

| 工具 | 版本要求 |
| --- | --- |
| JDK | 20 |
| Maven | 3.9 以上 |
| MySQL | 8.0 |
| 微信开发者工具 | 2.01.2510290 |
| 调试基础库 | 2.30.0 及以上 |
| IDE | IntelliJ IDEA / VS Code |
| 接口调试 | Apifox / Postman |

### 14.2 启动步骤

1. 执行 `sql/01_schema.sql` 与 `sql/02_seed.sql` 初始化数据库。
2. 修改 `application-dev.yml` 中的数据库账号密码与微信 AppID/AppSecret。
3. 在 `server/` 目录下执行 `mvn spring-boot:run`。
4. 浏览器访问 `http://localhost:8080/api/home`，返回 JSON 即表示后端就绪。
5. 微信开发者工具导入 `miniprogram/` 目录，填入 AppID 或使用测试号。
6. 在开发者工具中勾选"不校验合法域名、web-view、TLS 版本以及 HTTPS 证书"。
7. 首页展示种子商品即表示一期骨架跑通。

### 14.3 术语说明

小程序端：

| 术语 | 说明 |
| --- | --- |
| WXML | 小程序页面结构语言，作用相当于网页中的 HTML |
| WXSS | 小程序样式语言，作用相当于网页中的 CSS |
| rpx | 小程序响应式长度单位，750rpx 恒等于屏幕宽度 |
| tabBar | 页面底部的固定标签栏，本项目包含首页、分类、购物车、我的 |
| storage | 小程序的本地缓存，用于保存 token、用户信息等 |
| setData | 小程序更新页面数据的核心方法，调用后界面自动刷新 |
| AppID / AppSecret | 小程序的唯一标识与密钥，登录与调用微信接口时使用，只保存在后端 |
| code | `wx.login` 获取的临时登录凭证 |
| code2Session | 微信接口，用 code 换取 openid 与 session_key |
| session_key | 微信会话密钥，用于解密用户敏感数据 |
| unionid | 同一微信开放平台账号下的用户唯一标识 |
| openid | 微信用户在单个小程序内的唯一标识 |
| 合法域名 | 小程序网络请求允许访问的域名，正式环境需 HTTPS 且备案 |

后端与数据库：

| 术语 | 说明 |
| --- | --- |
| JWT | JSON Web Token，后端签发的登录凭证 |
| token | 后端签发的登录凭证，存放于小程序 storage |
| 拦截器 | 请求进入业务方法前统一执行校验的组件，本项目用于校验 token |
| CORS | 跨域资源共享，解决浏览器或开发工具中不同源的请求限制 |
| Mapper | MyBatis 的数据访问接口，每个表对应一个 |
| BaseMapper | MyBatis-Plus 提供的通用增删改查接口，Mapper 继承后免写单表 SQL |
| 动态代理 | 运行期生成接口实现对象的机制，MyBatis 据此实现 Mapper |
| 事务 | 一组数据库操作要么全部成功、要么全部回滚，用于下单扣库存 |
| 唯一索引 | 约束某列或某几列组合不能重复，如 `uk_openid` |
| 联合索引 | 由多列组成的索引，遵循最左匹配原则，如 `(status, sort)` |
| 自关联 | 表通过 parent_id 引用自身，形成树形结构，用于分类表 |
| 冗余字段 | 为提升查询性能而有意重复存储的字段，如 `goods.sales` |
| 静态资源映射 | 把 URL 路径映射到服务器磁盘目录，使图片与视频可被访问 |
| 富文本 | 包含文字与图片的格式化内容，对应 `goods.detail_html` |
| BigDecimal | Java 的精确小数类型，金额字段必须用它而不是 Double |
| 雪花算法 | 生成 19 位分布式唯一 ID 的算法，本项目用 INT 主键，不兼容该算法 |
| entity / dto / vo | 分别对应数据库映射对象、入参对象、出参对象 |

业务概念：

| 术语 | 说明 |
| --- | --- |
| SPU | Standard Product Unit，标准产品单位，对应 `goods` 表的一件商品 |
| 快照 | 下单瞬间写入订单明细的商品信息副本 |
| 模拟支付 | 不调用微信支付，直接把订单状态改为待发货 |
| 规格（SKU） | Stock Keeping Unit，可售的最小库存单位。同一商品的不同尺寸或颜色组合各算一个规格，对应 `goods_spec` 表 |
| 划线价 | 商品折扣前的原价，对应 `original_price`，为空表示无折扣 |
| 逻辑下架 | 通过 status 标记商品或分类下架，但不删除数据 |
| 回滚库存 | 取消订单时把已扣减的数量加回库存 |
| 聚合接口 | 一次请求返回多类数据的接口，如 `/api/home` |
