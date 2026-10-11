const { request } = require('../utils/request');

function list(params) {
  return request({ url: '/api/goods', data: params });
}

function detail(id) {
  return request({ url: '/api/goods/' + id });
}

module.exports = { list, detail };

