const app = getApp()

Page({
  data: {
    hiddenReAuthorizePop: true,//隐藏重新授权确认弹窗
    latitude: "", //维度，浮点数
    longitude: "", //经度，浮点数
    content: "本活动需要获取位置才可以参加"
  },
  onLoad: function () {
    //1. 页面加载的时候获取定位
    this.getLocation()
  },
  /**
   * 1. 获取用户定位
   */
  getLocation: function () {
    var self = this;
    wx.getLocation({
      type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的坐标，可传入'gcj02'
      altitude: true, //传入 true 会返回高度信息，由于获取高度需要较高精确度，会减慢接口返回速度
      success: function (res) {
        var latitude = res.latitude; // 纬度，浮点数
        var longitude = res.longitude; // 经度，浮点数
        self.setData({
          latitude: res.latitude,
          longitude: res.longitude
        })
      },
      fail: function (res) {
        //未授权就弹出弹窗提示用户重新授权
        self.reAuthorize();
      }
    });
  },
  /**
   * 1.2 重新授权按钮点击事件
   * click event   
   */
  openLocationSetting: function () {
    var self = this
    //先获取用户的当前设置，返回值中只会出现小程序已经向用户请求过的权限
    wx.getSetting({
      success: function (res) {
        if (res.authSetting && !res.authSetting["scope.userLocation"]) {
          //未授权则打开授权设置界面
          wx.openSetting({
            success: function (res) {
              if (res.authSetting && res.authSetting["scope.userLocation"]) {
                //允许授权,则自动获取定位，并关闭二确弹窗，否则返回首页不处理
                self.getLocation();
                self.setData({
                  hiddenReAuthorizePop: true
                })
                wx.showToast({
                  title: '您已授权获取位置信息',
                  icon: 'none'
                })
              } else {
                //未授权就弹出弹窗提示用户重新授权      
                self.reAuthorize();
              }
            }
          })
        } else {
          //授权则重新获取位置新（授权设置界面返回首页，首页授权二确弹窗未关闭）
          self.getLocation();
        }
      }
    })
  },
  /**
   * 重新授权
   */
  reAuthorize: function () {
    var self = this
    self.setData({ hiddenReAuthorizePop: false })
  }
})