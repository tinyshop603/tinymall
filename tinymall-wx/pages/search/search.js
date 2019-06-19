var util = require('../../utils/util.js');
var api = require('../../config/api.js');
var WxParse = require('../../lib/wxParse/wxParse.js');
var app = getApp();
//TODO 历史记录:没有对字体长度做限制<28
Page({
  data: {
    storeId: "",
    keywrod: '',
    searchStatus: false,
    goodsList: [],
    helpKeyword: [],
    historyKeyword: [],
    categoryFilter: false,
    currentSortType: 'default',
    currentSortOrder: 'desc',
    filterCategory: [],
    defaultKeyword: {},
    hotKeyword: [],
    page: 1,
    size: 100,
    categoryId: 0,

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
    collectBackImage: '/static/images/icon_collect.png',
    relatedGoods: [],
    picUrlSuffix: "?x-oss-process=image/resize,m_fixed,h_120,w_120"
  },
  //右上角转发分享功能
  onShareAppMessage: function () {
    return {
      title: '烟酒茶行',
      path: '/pages/catalog1/catalog'
    }
  },
  //事件处理函数
  closeSearch: function () {
    wx.navigateBack()
  },
  clearKeyword: function () {
    this.setData({
      keyword: '',
      searchStatus: false
    });
  },
  onLoad: function () {
    //商店ID赋值
    this.setData({
      storeId: app.globalData.storeId
    })
    this.getSearchKeyword();
  },

  getSearchKeyword() {
    let that = this;
    util.request(api.SearchIndex).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          historyKeyword: res.data.historyKeywordList,
          defaultKeyword: res.data.defaultKeyword,
          hotKeyword: res.data.hotKeywordList
        });
      }
    });
  },

  inputChange: function (e) {

    this.setData({
      keyword: e.detail.value,
      searchStatus: false
    });
    this.getHelpKeyword();
  },
  getHelpKeyword: function () {
    let that = this;
    util.request(api.SearchHelper, { keyword: that.data.keyword }).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          helpKeyword: res.data
        });
      }
    });
  },
  inputFocus: function () {
    this.setData({
      searchStatus: false,
      goodsList: []
    });

    if (this.data.keyword) {
      this.getHelpKeyword();
    }
  },
  clearHistory: function () {
    this.setData({
      historyKeyword: []
    })

    util.request(api.SearchClearHistory, {}, 'POST')
      .then(function (res) {
        console.log('清除成功');
      });
  },
  getGoodsList: function () {
    let that = this;
    util.request(api.GoodsList, {storeId:that.data.storeId, keyword: that.data.keyword, page: that.data.page, size: that.data.size, sort: that.data.currentSortType, order: that.data.currentSortOrder, categoryId: that.data.categoryId }).then(function (res) {
      if (res.errno === 0) {
        //wz-截取图片格式
        if (res.data.goodsList.length > 0) {
          for (let i = 0; i < res.data.goodsList.length; i++) {
            res.data.goodsList[i].listPicUrl = res.data.goodsList[i].listPicUrl + picUrlSuffix;
          }
        }
        that.setData({
          searchStatus: true,
          categoryFilter: false,
          goodsList: res.data.goodsList,
          filterCategory: res.data.filterCategoryList
        });
      }

      //重新获取关键词
      that.getSearchKeyword();
    });
  },
  onKeywordTap: function (event) {

    this.getSearchResult(event.target.dataset.keyword);

  },
  getSearchResult(keyword) {
    if(keyword === ''){
      keyword = this.data.defaultKeyword.keyword;
    }
    this.setData({
      keyword: keyword,
      page: 1,
      categoryId: 0,
      goodsList: []
    });

    this.getGoodsList();
  },
  openSortFilter: function (event) {
    let currentId = event.currentTarget.id;
    switch (currentId) {
      case 'categoryFilter':
        this.setData({
          categoryFilter: !this.data.categoryFilter,
          currentSortOrder: 'asc'
        });
        break;
      case 'priceSort':
        let tmpSortOrder = 'asc';
        if (this.data.currentSortOrder == 'asc') {
          tmpSortOrder = 'desc';
        }
        this.setData({
          currentSortType: 'price',
          currentSortOrder: tmpSortOrder,
          categoryFilter: false
        });

        this.getGoodsList();
        break;
      default:
        //综合排序
        this.setData({
          currentSortType: 'default',
          currentSortOrder: 'desc',
          categoryFilter: false
        });
        this.getGoodsList();
    }
  },
  selectCategory: function (event) {
    let currentIndex = event.target.dataset.categoryIndex;
    let filterCategory = this.data.filterCategory;
    let currentCategory = null;
    for (let key in filterCategory) {
      if (key == currentIndex) {
        filterCategory[key].selected = true;
        currentCategory = filterCategory[key];
      } else {
        filterCategory[key].selected = false;
      }
    }
    this.setData({
      filterCategory: filterCategory,
      categoryFilter: false,
      categoryId: currentCategory.id,
      page: 1,
      goodsList: []
    });
    this.getGoodsList();
  },
  onKeywordConfirm(event) {
    this.getSearchResult(event.detail.value);
  },
  addToCart: function (event) {
    var that = this;
    that.getGoodsInfo(event.currentTarget.dataset.goodsid, event);
  },
  //获取商品详情
  getGoodsInfo: function (goodid, event) {
    let that = this;
    util.request(api.GoodsDetail, { id: goodid }).then(function (res) {
      if (res.errno === 0) {

        let _specificationList = res.data.specificationList
        // 如果仅仅存在一种货品，那么商品页面初始化时默认checked
        if (_specificationList.length == 1) {
          if (_specificationList[0].valueList.length == 1) {
            _specificationList[0].valueList[0].checked = true

            // 如果仅仅存在一种货品，那么商品价格应该和货品价格一致
            // 这里检测一下
            let _productPrice = res.data.productList[0].retailPrice;
            let _goodsPrice = res.data.info.retailPrice;
            if (_productPrice != _goodsPrice) {
              console.error('商品数量价格和货品不一致');
            }

            that.setData({
              checkedSpecText: _specificationList[0].valueList[0].value,
              tmpSpecText: '已选择：' + _specificationList[0].valueList[0].value,
            });
          }
        }

        that.setData({
          goods: res.data.info,
          attribute: res.data.attribute,
          issueList: res.data.issue,
          comment: res.data.comment,
          brand: res.data.brand,
          specificationList: res.data.specificationList,
          productList: res.data.productList,
          userHasCollect: res.data.userHasCollect,
          checkedSpecPrice: res.data.info.retailPrice
        });

        if (res.data.userHasCollect == 1) {
          that.setData({
            collectBackImage: that.data.hasCollectImage
          });
        } else {
          that.setData({
            collectBackImage: that.data.noCollectImage
          });
        }

        WxParse.wxParse('goodsDetail', 'html', res.data.info.goodsDesc, that);

        that.getGoodsRelated(goodid, event);
      }
    });

  },
  getGoodsRelated: function (goodid, event) {
    let that = this;
    util.request(api.GoodsRelated, { id: goodid }).then(function (res) {
      if (res.errno === 0) {
        //wz-截取图片格式
        if (res.data.goodsList.length > 0) {
          for (let i = 0; i < res.data.goodsList.length; i++) {
            res.data.goodsList[i].listPicUrl = res.data.goodsList[i].listPicUrl + picUrlSuffix;
          }
        }
        that.setData({
          relatedGoods: res.data.goodsList,
        });
        that.addToCart2(event);
      }
    });

  },
  addToCart2: function (event) {
    var that = this;
    //根据选中的规格，判断是否有对应的sku信息
    let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());
    if (!checkedProductArray || checkedProductArray.length <= 0) {
      //找不到对应的product信息，提示没有库存
      wx.showToast({
        image: '/static/images/icon_error.png',
        title: '没有库存'
      });
      return false;
    }

    let checkedProduct = checkedProductArray[0];
    //验证库存
    if (checkedProduct.goodsNumber <= 0) {
      wx.showToast({
        image: '/static/images/icon_error.png',
        title: '没有库存'
      });
      return false;
    }
    //添加到购物车
    util.request(api.CartAdd, { goodsId: event.currentTarget.dataset.goodsid, number: 1, productId: checkedProduct.id }, "POST")//TODO第三个参数不确定
      .then(function (res) {
        let _res = res;
        if (_res.errno == 0) {
          wx.showToast({
            title: '添加成功'
          });
          //显示bar文本
          // wx.setTabBarBadge({
          //   index: 1,
          //   text: _res.data.toString()
          // });
        } else {
          wx.showToast({
            image: '/static/images/icon_error.png',
            title: _res.errmsg,
            mask: true
          });
        }
      });
  },
  getCheckedProductItem: function (key) {
    return this.data.productList.filter(function (v) {
      if (v.goodsSpecificationIds.toString() == key.toString()) {
        return true;
      } else {
        return false;
      }
    });
  },
  getCheckedSpecKey: function () {
    let checkedValue = this.getCheckedSpecValue().map(function (v) {
      return v.valueId;
    });
    return checkedValue;
  },
  //获取选中的规格信息
  getCheckedSpecValue: function () {
    let checkedValues = [];
    let _specificationList = this.data.specificationList;
    for (let i = 0; i < _specificationList.length; i++) {
      let _checkedObj = {
        name: _specificationList[i].name,
        valueId: 0,
        valueText: ''
      };
      for (let j = 0; j < _specificationList[i].valueList.length; j++) {
        if (_specificationList[i].valueList[j].checked) {
          _checkedObj.valueId = _specificationList[i].valueList[j].id;
          _checkedObj.valueText = _specificationList[i].valueList[j].value;
        }
      }
      checkedValues.push(_checkedObj);
    }

    return checkedValues;
  }
})