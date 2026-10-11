const orderApi = require('../../api/order');
const { resolveMediaUrl } = require('../../utils/request');

const STATUS_TEXT = {
  0: '待付款',
  1: '待发货',
  2: '待收货',
  3: '已完成',
  4: '已取消'
};

Page({
  data: {
    order: null,
    statusText: ''
  },

  onLoad(options) {
    this.id = options.id;
  },

  onShow() {
    this.load();
  },

  load() {
    orderApi.detail(this.id).then((order) => {
      order.items = (order.items || []).map((item) => ({
        ...item,
        goodsImg: resolveMediaUrl(item.goodsImg)
      }));
      this.setData({ order, statusText: STATUS_TEXT[order.status] || '' });
    }).catch(() => {});
  },

  onPay() {
    orderApi.pay(this.id).then(() => this.load());
  },

  onCancel() {
    wx.showModal({
      title: '提示',
      content: '确认取消订单？',
      success: (res) => {
        if (res.confirm) {
          orderApi.cancel(this.id).then(() => this.load());
        }
      }
    });
  },

  copyOrderNo() {
    wx.setClipboardData({ data: this.data.order.orderNo });
  }
});
