const cartApi = require('../../api/cart');
const { isLoggedIn } = require('../../utils/login');
const { resolveMediaUrl } = require('../../utils/request');

Page({
  data: {
    items: [],
    allChecked: false,
    selectedCount: 0,
    total: '0.00',
    loggedIn: false
  },

  onShow() {
    const loggedIn = isLoggedIn();
    this.setData({ loggedIn });
    if (!loggedIn) {
      this.setData({ items: [], allChecked: false, selectedCount: 0, total: '0.00' });
      return;
    }
    this.loadCart();
  },

  loadCart() {
    return cartApi.list().then((items) => {
      const list = items.map((item) => ({
        ...item,
        checked: Number(item.checked) === 1 ? 1 : 0,
        coverImg: resolveMediaUrl(item.coverImg)
      }));
      this.setData({ items: list });
      this.recalc();
    }).catch(() => {});
  },

  onPullDownRefresh() {
    this.loadCart().then(() => wx.stopPullDownRefresh(), () => wx.stopPullDownRefresh());
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goShopping() {
    wx.switchTab({ url: '/pages/index/index' });
  },

  onCheck(e) {
    const id = e.currentTarget.dataset.id;
    const checked = e.detail.value ? 1 : 0;
    cartApi.update(id, { checked }).then(() => {
      const items = this.data.items.map((item) => String(item.id) === String(id) ? { ...item, checked } : item);
      this.setData({ items });
      this.recalc();
    });
  },

  onAllCheck(e) {
    const checked = e.detail.value ? 1 : 0;
    const items = this.data.items.map((item) => ({ ...item, checked }));
    this.setData({ items });
    Promise.all(items.map((item) => cartApi.update(item.id, { checked }))).then(() => this.recalc());
  },

  onMinus(e) {
    this.changeQuantity(e.currentTarget.dataset.id, -1);
  },

  onPlus(e) {
    this.changeQuantity(e.currentTarget.dataset.id, 1);
  },

  changeQuantity(id, delta) {
    const item = this.data.items.find((current) => String(current.id) === String(id));
    const quantity = item.quantity + delta;
    if (quantity < 1) {
      return;
    }
    cartApi.update(id, { quantity })
      .then(() => {
        const items = this.data.items.map((current) => String(current.id) === String(id) ? { ...current, quantity } : current);
        this.setData({ items });
        this.recalc();
      })
      .catch((err) => wx.showToast({ title: err.msg || '库存不足', icon: 'none' }));
  },

  onDelete(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确认删除该商品？',
      success: (res) => {
        if (res.confirm) {
          cartApi.remove(id).then(() => this.loadCart());
        }
      }
    });
  },

  recalc() {
    const selected = this.data.items.filter((item) => Number(item.checked) === 1);
    const total = selected.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0);
    this.setData({
      selectedCount: selected.length,
      total: total.toFixed(2),
      allChecked: this.data.items.length > 0 && selected.length === this.data.items.length
    });
  },

  goCheckout() {
    const selected = this.data.items.filter((item) => Number(item.checked) === 1);
    if (!selected.length) {
      wx.showToast({ title: '请选择商品', icon: 'none' });
      return;
    }
    const ids = selected.map((item) => item.id);
    wx.navigateTo({
      url: '/pages/order/confirm?ids=' + ids.join(','),
      fail: () => wx.showToast({ title: '跳转失败，请重试', icon: 'none' })
    });
  }
});
