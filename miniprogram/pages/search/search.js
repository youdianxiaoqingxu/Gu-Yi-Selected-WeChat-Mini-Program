const goodsApi = require('../../api/goods');
const { resolveMediaUrl } = require('../../utils/request');

const HISTORY_KEY = 'searchHistory';

Page({
  data: {
    keyword: '',
    history: [],
    goods: [],
    total: 0,
    page: 1,
    size: 10,
    searched: false
  },

  onLoad() {
    this.setData({ history: wx.getStorageSync(HISTORY_KEY) || [] });
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    const keyword = this.data.keyword.trim();
    if (!keyword) {
      return;
    }
    // 保存搜索历史，去重且最多保留 10 条
    const history = [keyword, ...this.data.history.filter((item) => item !== keyword)].slice(0, 10);
    wx.setStorageSync(HISTORY_KEY, history);
    this.setData({ history, keyword, page: 1, goods: [], searched: true });
    this.loadGoods(keyword);
  },

  onHistoryTap(e) {
    this.setData({ keyword: e.currentTarget.dataset.k });
    this.onSearch();
  },

  clearHistory() {
    wx.removeStorageSync(HISTORY_KEY);
    this.setData({ history: [] });
  },

  loadGoods(keyword) {
    goodsApi.list({
      keyword,
      page: this.data.page,
      size: this.data.size
    }).then((res) => {
      const list = res.list.map((item) => ({
        ...item,
        coverImg: resolveMediaUrl(item.coverImg)
      }));
      this.setData({
        goods: this.data.page === 1 ? list : this.data.goods.concat(list),
        total: res.total
      });
    }).catch(() => {});
  },

  onReachBottom() {
    if (!this.data.searched || this.data.goods.length >= this.data.total) {
      return;
    }
    this.setData({ page: this.data.page + 1 });
    this.loadGoods(this.data.keyword);
  }
});

