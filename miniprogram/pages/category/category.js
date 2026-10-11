const categoryApi = require('../../api/category');
const goodsApi = require('../../api/goods');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    categories: [],
    currentCategoryId: null,
    sort: 'comprehensive',
    goods: [],
    total: 0,
    page: 1,
    size: 10
  },

  onLoad() {
    this.loadCategories();
  },

  onShow() {
    // 首页分类入口可能带来预选分类，仅在分类已加载后处理一次
    const pendingId = getApp().globalData.categoryId;
    if (pendingId && this.data.categories.length) {
      this.selectCategory(pendingId);
      getApp().globalData.categoryId = null;
    }
  },

  loadCategories() {
    categoryApi.tree().then((categories) => {
      this.setData({ categories });

      const pendingId = getApp().globalData.categoryId;
      const first = categories[0];
      const target = categories.find((item) => item.id === pendingId) || first;
      if (pendingId) {
        getApp().globalData.categoryId = null;
      }
      this.selectCategory(target ? target.id : null);
    }).catch(() => {});
  },

  selectCategory(id) {
    this.setData({
      currentCategoryId: id,
      page: 1,
      goods: []
    });
    this.loadGoods(id);
  },

  onCategoryTap(e) {
    this.selectCategory(e.currentTarget.dataset.id);
  },

  onSortTap(e) {
    this.setData({ sort: e.currentTarget.dataset.sort, page: 1, goods: [] });
    this.loadGoods(this.data.currentCategoryId);
  },

  onGoodsTap(e) {
    wx.navigateTo({ url: '/pages/goods/detail?id=' + e.currentTarget.dataset.id });
  },

  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' });
  },

  loadGoods(categoryId) {
    if (!categoryId) {
      return;
    }
    goodsApi.list({
      categoryId,
      sort: this.data.sort,
      page: this.data.page,
      size: this.data.size
    }).then((res) => {
      const list = res.list.map((item) => ({
        ...item,
        coverImg: resolveMediaUrl(item.coverImg),
        priceText: Number(item.price || 0).toFixed(2)
      }));
      this.setData({
        goods: this.data.page === 1 ? list : this.data.goods.concat(list),
        total: res.total
      });
    }).catch(() => {});
  },

  onLoadMore() {
    if (this.data.goods.length >= this.data.total) {
      return;
    }
    this.setData({ page: this.data.page + 1 });
    this.loadGoods(this.data.currentCategoryId);
  }
});
