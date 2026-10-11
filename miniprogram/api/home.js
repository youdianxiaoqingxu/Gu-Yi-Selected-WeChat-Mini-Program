const { request } = require('../utils/request');

function home() {
  return request({ url: '/api/home' });
}

module.exports = { home };

