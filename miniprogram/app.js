const { getToken } = require('./utils/login');

App({
  globalData: {
    token: '',
    userInfo: null
  },

  onLaunch() {
    const token = getToken();
    if (token) {
      this.globalData.token = token;
    }
  }
});

