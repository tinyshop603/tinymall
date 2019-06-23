var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

var app = getApp();

Page({
  data: {
    checkedGoodsList: [],
    checkedAddress: {},
    checkedCoupon: [],
    couponList: [],
    goodsTotalPrice: 0.00, //商品总价
    freightPrice: 0.00,    //快递费
    couponPrice: 0.00,     //优惠券的价格
    orderTotalPrice: 0.00,  //订单总价
    actualPrice: 0.00,     //实际需要支付的总价
    cartId: 0,
    addressId: 0,
    couponId: 0,
    remark:"",
    isValidAddress:false

    // pickerIndex: 1,
    // array: ['货到付款', '在线支付'],
    // objectArray:[
    //   {
    //     id:0,
    //     name:'货到付款'
    //   },
    //   {
    //     id: 1,
    //     name: '在线支付'
    //   }
    // ]
  },
  // pickerIndex: function (e) {
  //   this.setData({
  //     pickerIndex: e.detail.value
  //   })
  // },
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
  getCheckoutInfo: function () {
    let that = this;
    util.request(api.CartCheckout, { cartId: that.data.cartId, addressId: that.data.addressId, couponId: that.data.couponId }).then(function (res) {
      if (res.errno === 0) {
        //wz-截取图片格式
        if (res.data.checkedGoodsList.length > 0) {
          for (let i = 0; i < res.data.checkedGoodsList.length; i++) {
              res.data.checkedGoodsList[i].picUrl = res.data.checkedGoodsList[i].picUrl + "?x-oss-process=image/resize,m_fixed,h_120,w_120";       
          }
        }
        that.setData({
          checkedGoodsList: res.data.checkedGoodsList,
          checkedAddress: res.data.checkedAddress,
          actualPrice: res.data.actualPrice,
          checkedCoupon: res.data.checkedCoupon,
          couponList: res.data.couponList,
          couponPrice: res.data.couponPrice,
          freightPrice: res.data.freightPrice,
          goodsTotalPrice: res.data.goodsTotalPrice,
          orderTotalPrice: res.data.orderTotalPrice,
          addressId: res.data.addressId,
          couponId: res.data.couponId,
          isValidAddress: res.data.isValidAddress
        });
        if (!res.data.isValidAddress) {
          that.showAdressOutBox();
        }
      }
      wx.hideLoading();
    });
  },
  showAdressOutBox: function () {
    var that = this;
    // 超出配送范围提示
    wx.showModal({
      content: '收货地址超出服务范围',
      confirmText: '重选地址',
      confirmColor: '#2F9F42',
      success(res) {
        if (res.confirm) {
          that.selectAddress();
        }
      }
    })
  },
  selectAddress() {
    wx.navigateTo({
      url: '/pages/shopping/address/address',
    })
  },
  addAddress() {
    wx.navigateTo({
      url: '/pages/shopping/addressAdd/addressAdd',
    })
  },
  onReady: function () {
    // 页面渲染完成

  },
  onShow: function () {
    // 页面显示
    wx.showLoading({
      title: '加载中...',
    })
    try {
      var cartId = wx.getStorageSync('cartId');
      if (cartId) {
        this.setData({
          'cartId': cartId
        });
      }

      var addressId = wx.getStorageSync('addressId');
      if (addressId) {
        this.setData({
          'addressId': addressId
        });
      }

      var couponId = wx.getStorageSync('couponId');
      if (couponId) {
        this.setData({
          'couponId': couponId
        });
      }
    } catch (e) {
      // Do something when catch error
      console.log(e);
    }
    this.getCheckoutInfo();
  },
  onHide: function () {
    // 页面隐藏

  },
  onUnload: function () {
    // 页面关闭

  },
  submitOrder: function () {
    var that = this;
    if (this.data.addressId <= 0) {
      util.showErrorToast('请选择收货地址');
      return false;
    }

    //获取当前时间戳
    var timestamp = Date.parse(new Date());
    timestamp = timestamp / 1000;
    //获取当前时间
    var n = timestamp * 1000;
    var date = new Date(n);
    //时
    var h = date.getHours();
    //分
    var m = date.getMinutes();
    //营业时间：8:30~24:00
     var ifOpen = false;
     if(h>8){
       ifOpen=true;
     }else if(h=8 && m>30){
       ifOpen = true;
     }
    // if (!ifOpen){
    //    wx.showModal({
    //      title: '当前时间尚未营业',
    //      content:'营业时间:8:30~24:00',
    //      showCancel:false,
    //    })
    //    return false;
    //  }
    if (!this.data.isValidAddress) {
      that.showAdressOutBox();
    }

    that.submitPrepay();

    //判断支付方式
    // if (that.data.pickerIndex == 1){
    //   that.submitPrepay();
    // } else if (that.data.pickerIndex == 0){
    //   that.submitAfterpay();
    // }


    
       
  },
  
  //微信支付
  submitPrepay:function(){
    var that = this;
    util.request(api.OrderSubmit, { cartId: this.data.cartId, addressId: this.data.addressId, couponId: this.data.couponId, modeId: this.data.pickerIndex, remarkText: this.data.remark}, 'POST').then(res => {
      if (res.errno === 0) {
        const orderId = res.data.orderId;
        util.request(api.OrderPrepay, {
          orderId: orderId
        }, 'POST').then(function (res) {
          if (res.errno === 0) {
            const payParam = res.data;
            console.log("支付过程开始")
            that.hideLoading();
            wx.requestPayment({
              'timeStamp': payParam.timeStamp,
              'nonceStr': payParam.nonceStr,
              'package': payParam.package_,
              'signType': payParam.signType,
              'paySign': payParam.paySign,
              'success': function (res) {
                console.log("支付过程成功")
                wx.redirectTo({
                  url: '/pages/payResult/payResult?status=1&orderId=' + orderId
                });
              },
              'fail': function (res) {
                console.log("支付过程失败")
                wx.redirectTo({
                  url: '/pages/payResult/payResult?status=0&orderId=' + orderId
                });
              },
              'complete': function (res) {
                that.hideLoading();
                console.log("支付过程结束")
              }
            });
          }
        });
      } else {
        wx.redirectTo({
          url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        });
      }
    });
  },

  //货到付款
  // submitAfterpay:function(){
  //   var that = this;
  //   util.request(api.OrderSubmit, { cartId: this.data.cartId, addressId: this.data.addressId, couponId: this.data.couponId, modeId: this.data.pickerIndex, remarkText: this.data.remark}, 'POST').then(res => {
  //     if (res.errno === 0) {
  //       const orderId = res.data.orderId;
  //       wx.redirectTo({
  //         url: '/pages/payResult/payResult?status=1&orderId=' + orderId
  //       });


  //     } else {
  //       wx.redirectTo({
  //         url: '/pages/payResult/payResult?status=0&orderId=' + orderId
  //       });
  //     }
  //     that.hideLoading();
  //   });
  // },

  showLoading: function(message) {
    if(wx.showLoading) {
      // 基础库 1.1.0 微信6.5.6版本开始支持，低版本需做兼容处理
      wx.showLoading({
        title: message,
        mask: true
      });
    } else {
      // 低版本采用Toast兼容处理并将时间设为20秒以免自动消失
      wx.showToast({
        title: message,
        icon: 'loading',
        mask: true,
        duration: 20000
      });
    }
  },
 
 hideLoading : function() {
    if (wx.hideLoading) {
      // 基础库 1.1.0 微信6.5.6版本开始支持，低版本需做兼容处理
      wx.hideLoading();
    } else {
      wx.hideToast();
    }
  },


  //TODO 需要测试 可能直接失去焦点
  onKeywordConfirm:function(event){
    this.setData({
      'remark': event.detail.value
    });
  }
})



        // 模拟支付成功，同理，后台也仅仅是返回一个成功的消息而已
        // wx.showModal({
        //   title: '目前不能微信支付',
        //   content: '点击确定模拟支付成功，点击取消模拟未支付成功',
        //   success: function(res) {
        //     if (res.confirm) {
        //       util.request(api.OrderPrepay, { orderId: orderId }, 'POST').then(res => {
        //         if (res.errno === 0) {
        //           wx.redirectTo({
        //             url: '/pages/payResult/payResult?status=1&orderId=' + orderId
        //           });
        //         }
        //         else{
        //           wx.redirectTo({
        //             url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        //           });
        //         }
        //       });
        //     }
        //     else if (res.cancel) {
        //       wx.redirectTo({
        //         url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        //       });
        //     }

        //   }
        // });

        // util.request(api.OrderPrepay, {
        //   orderId: orderId
        // }, 'POST').then(function (res) {
        //   if (res.errno === 0) {
        //     const payParam = res.data;
        //     console.log("支付过程开始")
        //     wx.requestPayment({
        //       'timeStamp': payParam.timeStamp,
        //       'nonceStr': payParam.nonceStr,
        //       'package': payParam.packageValue,
        //       'signType': payParam.signType,
        //       'paySign': payParam.paySign,
        //       'success': function (res) {
        //         console.log("支付过程成功")
        //         wx.redirectTo({
        //           url: '/pages/payResult/payResult?status=1&orderId=' + orderId
        //         });
        //       },
        //       'fail': function (res) {
        //         console.log("支付过程失败")
        //         wx.redirectTo({
        //           url: '/pages/payResult/payResult?status=0&orderId=' + orderId
        //         });
        //       },
        //       'complete': function (res) {
        //         console.log("支付过程结束")
        //       }
        //     });
        //   }
        // });