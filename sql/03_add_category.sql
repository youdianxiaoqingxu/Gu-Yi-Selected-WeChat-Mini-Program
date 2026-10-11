-- 追加首页一级分类：剪纸、泥塑
-- 说明：新建数据库直接执行 02_seed.sql 即可；本文件用于已在使用的数据库
USE `guyi_craft`;

INSERT INTO `category` (`id`, `parent_id`, `name`, `icon`, `sort`, `status`) VALUES
(7, 0, '剪纸', NULL, 7, 1),
(8, 0, '泥塑', NULL, 8, 1)
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `parent_id` = VALUES(`parent_id`),
  `sort` = VALUES(`sort`),
  `status` = VALUES(`status`);
