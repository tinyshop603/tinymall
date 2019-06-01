var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var app = getApp();
Page({
  data: {
    storeId: "",
    page: 1,
    size: 100,
    goodsCount: 0,
    categoryList: [],
    currentCategory: {},
    currentSubCategoryList: {},
    allGoodsList:[],
    curCategoryId:"g",
    curCategoryIndex:0,
    scrollHigh:0,
    isCalu:false,
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
          currentCategory: res.data.categoryList[0],// 初始化使用第一个
          curCategoryId: 'g' + res.data.categoryList[0].id,
          // currentSubCategoryList:res.data.currentSubCategoryList,
          allGoodsList: res.data.allGoodsList
        });
        wx.hideLoading();
      });
    // util.request(api.GoodsCount, { storeid:that.data.storeId}).then(function (res) {
    //   that.setData({
    //     goodsCount: res.data.goodsCount
    //   });
    // });
  },

  // 左侧L2点击事件
  switchCate: function (event) {
    var that = this;
    if (this.data.currentCategory.id == event.currentTarget.dataset.id) {
      return false;
    }
    this.getCurrentCategory(event.currentTarget.dataset.id, event.currentTarget.dataset.index);
  },
  // 锚点跳转
  getCurrentCategory: function (categoryId,index) {
    let that = this;
    that.setData({
      curCategoryId: categoryId,
      curCategoryIndex: index
    });
    // util.request(api.GoodsListCurrent, { categoryId: categoryId, page: that.data.page, size: that.data.size })
    //   .then(function (res) {
    //     that.setData({
    //       currentCategory: res.data.currentCategory,
    //       currentSubCategoryList: res.data.goodsList
    //     });
    //   });
  },
  // 监听goods滚动，判断调节左侧展示
  // monitorScroll:function(event){
  //   var that = this;
  //   console.log(event);
  //   var isCalu = that.data.isCalu;
  //   if (isCalu){
  //     return false;
  //   }
  //   that.setData({
  //     isCalu:true
  //   });
  //   var curScrollHeight = event.detail.scrollTop
  //   //创建节点选择器
  //   // var query = wx.createSelectorQuery();
  //   var curCategoryId = this.data.categoryList[this.data.curCategoryIndex].id;
  //   console.log(curCategoryId);
  //   wx.createSelectorQuery().select("#g" + curCategoryId).fields({
  //       dataset: true,
  //       size: true,
  //       scrollOffset: true,
  //       properties: ['scrollX', 'scrollY'],
  //       context: true,
  //     }, function (res) {
  //       var scrollHigh = that.data.scrollHigh;
  //       if (event.detail.scrollTop > (res.height + scrollHigh)){
  //         var categoryList = that.data.categoryList;
  //         var index = that.data.curCategoryIndex;
  //         var nextIndex = (index == categoryList.length) ? index : ++index;
  //         that.setData({
  //           curCategoryIndex: nextIndex,
  //           scrollHigh: scrollHigh + res.height
  //           // curCategoryId: "g" + categoryList[nextIndex].id,
  //         });
  //       } 
  //       that.setData({
  //         isCalu: false
  //       });
  //     }).exec()
  // }
  

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