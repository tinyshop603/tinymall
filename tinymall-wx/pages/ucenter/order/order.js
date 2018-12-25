var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data:{
    orderList: [],
    showType: 1
  },
  //右上角转发分享功能
  onShareAppMessage: function () {
    return {
      title: '烟酒茶行',
      path: '/pages/catalog1/catalog'
    }
  },
  onLoad:function(options){
    this.setData({
      showType: options.showType
    });
    // 页面初始化 options为页面跳转所带来的参数
  },
  getOrderList(){
    let that = this;
    util.request(api.OrderList, { showType: that.data.showType}).then(function (res) {
      if (res.errno === 0) {
        console.log(res.data);
        //wz-截取图片格式
        if (res.data.data.length > 0) {
          for (let i = 0; i < res.data.data.length; i++) {
            for (let j = 0; j < res.data.data[i].goodsList.length; j++){
              let oldPicUrl = res.data.data[i].goodsList[j].picUrl;
              if(oldPicUrl.indexOf("fuss10") != -1){
                res.data.data[i].goodsList[j].picUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?"))+"?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
              }            
            }           
          }
        }
        that.setData({
          orderList: res.data.data
        });
      }
    });
  },
  switchTab: function (event) {
    let showType = event.currentTarget.dataset.index;
    if (typeof (showType) != "undefined"){
      this.setData({
        showType: showType
      });
    }
    
    this.getOrderList();
  },
  // “取消订单”点击效果
  cancelOrder: function (event) {
    let that = this;
    let orderId = event.target.dataset.orderId;

    wx.showModal({
      title: '',
      content: '确定要取消此订单？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderCancel, {
            orderId: orderId
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '取消订单成功',
                duration: 2000, //毫秒，默认：1500
                mask: false,
                success: function () {
                  that.setData({
                    showType: 5
                  });
                  that.getOrderList();
                }
              });
              // util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  },
  // “去付款”按钮点击效果
  payOrder: function (event) {
    let that = this;
    let orderId = event.target.dataset.orderId;
    util.request(api.OrderPrepay, {
      orderId: orderId
    }, 'POST').then(function (res) {
      if (res.errno === 0) {
        const payParam = res.data;
        console.log("支付过程开始")
        wx.requestPayment({
          'timeStamp': payParam.timeStamp,
          'nonceStr': payParam.nonceStr,
          'package': payParam.packageValue,
          'signType': payParam.signType,
          'paySign': payParam.paySign,
          'success': function (res) {
            console.log("支付过程成功")
            that.setData({
              showType: 2
            });
            that.getOrderList();
            // util.redirect('/pages/ucenter/order/order');
          },
          'fail': function (res) {
            console.log("支付过程失败")
            util.showErrorToast('支付失败');
          },
          'complete': function (res) {
            console.log("支付过程结束")
          }
        });
      }
    });

  },
  // “确认收货”点击效果
  confirmOrder: function (event) {
    let that = this;
    let orderId = event.target.dataset.orderId;
    wx.showModal({
      title: '',
      content: '确认收货？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderConfirm, {
            orderId: orderId
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '确认收货成功！',
                duration: 2000, //毫秒，默认：1500
                mask: false,
                success: function () {
                  that.setData({
                    showType: 4
                  });
                  that.getOrderList();
                }
              });
              // util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  },
  // “删除”点击效果
  deleteOrder: function (event) {
    let that = this;
    let orderId = event.target.dataset.orderId;
    wx.showModal({
      title: '',
      content: '确定要删除此订单？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderDelete, {
            orderId: orderId
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '删除成功',
                duration: 2000, //毫秒，默认：1500
                mask: false,
                success: function () {
                  that.getOrderList();
                }
              });
              
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  }, 
  // “取消订单并退款”点击效果
  refundOrder: function (event) {
    let that = this;
    // let orderInfo = that.data.orderInfo;
    let orderId = event.target.dataset.orderId;
    wx.showModal({
      title: '',
      content: '确定要申请退款？',
      success: function (res) {
        if (res.confirm) {
          util.request(api.OrderRefund, {
            orderId: orderId
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
              wx.showToast({
                title: '申请退款成功',
                duration: 2000, //毫秒，默认：1500
                mask: false,
                success: function () {
                  that.getOrderList();
                }
              });
              // util.redirect('/pages/ucenter/order/order');
            }
            else {
              util.showErrorToast(res.errmsg);
            }
          });
        }
      }
    });
  },  
  onReady:function(){
    // 页面渲染完成
  },
  onShow:function(){
    // 页面显示
    this.getOrderList();
  },
  onHide:function(){
    // 页面隐藏
  },
  onUnload:function(){
    // 页面关闭
  }
})