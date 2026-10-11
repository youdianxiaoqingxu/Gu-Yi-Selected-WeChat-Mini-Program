const orderApi = require('../../api/order');
const addressApi = require('../../api/address');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    ids: [],
    address: null,
    preview: null,
    payText: '0.00',
    remark: ''
  },

  onLoad(options) {
    this.setData({ ids: (options.ids || '').split(',').map(Number).filter(Boolean) });
  },

  onShow() {
    this.load();
  },

  load() {
    addressApi.list().then((list) => {
      const selectedId = getApp().globalData.selectedAddressId;
      const address = list.find((item) => item.id === selectedId)
        || list.find((item) => item.isDefault === 1)
        || list[0]
        || null;
      if (selectedId) {
        getApp().globalData.selectedAddressId = null;
      }
      this.setData({ address });
    }).catch(() => {});

    orderApi.preview({ cartIds: this.data.ids }).then((preview) => {
      preview.items = (preview.items || []).map((item) => ({
        ...item,
        coverImg: resolveMediaUrl(item.coverImg)
      }));
      this.setData({ preview, payText: Number(preview.payAmount || 0).toFixed(2) });
    }).catch(() => {});
  },

  chooseAddress() {
    wx.navigateTo({ url: '/pages/address/list?select=1' });
  },

  onRemark(e) {
    this.setData({ remark: e.detail.value });
  },

  submit() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }
    orderApi.create({
      addressId: this.data.address.id,
      cartIds: this.data.ids,
      remark: this.data.remark
    })
      .then((orderId) => wx.redirectTo({ url: '/pages/order/detail?id=' + orderId }))
      .catch((err) => wx.showToast({ title: err.msg || '下单失败', icon: 'none' }));
  }
});
