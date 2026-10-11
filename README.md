# 古艺手作甄选：非遗手工艺品展示订购系统

本期范围：微信小程序端 + 后端服务（不含 Web 管理后台）。

## 目录

```text
miniprogram/   微信小程序端
server/        Spring Boot 后端
sql/           建表与种子数据脚本
upload/        图片与视频落盘目录（运行期生成）
docs/          设计文档与答辩材料
```

## 初始化数据库

```bash
mysql -uroot -p --default-character-set=utf8mb4 < sql/01_schema.sql
mysql -uroot -p --default-character-set=utf8mb4 guyi_craft < sql/02_seed.sql
```

然后修改 `server/src/main/resources/application-dev.yml` 中的数据库账号密码，以及
`guyi.wx.app-id` / `guyi.wx.secret`。

## 启动后端

```bash
cd server
mvn spring-boot:run
```

启动后访问 `http://localhost:8080/api/home`，返回首页聚合 JSON 即表示后端就绪。

## 运行小程序

1. 用微信开发者工具导入 `miniprogram/` 目录。
2. 在开发者工具中勾选“不校验合法域名、web-view、TLS 版本以及 HTTPS 证书”。
3. 真机预览时，把 `miniprogram/utils/request.js` 里的 `BASE_URL` 改为局域网 IP。
4. 如需真机登录，把 `project.config.json` 的 `appid` 和 `application-dev.yml` 的微信配置填成你的 AppID/AppSecret。

## 已实现功能

- M1：数据库建表、种子数据、Spring Boot 骨架、JWT 鉴权、静态资源映射。
- M2：微信登录、用户资料、头像上传。
- M3：首页聚合、分类树、商品列表/搜索、商品详情。
- M4：购物车、收货地址、下单事务、模拟支付、订单列表/详情。
- M5：我的、收藏列表、收藏/取消收藏。

## 待办（M6 收尾）

- 补充真实的商品图、宣传视频、tabBar 图标（放入 `upload/` 对应目录）。
- 真机联调：验证视频自动播放、头像昵称、登录与下单全流程。
