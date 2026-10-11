Component({
  properties: {
    // 商品数据由父页面传入
    goods: {
      type: Object,
      value: {}
    }
  },

  data: {
    priceText: '0.00'
  },

  observers: {
    // 商品变化时同步格式化价格
    'goods.price': function (price) {
      const num = Number(price);
      this.setData({ priceText: Number.isNaN(num) ? '0.00' : num.toFixed(2) });
    }
  },

  methods: {
    onTap() {
      // 点击卡片跳转商品详情
      wx.navigateTo({ url: '/pages/goods/detail?id=' + this.data.goods.id });
    }
  }
});

