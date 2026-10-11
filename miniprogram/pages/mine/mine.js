const userApi = require('../../api/user');
const { isLoggedIn } = require('../../utils/login');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    loggedIn: false,
    userInfo: {},
    avatarDisplay: '',
    nicknameFirst: '艺'
  },

  onShow() {
    this.refresh();
  },

  refresh() {
    if (!isLoggedIn()) {
      this.setData({ loggedIn: false, userInfo: {}, avatarDisplay: '', nicknameFirst: '艺' });
      return;
    }
    userApi.getProfile()
      .then((user) => {
        wx.setStorageSync('userInfo', user);
        this.setUser(user);
      })
      .catch(() => {
        this.setData({ loggedIn: false, userInfo: {}, avatarDisplay: '', nicknameFirst: '艺' });
      });
  },

  setUser(user) {
    const nickname = (user && user.nickname) || '微信用户';
    this.setData({
      loggedIn: true,
      userInfo: user || {},
      avatarDisplay: resolveMediaUrl(user && user.avatarUrl),
      nicknameFirst: nickname.slice(0, 1)
    });
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goProfile() {
    wx.navigateTo({ url: '/pages/mine/profile' });
  },

  goOrders() {
    wx.navigateTo({ url: '/pages/order/list' });
  },

  goAddress() {
    wx.navigateTo({ url: '/pages/address/list' });
  },

  goFavorite() {
    wx.navigateTo({ url: '/pages/favorite/favorite' });
  },

  goAbout() {
    wx.showToast({ title: '关于与客服', icon: 'none' });
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      confirmColor: '#B04A3A',
      success: (res) => {
        if (!res.confirm) {
          return;
        }
        wx.removeStorageSync('token');
        wx.removeStorageSync('userInfo');
        this.setData({
          loggedIn: false,
          userInfo: {},
          avatarDisplay: '',
          nicknameFirst: '艺'
        });
        wx.showToast({ title: '已退出登录', icon: 'none' });
      }
    });
  },

  onChooseAvatar(e) {
    const filePath = e.detail.avatarUrl;
    if (!filePath) {
      return;
    }
    wx.showLoading({ title: '上传中' });
    userApi.uploadAvatar(filePath)
      .then((url) => userApi.updateProfile({
        nickname: this.data.userInfo.nickname || '微信用户',
        avatarUrl: url
      }))
      .then((user) => {
        wx.hideLoading();
        wx.setStorageSync('userInfo', user);
        this.setUser(user);
        wx.showToast({ title: '头像已更新', icon: 'success' });
      })
      .catch(() => {
        wx.hideLoading();
        wx.showToast({ title: '头像更新失败', icon: 'none' });
      });
  },

  onNicknameBlur(e) {
    const nickname = e.detail.value;
    if (!nickname || nickname === this.data.userInfo.nickname) {
      return;
    }
    userApi.updateProfile({ nickname })
      .then((user) => {
        wx.setStorageSync('userInfo', user);
        this.setUser(user);
        wx.showToast({ title: '昵称已保存', icon: 'success' });
      })
      .catch(() => {
        wx.showToast({ title: '昵称保存失败', icon: 'none' });
      });
  }
});
