const addressApi = require('../../api/address');

Page({
  data: {
    id: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false,
    region: []
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.load(options.id);
    }
  },

  load(id) {
    addressApi.list().then((list) => {
      const address = list.find((item) => item.id == id);
      if (address) {
        this.setData({
          name: address.name,
          phone: address.phone,
          province: address.province,
          city: address.city,
          district: address.district,
          detail: address.detail,
          isDefault: address.isDefault === 1,
          region: [address.province, address.city, address.district]
        });
      }
    });
  },

  onName(e) {
    this.setData({ name: e.detail.value });
  },

  onPhone(e) {
    this.setData({ phone: e.detail.value });
  },

  onDetail(e) {
    this.setData({ detail: e.detail.value });
  },

  onRegion(e) {
    const [province, city, district] = e.detail.value;
    this.setData({ province, city, district });
  },

  onDefault(e) {
    this.setData({ isDefault: e.detail.value });
  },

  save() {
    const { name, phone, province, city, district, detail, isDefault } = this.data;
    if (!name || !phone || !detail) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' });
      return;
    }
    const data = { name, phone, province, city, district, detail, isDefault: isDefault ? 1 : 0 };
    const request = this.data.id
      ? addressApi.update(this.data.id, data)
      : addressApi.add(data);
    request
      .then(() => wx.navigateBack())
      .catch((err) => wx.showToast({ title: err.msg || '保存失败', icon: 'none' }));
  }
});

