const userApi = require('../../api/user');

Page({
  data: {},

  onLogin() {
    if (this.loggingIn) {
      return;
    }
    this.loggingIn = true;
    wx.showLoading({ title: '登录中', mask: true });

    wx.login({
      success: (res) => {
        if (!res.code) {
          this.fail('获取登录凭证失败');
          return;
        }
        userApi.login(res.code)
          .then((data) => {
            wx.hideLoading();
            this.loggingIn = false;
            wx.setStorageSync('token', data.token);
            wx.setStorageSync('userInfo', data.user);
            this.back();
          })
          .catch(() => this.fail('登录失败，请检查后端配置'));
      },
      fail: () => this.fail('微信登录失败')
    });
  },

  back() {
    const pages = getCurrentPages();
    if (pages.length > 1) {
      wx.navigateBack();
    } else {
      wx.switchTab({ url: '/pages/mine/mine' });
    }
  },

  fail(msg) {
    wx.hideLoading();
    this.loggingIn = false;
    wx.showToast({ title: msg, icon: 'none' });
  }
});
