const { request } = require('../utils/request');

function list() {
  return request({ url: '/api/cart' });
}

function add(data) {
  return request({ url: '/api/cart', method: 'POST', data });
}

function update(id, data) {
  return request({ url: '/api/cart/' + id, method: 'PUT', data });
}

function remove(id) {
  return request({ url: '/api/cart/' + id, method: 'DELETE' });
}

module.exports = { list, add, update, remove };

