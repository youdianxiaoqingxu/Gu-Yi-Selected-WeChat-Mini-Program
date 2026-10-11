const BASE_URL = 'http://localhost:8080';

let redirecting = false;

function resolveMediaUrl(path) {
  if (!path) {
    return '';
  }
  if (/^https?:\/\//.test(path)) {
    return path;
  }
  return BASE_URL + path;
}

function redirectToLogin() {
  // 避免多个接口同时返回 401 时重复跳转
  if (redirecting) {
    return;
  }
  const pages = getCurrentPages();
  const current = pages[pages.length - 1];
  if (current && current.route === 'pages/login/login') {
    return;
  }
  redirecting = true;
  wx.showToast({ title: '请先登录', icon: 'none' });
  setTimeout(() => {
    wx.navigateTo({
      url: '/pages/login/login',
      complete: () => {
        redirecting = false;
      }
    });
  }, 400);
}

function request(options) {
  const token = wx.getStorageSync('token');
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: Object.assign(
        { 'Content-Type': 'application/json' },
        token ? { Authorization: 'Bearer ' + token } : {},
        options.header || {}
      ),
      success(res) {
        const body = res.data || {};
        if (body.code === 200) {
          resolve(body.data);
          return;
        }
        if (body.code === 401) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          redirectToLogin();
        }
        reject(body);
      },
      fail(err) {
        reject(err);
      }
    });
  });
}

module.exports = { BASE_URL, resolveMediaUrl, request };
