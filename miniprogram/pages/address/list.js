const addressApi = require('../../api/address');

Page({
  data: {
    list: [],
    select: false
  },

  onLoad(options) {
    // select=1 表示从订单确认页进入，点击地址即返回选中结果
    this.setData({ select: options.select === '1' });
  },

  onShow() {
    this.load();
  },

  load() {
    addressApi.list().then((list) => this.setData({ list })).catch(() => {});
  },

  onTap(e) {
    if (!this.data.select) {
      return;
    }
    getApp().globalData.selectedAddressId = e.currentTarget.dataset.id;
    wx.navigateBack();
  },

  onAdd() {
    wx.navigateTo({ url: '/pages/address/edit' });
  },

  onEdit(e) {
    wx.navigateTo({ url: '/pages/address/edit?id=' + e.currentTarget.dataset.id });
  },

  onSetDefault(e) {
    addressApi.setDefault(e.currentTarget.dataset.id).then(() => this.load());
  },

  onDelete(e) {
    wx.showModal({
      title: '提示',
      content: '确认删除该地址？',
      success: (res) => {
        if (res.confirm) {
          addressApi.remove(e.currentTarget.dataset.id).then(() => this.load());
        }
      }
    });
  }
});
