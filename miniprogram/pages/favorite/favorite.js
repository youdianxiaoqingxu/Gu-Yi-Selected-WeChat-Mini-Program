const favoriteApi = require('../../api/favorite');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    list: []
  },

  onShow() {
    this.load();
  },

  load() {
    return favoriteApi.list().then((list) => {
      this.setData({
        list: list.map((item) => ({
          // 转成商品卡片所需的结构，同时保留 goodsId 供取消收藏使用
          id: item.goodsId,
          goodsId: item.goodsId,
          title: item.goodsTitle,
          coverImg: resolveMediaUrl(item.coverImg),
          price: item.price,
          originalPrice: item.originalPrice,
          craft: item.craft
        }))
      });
    }).catch(() => {});
  },

  onPullDownRefresh() {
    this.load().then(() => wx.stopPullDownRefresh(), () => wx.stopPullDownRefresh());
  },

  onRemove(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确认取消收藏？',
      success: (res) => {
        if (res.confirm) {
          favoriteApi.remove(id).then(() => this.load());
        }
      }
    });
  }
});
