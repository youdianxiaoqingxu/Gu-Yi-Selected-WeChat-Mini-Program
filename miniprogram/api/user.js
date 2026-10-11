const { BASE_URL, request } = require('../utils/request');

function login(code) {
  return request({ url: '/api/login', method: 'POST', data: { code } });
}

function getProfile() {
  return request({ url: '/api/user/profile' });
}

function updateProfile(data) {
  return request({ url: '/api/user/profile', method: 'PUT', data });
}

function uploadAvatar(filePath) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    wx.uploadFile({
      url: BASE_URL + '/api/upload/avatar',
      filePath,
      name: 'file',
      header: token ? { Authorization: 'Bearer ' + token } : {},
      success(res) {
        try {
          const body = JSON.parse(res.data);
          if (body.code === 200) {
            resolve(body.data);
          } else {
            reject(body);
          }
        } catch (e) {
          reject(e);
        }
      },
      fail: reject
    });
  });
}

module.exports = { login, getProfile, updateProfile, uploadAvatar };

