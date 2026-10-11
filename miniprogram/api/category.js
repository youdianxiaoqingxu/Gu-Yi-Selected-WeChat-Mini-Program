const { request } = require('../utils/request');

// 获取启用中的分类树
function tree() {
  return request({ url: '/api/category/tree' });
}

module.exports = { tree };

