var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    latitude: "", //维度，浮点数
    longitude: "", //经度，浮点数
    keywordsNearbyAddresses:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 页面加载时获取定位
    this.getLocation();
  },
  /**
   * 获取用户定位
   */
  getLocation: function () {
    var self = this;
    wx.getLocation({
      type: 'gcj02', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的坐标，可传入'gcj02'
      // altitude: true, //传入 true 会返回高度信息，由于获取高度需要较高精确度，会减慢接口返回速度
      success: function (res) {
        const latitude = res.latitude; // 纬度，浮点数
        const longitude = res.longitude; // 经度，浮点数
        self.setData({
          latitude: latitude,
          longitude: longitude
        })
        self.getResList("");
      },
      // fail: function (res) {
      //   //未授权就弹出弹窗提示用户重新授权
      //   self.reAuthorize();
      // },
      complete: function (res) {

      }
    });
  },
  getResList: function (key) {
    var self = this;
    if(key == null){
      key = "";
    }
    let lat = this.data.latitude;
    let lng = this.data.longitude;
    if (lat == "" || lng == ""){
      this.openLocationSetting();
      return false;
    }
    util.request(api.ConsumerLocation, { lat: lat, lng: lng, keyword: key}, "GET")
      .then(function (res) {
        console.log(res);
        self.setData({
          keywordsNearbyAddresses: res.data.keywordsNearbyAddresses
        })
      });
  },

  getAddressByKey:function (e) {
    var key = e.detail.value;
    this.getResList(key);
  },
  /**
   * 重新授权按钮点击事件
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
                // self.setData({
                //   hiddenReAuthorizePop: true
                // })
                wx.showToast({
                  title: '您已授权获取位置信息',
                  icon: 'none'
                })
              }
              // else {
              //   //未授权就弹出弹窗提示用户重新授权      
              //   self.reAuthorize();
              // }
            }
          })
        }
        else {
          //授权则重新获取位置（授权设置界面返回首页，首页授权二确弹窗未关闭）
          self.getLocation();
          wx.showToast({
            title: '位置已更新',
            icon: 'none'
          })
        }
      }
    })
  },

  getAddress:function(e){
    var pages = getCurrentPages();
    var currPage = pages[pages.length - 1];   //当前页面
    var prevPage = pages[pages.length - 2];  //上一个页面

    //直接调用上一个页面的setData()方法，把数据存到上一个页面中去
    prevPage.setData({
      chooseName: e.target.dataset.name
    })
    wx.navigateBack({
      delta: 1
    })
  },

  /**
   * 重新授权位置信息
   */
  // reAuthorize: function () {
  //   var self = this
  //   self.setData({ hiddenReAuthorizePop: false })
  // },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})