-- 古艺手作甄选：非遗手工艺品展示订购系统
-- 数据库建表脚本
-- 执行方式：mysql -uroot -p < sql/01_schema.sql

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `guyi_craft`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `guyi_craft`;

-- 微信用户表
CREATE TABLE `user` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid`      VARCHAR(64)  NOT NULL COMMENT '微信 openid，用户在本小程序内的唯一标识',
  `unionid`     VARCHAR(64)  DEFAULT NULL COMMENT '微信 unionid，同一开放平台账号下唯一',
  `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '用户昵称',
  `real_name`   VARCHAR(32)  DEFAULT NULL COMMENT '真实姓名',
  `avatar_url`  VARCHAR(255) DEFAULT NULL COMMENT '头像图片相对路径',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `gender`      TINYINT      NOT NULL DEFAULT 0 COMMENT '性别：0未知 1男 2女',
  `birthday`    VARCHAR(10)  DEFAULT NULL COMMENT '生日，YYYY-MM-DD',
  `region`      VARCHAR(64)  DEFAULT NULL COMMENT '地区',
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

-- 商品分类表
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
