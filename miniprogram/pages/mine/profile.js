const userApi = require('../../api/user');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    user: {}
  },

  onShow() {
    this.load();
  },

  load() {
    userApi.getProfile()
      .then((user) => this.applyUser(user))
      .catch(() => {});
  },

  applyUser(user) {
    user.avatarDisplay = resolveMediaUrl(user.avatarUrl);
    user.nameFirst = (user.realName || user.nickname || '艺').slice(0, 1);
    this.setData({ user });
    wx.setStorageSync('userInfo', user);
  },

  save(patch) {
    const user = this.data.user;
    const data = Object.assign({ nickname: user.nickname || '微信用户' }, patch);
    return userApi.updateProfile(data)
      .then((next) => this.applyUser(next))
      .catch((err) => {
        wx.showToast({ title: err.msg || '保存失败', icon: 'none' });
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
        nickname: this.data.user.nickname || '微信用户',
        avatarUrl: url
      }))
      .then((user) => {
        this.applyUser(user);
        wx.hideLoading();
        wx.showToast({ title: '头像已更新', icon: 'success' });
      })
      .catch(() => {
        wx.hideLoading();
        wx.showToast({ title: '头像更新失败', icon: 'none' });
      });
  },

  onRealNameBlur(e) {
    const realName = e.detail.value.trim();
    if (!realName || realName === this.data.user.realName) {
      return;
    }
    this.save({ realName });
  },

  onPhoneBlur(e) {
    const phone = e.detail.value.trim();
    if (!phone || phone === this.data.user.phone) {
      return;
    }
    if (!/^1\d{10}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }
    this.save({ phone });
  },

  onGenderChange(e) {
    this.save({ gender: Number(e.detail.value) });
  },

  onBirthdayChange(e) {
    this.save({ birthday: e.detail.value });
  },

  onRegionChange(e) {
    this.save({ region: e.detail.value.join(' ') });
  },

  goAddress() {
    wx.navigateTo({ url: '/pages/address/list' });
  }
});
