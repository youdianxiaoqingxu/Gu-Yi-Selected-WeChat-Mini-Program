const homeApi = require('../../api/home');
const { resolveMediaUrl } = require('../../utils/request');

// 一级分类自定义图标，未配置的分类仍显示名称首字
const CATEGORY_ICONS = {
  1: '/static/category/ceramic.jpg'
};

Page({
  data: {
    video: null,
    videoError: false,
    banners: [],
    categories: [],
    hotGoods: []
  },

  onLoad() {
    this.loadHome();
  },

  onReady() {
    // 创建视频上下文，用于页面显示/隐藏时控制播放
    this.videoContext = wx.createVideoContext('promoVideo', this);
  },

  onShow() {
    // 非用户主动暂停时，回到首页继续播放
    if (this.videoContext && !this.userPaused) {
      this.videoContext.play();
    }
  },

  onHide() {
    // 标记为系统暂停，区别于用户主动点击暂停
    this.pausingBySystem = true;
    if (this.videoContext) {
      this.videoContext.pause();
    }
  },

  onVideoPlay() {
    this.videoPlaying = true;
    this.pausingBySystem = false;
  },

  onVideoTap() {
    if (!this.videoContext) {
      return;
    }

    if (this.videoPlaying) {
      this.userPaused = true;
      this.videoContext.pause();
      return;
    }

    this.userPaused = false;
    this.videoContext.play();
  },

  onVideoPause() {
    this.videoPlaying = false;
    // 非系统暂停则视为用户主动暂停，后续不再自动续播
    if (!this.pausingBySystem) {
      this.userPaused = true;
    }
    this.pausingBySystem = false;
  },

  onVideoError() {
    // 视频加载失败时隐藏播放器，用封面兜底
    this.setData({ videoError: true });
  },

  loadHome() {
    return homeApi.home()
      .then((data) => {
        // 将后端相对路径统一拼成完整地址
        const banners = (data.banners || []).map((item) => ({
          ...item,
          image: resolveMediaUrl(item.image)
        }));
        const hotGoods = (data.hotGoods || []).map((item) => ({
          ...item,
          coverImg: resolveMediaUrl(item.coverImg)
        }));
        const categories = (data.categories || []).map((item) => ({
          ...item,
          firstChar: (item.name || '艺').slice(0, 1),
          iconImage: CATEGORY_ICONS[item.id] || ''
        }));
        const video = data.video
          ? {
              ...data.video,
              url: resolveMediaUrl(data.video.url),
              coverImg: resolveMediaUrl(data.video.coverImg)
            }
          : null;

        this.setData({ video, banners, categories, hotGoods });
      })
      .catch(() => {});
  },

  onPullDownRefresh() {
    this.loadHome().then(() => wx.stopPullDownRefresh(), () => wx.stopPullDownRefresh());
  },

  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' });
  },

  onCategoryTap(e) {
    // 记录选中的一级分类，切到分类 tab 后由分类页读取
    getApp().globalData.categoryId = e.currentTarget.dataset.id;
    wx.switchTab({ url: '/pages/category/category' });
  },

  onBannerTap(e) {
    const { type, id } = e.currentTarget.dataset;
    if (type === 'goods' && id) {
      wx.navigateTo({ url: '/pages/goods/detail?id=' + id });
    }
  }
});
