const { request } = require('../utils/request');

function list() {
  return request({ url: '/api/address' });
}

function add(data) {
  return request({ url: '/api/address', method: 'POST', data });
}

function update(id, data) {
  return request({ url: '/api/address/' + id, method: 'PUT', data });
}

function remove(id) {
  return request({ url: '/api/address/' + id, method: 'DELETE' });
}

function setDefault(id) {
  return request({ url: '/api/address/' + id + '/default', method: 'PUT' });
}

module.exports = { list, add, update, remove, setDefault };

