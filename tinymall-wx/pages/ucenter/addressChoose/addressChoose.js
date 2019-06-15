var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    latitude: "", // 维度，浮点数
    longitude: "", // 经度，浮点数
    keywordsNearbyAddresses:[],// 搜索结果
    keyWord:"keyWord", // 关键词搜索
    showNoRes:false, // 初始化不显示无结果view，第一次查询后一直为true
    searchWaitNum:0 // 等待查询数量
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 页面加载时获取定位
    wx.showLoading({
      title: '加载中...',
    });
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
        self.getResList();
      },
      fail: function (res) {
        //未授权就弹出弹窗提示用户重新授权
        wx.hideLoading();
        self.reAuthorize();
      },
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
        if(res.errno === -1){
          self.setData({
            keywordsNearbyAddresses: "",
            keyWord:"",
            showNoRes: true
          })
          wx.showToast({
            title: '查询错误，请重试',
            icon: 'none'
          })
        }else{
          self.setData({
            keywordsNearbyAddresses: res.data.keywordsNearbyAddresses,
            showNoRes: true
          })
        }
        wx.hideLoading();
      });
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
              }
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

  /**
  * 重新授权位置信息
  */
  reAuthorize: function () {
    var self = this;
    wx.showModal({
      title: '提示',
      content: '未获取到位置信息，请点击授权',
      success(res) {
        if (res.confirm) {
          self.openLocationSetting();
        } else if (res.cancel) {
          console.log('位置信息获取失败');
          wx.navigateBack();
        }
      }
    })
  },

  /**
   * 打字监听延时功能，防止每个字都做查询
  */


  /**
   * 监听输入框查询结果
  */
  getAddressByKey: function (e) {
    var self = this;
    const key = e.detail.value;
    const searchWaitNumPlus = this.data.searchWaitNum + 1;
    this.setData({
      keyWord: key,
      searchWaitNum: searchWaitNumPlus
    });
    setTimeout(function () {
      // console.log("延迟调用============");
      let searchWaitNum = self.data.searchWaitNum;
      if (searchWaitNum <= 1){
        // console.log("searchWaitNum=" + searchWaitNum + "；进行查询");
        self.setData({
          searchWaitNum: 0
        })
        self.getResList(key);
      }else{
        // console.log("searchWaitNum=" + searchWaitNum + "；略过本次查询");
        const searchWaitNumSub = self.data.searchWaitNum - 1;
        self.setData({
          searchWaitNum: searchWaitNumSub
        })
      }
    }, 500)
    
  },
  /**
   * 返回上一页(带选择值)
  */
  getAddress:function(e){
    var pages = getCurrentPages();
    // var currPage = pages[pages.length - 1];   //当前页面
    var prevPage = pages[pages.length - 2];  //上一个页面

    //直接调用上一个页面的setData()方法，把数据存到上一个页面中去
    let address = prevPage.data.address;
    address.address = e.currentTarget.dataset.name

    prevPage.setData({
      address: address
    });
    wx.navigateBack({
      delta: 1
    })
  },

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