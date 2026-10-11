const { request } = require('../utils/request');

function preview(data) {
  return request({ url: '/api/order/preview', method: 'POST', data });
}

function create(data) {
  return request({ url: '/api/order/create', method: 'POST', data });
}

function pay(id) {
  return request({ url: '/api/order/' + id + '/pay', method: 'POST' });
}

function cancel(id) {
  return request({ url: '/api/order/' + id + '/cancel', method: 'POST' });
}

function list(params) {
  return request({ url: '/api/order/list', data: params });
}

function detail(id) {
  return request({ url: '/api/order/' + id });
}

module.exports = { preview, create, pay, cancel, list, detail };

