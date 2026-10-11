const { request } = require('../utils/request');

// 获取收藏列表
function list() {
  return request({ url: '/api/favorite' });
}

// 收藏商品
function add(goodsId) {
  return request({ url: '/api/favorite/' + goodsId, method: 'POST' });
}

// 取消收藏
function remove(goodsId) {
  return request({ url: '/api/favorite/' + goodsId, method: 'DELETE' });
}

module.exports = { list, add, remove };

