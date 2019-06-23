var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var app = getApp();
Page({
  data: {
    storeId: "",
    addressText: "点击获取位置信息",
    page: 1,
    size: 100,
    goodsCount: 0,
    categoryList: [],
    currentCategory: {},
    currentSubCategoryList: {},
    allGoodsList:[],
    curCategoryId:"g",
    curCategoryIndex:0,
    subHeightArr:[0],
    toView:"",
    systemHeight: ""
  },
  onLoad: function () {
    let that = this;

    wx.showToast({
      title: '成功',
      icon: 'success',
      duration: 2000
    })

    // 页面加载时获取定位
    this.getLocation();
    // 根据手机可用高度动态赋予容器高度
    wx.getSystemInfo({
      success: function (sysInfo) {
        wx.createSelectorQuery().select('#toolBar').fields({
          dataset: true,
          size: true,
          scrollOffset: true,
          properties: ['scrollX', 'scrollY'],
          computedStyle: ['margin', 'backgroundColor'],
          context: true,
        }, function (res) {
          const systemHeight = sysInfo.windowHeight - res.height 
          that.setData({
            systemHeight: systemHeight + "px"
          })
        }).exec()
      }
    })
    // 商店ID赋值
    this.setData({
      storeId: app.globalData.storeId
    })
    // 页面加载时获取商品
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
  //TODO 需要做懒加载技术
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
        console.log("Catalog:" + new Date());
        wx.hideLoading();
      });
    that.caluSubHeight();
  },

  // 计算右侧高度进行数组
  caluSubHeight:function () {
    let that = this;
    var categoryList = this.data.categoryList;
    var timeoutId;
    if (categoryList.length === 0){
      var timeoutId = setTimeout(function () {
        that.caluSubHeight();
      }, 500)
    }
    // 清除定时器
    // clearTimeout(timeoutId);
    var index = 0;
    for (var i = 0; i < categoryList.length; i++) {
      wx.createSelectorQuery().select("#g" + categoryList[i].id).fields({
        dataset: true,
        size: true,
        scrollOffset: true,
        properties: ['scrollX', 'scrollY'],
        context: true,
      }, function (result) {
        var height = parseInt(that.data.subHeightArr[index]) + result.height;

          that.setData({
            subHeightArr: that.data.subHeightArr.concat(height)
          })
        index++;
      }).exec();
    }
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
        console.log(res);
        const latitude = res.latitude; // 纬度，浮点数
        const longitude = res.longitude; // 经度，浮点数
        util.request(api.ConsumerLocation, { lat: latitude, lng: longitude }, "GET")
          .then(function (res) {
            console.log(res);
            var address;
            if (res.erron === -1){
              console.log("位置获取错误");
            } else if (res.data.poiRegions && res.data.poiRegions.length > 0){
              address = res.data.poiRegions[0].name;
            }else{
              address = res.data.formattedAddress;
            }
            self.setData({
              addressText: address,
            })
            // 超出配送范围提示
            if (res.data.distributionStatus == 'out') {
              wx.showModal({
                content: '您当前位置超出服务范围',
                confirmText: '知道了',
                confirmColor: '#2F9F42',
                showCancel: false
              })
            }
            
            console.log("location:"+new Date());
          });
      },
      // fail: function (res) {
      //   //未授权就弹出弹窗提示用户重新授权
      //   self.reAuthorize();
      // },
      complete: function (res) {
      }
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

  /**
   * 重新授权位置信息
   */
  // reAuthorize: function () {
  //   var self = this
  //   self.setData({ hiddenReAuthorizePop: false })
  // },

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
  monitorScroll:function(event){
    var that = this;
    var curScrollHeight = event.detail.scrollTop;
    // console.log(curScrollHeight);
    var curCategoryIndex = this.data.curCategoryIndex;
    var minHeight = this.data.subHeightArr[curCategoryIndex];
    var maxHeight = this.data.subHeightArr[curCategoryIndex+1];
    if (event.detail.scrollTop > maxHeight) {
      that.setData({
        curCategoryIndex: curCategoryIndex + 1
      });
    } else if (event.detail.scrollTop < minHeight){
      that.setData({
        curCategoryIndex: curCategoryIndex - 1
      });
    }
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