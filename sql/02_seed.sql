-- 古艺手作甄选：种子数据脚本
-- 执行方式：mysql -uroot -p guyi_craft < sql/02_seed.sql
-- 说明：图片与视频路径为占位路径，实际文件请放入 upload/ 对应目录。

SET NAMES utf8mb4;

USE `guyi_craft`;

-- 商品分类（一级 + 二级）
INSERT INTO `category` (`id`, `parent_id`, `name`, `icon`, `sort`, `status`) VALUES
(1,  0, '陶瓷器',   '/static/upload/category/ceramic.png', 1, 1),
(2,  0, '织绣染',   '/static/upload/category/textile.png', 2, 1),
(3,  0, '雕刻',     '/static/upload/category/carving.png', 3, 1),
(4,  0, '漆器',     '/static/upload/category/lacquer.png', 4, 1),
(5,  0, '金属工艺', '/static/upload/category/metal.png',   5, 1),
(6,  0, '编织',     '/static/upload/category/weaving.png', 6, 1),
(7,  0, '剪纸',     NULL, 7, 1),
(8,  0, '泥塑',     NULL, 8, 1),
(11, 1, '青花瓷',   NULL, 1, 1),
(12, 1, '紫砂陶',   NULL, 2, 1),
(13, 1, '汝窑瓷',   NULL, 3, 1),
(21, 2, '苏绣',     NULL, 1, 1),
(22, 2, '云锦',     NULL, 2, 1),
(23, 2, '蓝印花布', NULL, 3, 1),
(31, 3, '木雕',     NULL, 1, 1),
(32, 3, '竹刻',     NULL, 2, 1),
(33, 3, '石雕',     NULL, 3, 1),
(41, 4, '脱胎漆器', NULL, 1, 1),
(42, 4, '螺钿',     NULL, 2, 1),
(51, 5, '景泰蓝',   NULL, 1, 1),
(52, 5, '银饰',     NULL, 2, 1),
(61, 6, '竹编',     NULL, 1, 1),
(62, 6, '草编',     NULL, 2, 1);

-- 手工艺品
INSERT INTO `goods`
(`id`, `category_id`, `title`, `subtitle`, `cover_img`, `images`, `detail_html`,
 `price`, `original_price`, `stock`, `sales`, `unit`, `craft`, `origin`, `material`, `status`, `sort`) VALUES
(1,  11, '青花瓷缠枝莲纹茶具', '釉下彩绘，温润如玉的日常茶席',
 '/static/upload/goods/2026/10/01/goods-01-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-01-1.jpg', '/static/upload/goods/2026/10/01/goods-01-2.jpg', '/static/upload/goods/2026/10/01/goods-01-3.jpg'),
 '<p>以景德镇高岭土手工拉坯，缠枝莲纹一笔一画手绘完成，经高温窑火烧制。</p><p>传承人：王师傅，景德镇市级非遗传承人。</p>',
 368.00, 428.00, 50, 120, '套', '青花瓷', '江西景德镇', '高岭土', 1, 1),
(2,  12, '紫砂壶 · 石瓢', '宜兴原矿紫砂，泡养愈久愈温润',
 '/static/upload/goods/2026/10/01/goods-02-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-02-1.jpg', '/static/upload/goods/2026/10/01/goods-02-2.jpg'),
 '<p>选用宜兴黄龙山原矿紫砂泥，传统石瓢造型，手工拍打成型，出水爽利。</p>',
 680.00, NULL, 30, 68, '把', '紫砂陶', '江苏宜兴', '紫砂泥', 1, 2),
(3,  13, '汝窑天青釉杯', '雨过天青云破处，素雅含蓄',
 '/static/upload/goods/2026/10/01/goods-03-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-03-1.jpg', '/static/upload/goods/2026/10/01/goods-03-2.jpg'),
 '<p>仿宋汝窑天青釉，釉面温润，开片自然，杯身轻薄。</p>',
 258.00, NULL, 80, 95, '只', '汝窑瓷', '河南汝州', '瓷土', 1, 3),
(4,  21, '苏绣团扇 · 双面绣', '一针一线，两面成画的江南雅物',
 '/static/upload/goods/2026/10/01/goods-04-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-04-1.jpg', '/static/upload/goods/2026/10/01/goods-04-2.jpg', '/static/upload/goods/2026/10/01/goods-04-3.jpg'),
 '<p>苏州绣娘手工双面绣，正反两面图案一致，桑蚕丝线光泽柔和。</p>',
 520.00, 599.00, 25, 42, '把', '苏绣', '江苏苏州', '桑蚕丝', 1, 4),
(5,  22, '云锦手拿包', '寸锦寸金，织造技艺的随身之美',
 '/static/upload/goods/2026/10/01/goods-05-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-05-1.jpg', '/static/upload/goods/2026/10/01/goods-05-2.jpg'),
 '<p>南京云锦手工织造，真丝与金线交织，图案富丽。</p>',
 899.00, NULL, 15, 18, '件', '云锦', '江苏南京', '真丝金线', 1, 5),
(6,  23, '蓝印花布抱枕', '蓝白之间，朴素温暖的江南记忆',
 '/static/upload/goods/2026/10/01/goods-06-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-06-1.jpg', '/static/upload/goods/2026/10/01/goods-06-2.jpg'),
 '<p>南通蓝印花布传统刮浆印染，棉麻质地，亲肤透气。</p>',
 129.00, NULL, 120, 210, '个', '蓝印花布', '江苏南通', '棉麻', 1, 6),
(7,  31, '东阳木雕摆件', '刀法细腻，木上生花的匠心之作',
 '/static/upload/goods/2026/10/01/goods-07-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-07-1.jpg', '/static/upload/goods/2026/10/01/goods-07-2.jpg', '/static/upload/goods/2026/10/01/goods-07-3.jpg'),
 '<p>东阳木雕艺人以黄杨木手工雕刻，题材吉祥，层次丰富。</p>',
 1280.00, 1580.00, 10, 23, '件', '木雕', '浙江东阳', '黄杨木', 1, 7),
(8,  32, '竹刻臂搁', '文人案头的清雅小件',
 '/static/upload/goods/2026/10/01/goods-08-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-08-1.jpg', '/static/upload/goods/2026/10/01/goods-08-2.jpg'),
 '<p>常州留青竹刻，皮青留白，刀法简洁，纹理自然。</p>',
 360.00, NULL, 40, 31, '件', '竹刻', '江苏常州', '楠竹', 1, 8),
(9,  41, '脱胎漆器茶盘', '大漆温润，历久弥新的东方质感',
 '/static/upload/goods/2026/10/01/goods-09-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-09-1.jpg', '/static/upload/goods/2026/10/01/goods-09-2.jpg'),
 '<p>福州脱胎漆器技艺，天然大漆髹饰，轻而坚固，色泽沉稳。</p>',
 760.00, NULL, 20, 35, '件', '脱胎漆器', '福建福州', '天然大漆', 1, 9),
(10, 51, '景泰蓝掐丝珐琅首饰盒', '掐丝填釉，铜胎上的斑斓艺术',
 '/static/upload/goods/2026/10/01/goods-10-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-10-1.jpg', '/static/upload/goods/2026/10/01/goods-10-2.jpg', '/static/upload/goods/2026/10/01/goods-10-3.jpg'),
 '<p>北京景泰蓝传统技艺，手工掐丝、点蓝、烧制，色彩绚丽。</p>',
 468.00, 528.00, 60, 88, '件', '景泰蓝', '北京', '铜胎珐琅', 1, 10),
(11, 61, '竹编收纳篮', '经纬交织，实用与美感的平衡',
 '/static/upload/goods/2026/10/01/goods-11-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-11-1.jpg', '/static/upload/goods/2026/10/01/goods-11-2.jpg'),
 '<p>四川青神竹编，慈竹破篾手工编织，结实耐用。</p>',
 98.00, NULL, 200, 150, '个', '竹编', '四川青神', '慈竹', 1, 11),
(12, 52, '苗族银饰项链', '叮当作响的苗岭风情',
 '/static/upload/goods/2026/10/01/goods-12-cover.jpg',
 JSON_ARRAY('/static/upload/goods/2026/10/01/goods-12-1.jpg', '/static/upload/goods/2026/10/01/goods-12-2.jpg'),
 '<p>黔东南苗族银饰锻制技艺，手工捶打成型，纹理古朴。</p>',
 688.00, NULL, 26, 29, '条', '银饰', '贵州黔东南', '990银', 1, 12);

-- 商品规格（本期单规格）
INSERT INTO `goods_spec` (`goods_id`, `spec_json`, `price`, `stock`, `image`) VALUES
(1,  JSON_OBJECT('规格', '标准款'), 368.00,  50,  '/static/upload/goods/2026/10/01/goods-01-cover.jpg'),
(2,  JSON_OBJECT('规格', '标准款'), 680.00,  30,  '/static/upload/goods/2026/10/01/goods-02-cover.jpg'),
(3,  JSON_OBJECT('规格', '标准款'), 258.00,  80,  '/static/upload/goods/2026/10/01/goods-03-cover.jpg'),
(4,  JSON_OBJECT('规格', '标准款'), 520.00,  25,  '/static/upload/goods/2026/10/01/goods-04-cover.jpg'),
(5,  JSON_OBJECT('规格', '标准款'), 899.00,  15,  '/static/upload/goods/2026/10/01/goods-05-cover.jpg'),
(6,  JSON_OBJECT('规格', '标准款'), 129.00,  120, '/static/upload/goods/2026/10/01/goods-06-cover.jpg'),
(7,  JSON_OBJECT('规格', '标准款'), 1280.00, 10,  '/static/upload/goods/2026/10/01/goods-07-cover.jpg'),
(8,  JSON_OBJECT('规格', '标准款'), 360.00,  40,  '/static/upload/goods/2026/10/01/goods-08-cover.jpg'),
(9,  JSON_OBJECT('规格', '标准款'), 760.00,  20,  '/static/upload/goods/2026/10/01/goods-09-cover.jpg'),
(10, JSON_OBJECT('规格', '标准款'), 468.00,  60,  '/static/upload/goods/2026/10/01/goods-10-cover.jpg'),
(11, JSON_OBJECT('规格', '标准款'), 98.00,   200, '/static/upload/goods/2026/10/01/goods-11-cover.jpg'),
(12, JSON_OBJECT('规格', '标准款'), 688.00,  26,  '/static/upload/goods/2026/10/01/goods-12-cover.jpg');

-- 首页轮播
INSERT INTO `banner` (`image`, `link_type`, `link_id`, `sort`, `status`) VALUES
('/static/upload/banner/banner-1.jpg', 'goods',    1, 1, 1),
('/static/upload/banner/banner-2.jpg', 'goods',    4, 2, 1),
('/static/upload/banner/banner-3.jpg', 'category', 2, 3, 1);

-- 首页宣传视频
INSERT INTO `home_video` (`title`, `url`, `cover_img`, `duration`, `sort`, `status`) VALUES
('非遗手作宣传片', '/static/upload/video/promo.mp4', '/static/upload/video/promo-cover.jpg', 30, 1, 1);

-- 评价种子数据（先建一个演示用户用于只读评价展示）
INSERT INTO `user` (`id`, `openid`, `nickname`, `avatar_url`) VALUES
(1, 'seed-user-0001', '非遗爱好者', '/static/upload/avatar/seed-avatar.jpg');

INSERT INTO `review` (`goods_id`, `user_id`, `rating`, `content`, `images`) VALUES
(1,  1, 5, '釉色温润，缠枝莲纹细腻，礼盒包装很用心。',
 JSON_ARRAY('/static/upload/review/review-1.jpg')),
(4,  1, 5, '双面绣非常精美，团扇拿在手里很有质感。', NULL),
(7,  1, 4, '木雕细节到位，就是发货稍微慢了一点。', NULL),
(10, 1, 5, '掐丝工整，颜色复古耐看，作为礼物很合适。',
 JSON_ARRAY('/static/upload/review/review-10.jpg'));
