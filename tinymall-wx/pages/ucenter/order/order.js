var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

Page({
  data:{
    orderList: [],
    showType: 2
  },
  onLoad:function(options){
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
              res.data.data[i].goodsList[j].picUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?"))+"?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
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
    this.setData({
      showType: showType
    });
    this.getOrderList();
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