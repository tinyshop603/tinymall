var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var app = getApp();
Page({
  data: {
    storeId: "",
    page: 1,
    size: 20,
    goodsCount: 0,
    categoryList: [],
    currentCategory: {},
    currentSubCategoryList: {},
    systemHeight: ""
  },
  onLoad: function () {
    let that = this;
    //根据手机可用高度动态赋予容器高度
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          systemHeight: res.windowHeight - (res.windowWidth / 750) * 94 + "px"
        })
      }
    })
    //商店ID赋值
    this.setData({
      storeId: app.globalData.storeId
    })
    this.getCatalog();
  },

  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
    var that = this;
    //TODO 是否要登录
    util.request(api.CartGoodsCount).then(function (res) {
      if (res.errno === 0) {
        if (res.data != 0){
          wx.setTabBarBadge({
            index: 1,
            text: res.data.toString()
          });
        }
        
      }
    });
  },
  onHide: function () {
    // 页面隐藏
    // wx.removeTabBarBadge({
    //   index: 1
    // });
  },
  onUnload: function () {
    // 页面关闭
  },
  //右上角转发分享功能
  onShareAppMessage: function () {
    return {
      title: '烟酒茶行',
      path: '/pages/catalog1/catalog'
    }
  },
  //TODO 当前只支持每页获取最多20（可调）个商品，将来会做懒加载技术
    //TODO 是否能通过一次请求获取以下信息
  getCatalog: function () {
    let that = this;
    wx.showLoading({
      title: '加载中...',
    });

    // util.request(api.CatalogAndGoodsList, { storeId: that.data.storeId, page: that.data.page, size: that.data.size})
    util.request(api.FirstScreenUrl, { storeId: that.data.storeId, page: that.data.page, size: that.data.size })
      .then(function (res) {
        that.setData({
          categoryList: res.data.categoryList,
          currentCategory: res.data.currentCategory,
          currentSubCategoryList:res.data.currentSubCategoryList
        });
        wx.hideLoading();
      });
    util.request(api.GoodsCount, { storeid:that.data.storeId}).then(function (res) {
      that.setData({
        goodsCount: res.data.goodsCount
      });
    });
  },

  //左侧L2点击事件
  switchCate: function (event) {
    var that = this;
    if (this.data.currentCategory.id == event.currentTarget.dataset.id) {
      return false;
    }
    this.getCurrentCategory(event.currentTarget.dataset.id);
  },
  //TODO 当前只支持每页获取最多20（可调）个商品，将来会做懒加载技术
  getCurrentCategory: function (categoryId) {
    let that = this;
    util.request(api.GoodsListCurrent, { categoryId: categoryId, page: that.data.page, size: that.data.size })
      .then(function (res) {
        that.setData({
          currentCategory: res.data.currentCategory,
          currentSubCategoryList: res.data.goodsList
        });
      });
  },

  //添加商品
  addToCart: function (event) {
    var that = this;
    //TODO第三个参数不确定
    util.request(api.CartAdd, { goodsId: event.currentTarget.dataset.goodsid, number: 1 }, "POST")
      .then(function (res) {
        let _res = res;
        if (_res.errno == 0) {
          wx.showToast({
            title: '添加成功'
          });
          //显示bar文本
          wx.setTabBarBadge({
            index: 1,
            text: _res.data.toString()
          });
        } else {
          wx.showToast({
            image: '/static/images/icon_error.png',
            title: _res.errmsg,
            mask: true
          });
        }
      });
  },
})
