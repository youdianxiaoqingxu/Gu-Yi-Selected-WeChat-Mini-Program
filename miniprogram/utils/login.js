function getToken() {
  return wx.getStorageSync('token') || '';
}

function isLoggedIn() {
  return !!getToken();
}

function redirectToLogin() {
  const pages = getCurrentPages();
  const current = pages[pages.length - 1];
  const redirect = current ? encodeURIComponent('/' + current.route) : '';
  wx.navigateTo({ url: '/pages/login/login?redirect=' + redirect });
}

module.exports = { getToken, isLoggedIn, redirectToLogin };

