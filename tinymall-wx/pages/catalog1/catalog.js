var util = require('../../utils/util.js');
var api = require('../../config/api.js');
// var WxParse = require('../../lib/wxParse/wxParse.js');
var app = getApp();
Page({
  data: {
    storeId: 1042785,//回龙观
    categoryList: [],
    currentCategory: {},
    currentSubCategoryList: {},
    scrollLeft: 0,
    scrollTop: 0,
    goodsCount: 0,
    scrollHeight: 0,
    page: 1,
    size: 20,
    clickid: 0,
    checkedSpecText: '规格数量选择',
    tmpSpecText: '请选择规格数量',
    goods: {},
    attribute: [],
    issueList: [],
    comment: [],
    brand: {},
    specificationList: [],
    productList: [],
    userHasCollect: 0,
    checkedSpecPrice: 0,
    relatedGoods: [],
    systemHeight:"0px"
  },
  onLoad: function () {
    let that = this;
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          systemHeight: res.windowHeight - (res.windowWidth / 750) * 94 + "px"
        })
      }
    })
    this.getCatalog();
  },
  getCatalog: function () {
    let that = this;
    wx.showLoading({
      title: '加载中...',
    });
    util.request(api.CatalogAndGoodsList, { storeId: that.data.storeId, page: that.data.page, size: that.data.size})
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
  getCurrentCategory: function (categoryId) {
    let that = this;
    util.request(api.GoodsListCurrent, { categoryId: categoryId, page: that.data.page, size: that.data.size })//左侧点击事件
      .then(function (res) {
        that.setData({
          currentCategory: res.data.currentCategory,
          currentSubCategoryList: res.data.goodsList
        });
      });
  },
  getCategoryInfo: function () {
    let that = this;
    util.request(api.GoodsCategory, { id: this.data.id })
      .then(function (res) {

        if (res.errno == 0) {
          that.setData({
            navList: res.data.brotherCategory,
            currentCategory: res.data.currentCategory
          });

          wx.setNavigationBarTitle({
            title: res.data.parentCategory.name
          })

          //nav位置
          let currentIndex = 0;
          let navListCount = that.data.navList.length;
          for (let i = 0; i < navListCount; i++) {
            currentIndex += 1;
            if (that.data.navList[i].id == that.data.id) {
              break;
            }
          }
          if (currentIndex > navListCount / 2 && navListCount > 5) {
            that.setData({
              scrollLeft: currentIndex * 60
            });
          }
          that.getGoodsList();

        } else {
          //显示错误信息
        }

      });
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
    wx.removeTabBarBadge({
      index: 1
    });
  },
  onUnload: function () {
    // 页面关闭
  },
  //右上角转发分享功能
  onShareAppMessage: function (){
    return{
      title:'烟酒茶行',
      path:'/pages/catalog1/catalog'
    }
  },
  getList: function () {
    var that = this;
    util.request(api.ApiRootUrl + 'api/catalog/' + that.data.currentCategory.catId)
      .then(function (res) {
        that.setData({
          categoryList: res.data,
        });
      });
  },
  switchCate: function (event) {
    var that = this;
    var currentTarget = event.currentTarget;
    if (this.data.clickid == event.currentTarget.dataset.id) {
      return false;
    }

    this.getCurrentCategory(event.currentTarget.dataset.id);
  },
  // addToCart: function(event){
  //   var that = this;
  //   that.getGoodsInfo(event.currentTarget.dataset.goodsid, event);
  // },
  addToCart: function(event){
    var that = this;
    // //根据选中的规格，判断是否有对应的sku信息
    // let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
    // if (!checkedProductArray || checkedProductArray.length <= 0) {
    //   //找不到对应的product信息，提示没有库存
    //   wx.showToast({
    //     image: '/static/images/icon_error.png',
    //     title: '没有库存'
    //   });
    //   return false;
    // }

    // let checkedProduct = checkedProductArray[0];
    //验证库存
    // if (checkedProduct.goodsNumber <= 0) {
    //   wx.showToast({
    //     image: '/static/images/icon_error.png',
    //     title: '没有库存'
    //   });
    //   return false;
    // }
    //添加到购物车
    util.request(api.CartAdd, { goodsId: event.currentTarget.dataset.goodsid, number: 1}, "POST")//TODO第三个参数不确定
      .then(function (res) {
        let _res = res;
        if (_res.errno == 0) {
          wx.showToast({
            title: '添加成功'
          });
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
  // getCheckedProductItem: function (key) {
  //   return this.data.productList.filter(function (v) {
  //     if (v.goodsSpecificationIds.toString() == key.toString()) {
  //       return true;
  //     } else {
  //       return false;
  //     }
  //   });
  // },
  // getCheckedSpecKey: function () {
  //   let checkedValue = this.getCheckedSpecValue().map(function (v) {
  //     return v.valueId;
  //   });
  //   return checkedValue;
  // },
  // //获取选中的规格信息
  // getCheckedSpecValue: function () {
  //   let checkedValues = [];
  //   let _specificationList = this.data.specificationList;
  //   for (let i = 0; i < _specificationList.length; i++) {
  //     let _checkedObj = {
  //       name: _specificationList[i].name,
  //       valueId: 0,
  //       valueText: ''
  //     };
  //     for (let j = 0; j < _specificationList[i].valueList.length; j++) {
  //       if (_specificationList[i].valueList[j].checked) {
  //         _checkedObj.valueId = _specificationList[i].valueList[j].id;
  //         _checkedObj.valueText = _specificationList[i].valueList[j].value;
  //       }
  //     }
  //     checkedValues.push(_checkedObj);
  //   }

  //   return checkedValues;
  // },
  //获取商品详情
  // getGoodsInfo: function (goodid, event) {
  //   let that = this;
  //   util.request(api.GoodsDetail, { id: goodid }).then(function (res) {
  //     if (res.errno === 0) {

  //       let _specificationList = res.data.specificationList
  //       // 如果仅仅存在一种货品，那么商品页面初始化时默认checked
  //       if (_specificationList.length == 1) {
  //         if (_specificationList[0].valueList.length == 1) {
  //           _specificationList[0].valueList[0].checked = true

  //           // 如果仅仅存在一种货品，那么商品价格应该和货品价格一致
  //           // 这里检测一下
  //           let _productPrice = res.data.productList[0].retailPrice;
  //           let _goodsPrice = res.data.info.retailPrice;
  //           if (_productPrice != _goodsPrice) {
  //             console.error('商品数量价格和货品不一致');
  //           }

  //           that.setData({
  //             checkedSpecText: _specificationList[0].valueList[0].value,
  //             tmpSpecText: '已选择：' + _specificationList[0].valueList[0].value,
  //           });
  //         }
  //       }

  //       that.setData({
  //         goods: res.data.info,
  //         attribute: res.data.attribute,
  //         issueList: res.data.issue,
  //         comment: res.data.comment,
  //         brand: res.data.brand,
  //         specificationList: res.data.specificationList,
  //         productList: res.data.productList,
  //         userHasCollect: res.data.userHasCollect,
  //         checkedSpecPrice: res.data.info.retailPrice
  //       });

  //       // if (res.data.userHasCollect == 1) {
  //       //   that.setData({
  //       //     collectBackImage: that.data.hasCollectImage
  //       //   });
  //       // } else {
  //       //   that.setData({
  //       //     collectBackImage: that.data.noCollectImage
  //       //   });
  //       // }

  //       WxParse.wxParse('goodsDetail', 'html', res.data.info.goodsDesc, that);

  //       that.getGoodsRelated(goodid, event);
  //     }
  //   });

  // },
  // getGoodsRelated: function (goodid,event) {
  //   let that = this;
  //   util.request(api.GoodsRelated, { id: goodid }).then(function (res) {
  //     if (res.errno === 0) {
  //       that.setData({
  //         relatedGoods: res.data.goodsList,
  //       });
  //       that.addToCart2(event);
  //     }
  //   });

  // },
  
})
