const goodsApi = require('../../api/goods');
const cartApi = require('../../api/cart');
const favoriteApi = require('../../api/favorite');
const { isLoggedIn } = require('../../utils/login');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    goods: null,
    priceText: '0.00',
    originalText: ''
  },

  onLoad(options) {
    this.loadDetail(options.id);
  },

  loadDetail(id) {
    goodsApi.detail(id).then((goods) => {
      // 将相对路径和规格信息转成页面可直接展示的数据
      goods.images = (goods.images || []).map(resolveMediaUrl);
      goods.specs = (goods.specs || []).map((spec) => ({
        ...spec,
        specText: Object.values(spec.specJson || {}).join(' / ') || '标准款',
        specPriceText: Number(spec.price || 0).toFixed(2),
        image: resolveMediaUrl(spec.image)
      }));
      goods.reviews = (goods.reviews || []).map((review) => ({
        ...review,
        avatarUrl: resolveMediaUrl(review.avatarUrl),
        images: (review.images || []).map(resolveMediaUrl)
      }));

      this.setData({
        goods,
        priceText: Number(goods.price || 0).toFixed(2),
        originalText: goods.originalPrice ? Number(goods.originalPrice).toFixed(2) : ''
      });
    }).catch(() => {});
  },

  previewImage(e) {
    wx.previewImage({
      current: e.currentTarget.dataset.url,
      urls: this.data.goods.images
    });
  },

  onFavorite() {
    if (!isLoggedIn()) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    const goods = this.data.goods;
    const request = goods.favorite ? favoriteApi.remove(goods.id) : favoriteApi.add(goods.id);
    request
      .then(() => {
        goods.favorite = !goods.favorite;
        this.setData({ goods });
        wx.showToast({ title: goods.favorite ? '已收藏' : '已取消收藏', icon: 'none' });
      })
      .catch((err) => wx.showToast({ title: err.msg || '操作失败', icon: 'none' }));
  },

  onAddCart() {
    if (!isLoggedIn()) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    const spec = (this.data.goods.specs || [])[0];
    cartApi.add({
      goodsId: this.data.goods.id,
      specId: spec ? spec.id : null,
      quantity: 1
    })
      .then(() => wx.showToast({ title: '已加入购物车', icon: 'success' }))
      .catch((err) => wx.showToast({ title: err.msg || '加入失败', icon: 'none' }));
  },

  onBuyNow() {
    this.onAddCart();
    // 立即购买简化为加入购物车后进入购物车结算
    setTimeout(() => wx.switchTab({ url: '/pages/cart/cart' }), 500);
  }
});
