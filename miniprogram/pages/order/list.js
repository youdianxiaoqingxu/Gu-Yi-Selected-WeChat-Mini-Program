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
    status: 'all',
    keyword: '',
    list: [],
    page: 1,
    size: 10,
    total: 0
  },

  onShow() {
    this.reload();
  },

  onTab(e) {
    this.setData({ status: e.currentTarget.dataset.key });
    this.reload();
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    this.reload();
  },

  onClearSearch() {
    this.setData({ keyword: '' });
    this.reload();
  },

  reload() {
    this.setData({ page: 1, list: [] });
    this.load();
  },

  load() {
    const params = { page: this.data.page, size: this.data.size };
    if (this.data.status !== 'all') {
      params.status = Number(this.data.status);
    }
    if (this.data.keyword) {
      params.keyword = this.data.keyword;
    }
    return orderApi.list(params).then((res) => {
      const list = res.list.map((order) => ({
        ...order,
        statusText: STATUS_TEXT[order.status] || '',
        payText: Number(order.payAmount || 0).toFixed(2),
        items: (order.items || []).map((item) => ({
          ...item,
          goodsImg: resolveMediaUrl(item.goodsImg)
        }))
      }));
      this.setData({
        list: this.data.page === 1 ? list : this.data.list.concat(list),
        total: res.total
      });
    }).catch(() => {});
  },

  onPullDownRefresh() {
    this.setData({ page: 1, list: [] });
    this.load().then(() => wx.stopPullDownRefresh(), () => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    if (this.data.list.length >= this.data.total) {
      return;
    }
    this.setData({ page: this.data.page + 1 });
    this.load();
  },

  onPay(e) {
    orderApi.pay(e.currentTarget.dataset.id).then(() => this.reload());
  },

  onCancel(e) {
    wx.showModal({
      title: '提示',
      content: '确认取消订单？',
      success: (res) => {
        if (res.confirm) {
          orderApi.cancel(e.currentTarget.dataset.id).then(() => this.reload());
        }
      }
    });
  },

  onDetail(e) {
    wx.navigateTo({ url: '/pages/order/detail?id=' + e.currentTarget.dataset.id });
  }
});
