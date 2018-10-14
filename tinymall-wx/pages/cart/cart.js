var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var user = require('../../utils/user.js');

var app = getApp();

Page({
  data: {
    cartGoods: [],
    cartTotal: {
      "goodsCount": 0,
      "goodsAmount": 0.00,
      "checkedGoodsCount": 0,
      "checkedGoodsAmount": 0.00
    },
    isEditCart: false,
    checkedAllStatus: true,
    editCartList: [],
    hasLogin: false
  },
  //右上角转发分享功能
  onShareAppMessage: function () {
    return {
      title: '烟酒茶行',
      path: '/pages/catalog1/catalog'
    }
  },
  onLoad: function (options) {
    
    // 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
    if (app.globalData.hasLogin){
      this.getCartList();
      util.request(api.CartGoodsCount).then(function (res) {
        if (res.errno === 0) {
          if (res.data != 0) {
            wx.setTabBarBadge({
              index: 1,
              text: res.data.toString()
            });
          }else{
            wx.removeTabBarBadge({
              index: 1
            });
          }

        }
      });
    }else{
      this.goLogin();
    }

    this.setData({
      isEditCart: false,
      hasLogin: app.globalData.hasLogin
    });

  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },
  goLogin() {
    wx.navigateTo({ url: "/pages/auth/login/login" });
  },
  getCartList: function () {
    let that = this;
    util.request(api.CartList).then(function (res) {
      if (res.errno === 0) {
        //wz-截取图片格式
        if (res.data.cartList.length > 0) {
          for (let i = 0; i < res.data.cartList.length; i++) {
            let oldPicUrl = res.data.cartList[i].picUrl;
            res.data.cartList[i].picUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?"))+"?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
          }
        }
        that.setData({
          cartGoods: res.data.cartList,
          cartTotal: res.data.cartTotal
        });

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      }
    });
  },
  isCheckedAll: function () {
    //判断购物车商品已全选
    return this.data.cartGoods.every(function (element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });
  },
  doCheckedAll: function () {
    let checkedAll = this.isCheckedAll()
    this.setData({
      checkedAllStatus: this.isCheckedAll()
    });
  },
  checkedItem: function (event) {
    let itemIndex = event.target.dataset.itemIndex;
    let that = this;

    let productIds = [];
    productIds.push(that.data.cartGoods[itemIndex].productId);
    if (!this.data.isEditCart) {
      util.request(api.CartChecked, { productIds: productIds, isChecked: that.data.cartGoods[itemIndex].checked ? 0 : 1 }, 'POST').then(function (res) {
        if (res.errno === 0) {
          //wz-截取图片格式
          if (res.data.cartList.length > 0) {
            for (let i = 0; i < res.data.cartList.length; i++) {
              let oldPicUrl = res.data.cartList[i].picUrl;
              res.data.cartList[i].picUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?"))+"?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
            }
          }
          that.setData({
            cartGoods: res.data.cartList,
            cartTotal: res.data.cartTotal
          });
        }

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    } else {
      //编辑状态
      let tmpCartData = this.data.cartGoods.map(function (element, index, array) {
        if (index == itemIndex){
          element.checked = !element.checked;
        }
        
        return element;
      });

      that.setData({
        cartGoods: tmpCartData,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }
  },
  //TODO wz-用于变更商品数量时计算总量，接口可优化
  checkedNum: function (event) {
    let itemIndex = event.target.dataset.itemIndex;
    let that = this;

    let productIds = [];
    productIds.push(that.data.cartGoods[itemIndex].productId);
      util.request(api.CartChecked, { productIds: productIds, isChecked: that.data.cartGoods[itemIndex].checked ? 1 : 0 }, 'POST').then(function (res) {
        if (res.errno === 0) {
          that.setData({
            cartTotal: res.data.cartTotal
          });
          //显示bar文本
          wx.setTabBarBadge({
            index: 1,
            text: res.data.cartTotal.goodsCount.toString()
          });
        }

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
  },

  getCheckedGoodsCount: function(){
    let checkedGoodsCount = 0;
    this.data.cartGoods.forEach(function (v) {
      if (v.checked === true) {
        checkedGoodsCount += v.number;
      }
    });
    console.log(checkedGoodsCount);
    return checkedGoodsCount;
  },
  checkedAll: function () {
    let that = this;

    if (!this.data.isEditCart) {
      var productIds = this.data.cartGoods.map(function (v) {
        return v.productId;
      });
      util.request(api.CartChecked, { productIds: productIds, isChecked: that.isCheckedAll() ? 0 : 1 }, 'POST').then(function (res) {
        if (res.errno === 0) {
          console.log(res.data);
          //wz-截取图片格式
          if (res.data.cartList.length > 0) {
            for (let i = 0; i < res.data.cartList.length; i++) {
              let oldPicUrl = res.data.cartList[i].picUrl;
              res.data.cartList[i].picUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?"))+"?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
            }
          }
          that.setData({
            cartGoods: res.data.cartList,
            cartTotal: res.data.cartTotal
          });
        }

        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    } else {
      //编辑状态
      let checkedAllStatus = that.isCheckedAll();
      let tmpCartData = this.data.cartGoods.map(function (v) {
        v.checked = !checkedAllStatus;
        return v;
      });
      
      that.setData({
        cartGoods: tmpCartData,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }

  },
  editCart: function () {
    var that = this;
    if (this.data.isEditCart) {
      this.getCartList();
      this.setData({
        isEditCart: !this.data.isEditCart
      });
    } else {
      //编辑状态
      let tmpCartList = this.data.cartGoods.map(function (v) {
        v.checked = false;
        return v;
      });
      this.setData({
        editCartList: this.data.cartGoods,
        cartGoods: tmpCartList,
        isEditCart: !this.data.isEditCart,
        checkedAllStatus: that.isCheckedAll(),
        'cartTotal.checkedGoodsCount': that.getCheckedGoodsCount()
      });
    }

  },
  updateCart: function (productId, goodsId, number, id, event) {
    let that = this;

    util.request(api.CartUpdate, {
      productId: productId,
      goodsId: goodsId,
      number: number,
      id: id
    }, 'POST').then(function (res) {
      that.setData({
        checkedAllStatus: that.isCheckedAll()
      });
      that.checkedNum(event);//wz-更新货物价格

    });

  },
  cutNumber: function (event) {

    let itemIndex = event.target.dataset.itemIndex;
    let cartItem = this.data.cartGoods[itemIndex];
    let number = (cartItem.number - 1 > 1) ? cartItem.number - 1 : 1;
    cartItem.number = number;
    this.setData({
      cartGoods: this.data.cartGoods
    });
    this.updateCart(cartItem.productId, cartItem.goodsId, number, cartItem.id, event);//wz-m更新货物价格
  },
  addNumber: function (event) {
    let itemIndex = event.target.dataset.itemIndex;
    let cartItem = this.data.cartGoods[itemIndex];
    let number = cartItem.number + 1;
    cartItem.number = number;
    this.setData({
      cartGoods: this.data.cartGoods
    });
    this.updateCart(cartItem.productId, cartItem.goodsId, number, cartItem.id, event);//wz-m更新货物价格
  },
  checkoutOrder: function () {
    //获取已选择的商品
    let that = this;

    var checkedGoods = this.data.cartGoods.filter(function (element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });

    if (checkedGoods.length <= 0) {
      return false;
    }
    //后期需要改
    // wx.removeTabBarBadge({
    //   index: 1
    // });
    // storage中设置了cartId，则是购物车购买
    try {
      wx.setStorageSync('cartId', 0);
      wx.navigateTo({
        url: '../shopping/checkout/checkout'
      })
    } catch (e) {
    }

  },
  deleteCart: function () {
    //获取已选择的商品
    let that = this;

    let productIds = this.data.cartGoods.filter(function (element, index, array) {
      if (element.checked == true) {
        return true;
      } else {
        return false;
      }
    });

    if (productIds.length <= 0) {
      return false;
    }

    productIds = productIds.map(function (element, index, array) {
      if (element.checked == true) {
        return element.productId;
      }
    });


    util.request(api.CartDelete, {
      productIds: productIds
    }, 'POST').then(function (res) {
      if (res.errno === 0) {
        console.log(res.data);
        let cartList = res.data.cartList.map(v => {
          v.checked = false;
          return v;
        });
        //wz-截取图片格式
        if (res.data.cartList.length > 0) {
          for (let i = 0; i < res.data.cartList.length; i++) {
            let oldPicUrl = res.data.cartList[i].picUrl;
            res.data.cartList[i].picUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?"))+"?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
          }
        }
        that.setData({
          cartGoods: cartList,
          cartTotal: res.data.cartTotal
        });
        //显示bar文本
        wx.setTabBarBadge({
          index: 1,
          text: res.data.cartTotal.goodsCount.toString()
        });
      }

      that.setData({
        checkedAllStatus: that.isCheckedAll()
      });
    });
  }
})