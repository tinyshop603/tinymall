package com.attitude.tinymall.wx.web;

import com.alibaba.fastjson.JSONObject;
import com.attitude.tinymall.core.common.SocketEvent;
import com.attitude.tinymall.core.domain.MessageInfo;
import com.attitude.tinymall.core.util.ResponseUtil;
import com.attitude.tinymall.core.util.WxPayEngine;
import com.attitude.tinymall.core.wx.WxPayMpOrderResult;
import com.attitude.tinymall.db.domain.LitemallAddress;
import com.attitude.tinymall.db.domain.LitemallCart;
import com.attitude.tinymall.db.domain.LitemallOrder;
import com.attitude.tinymall.db.domain.LitemallOrderGoods;
import com.attitude.tinymall.db.domain.LitemallProduct;
import com.attitude.tinymall.db.domain.LitemallUser;
import com.attitude.tinymall.db.enums.OrderStatusEnum;
import com.attitude.tinymall.db.enums.PayStatusEnum;
import com.attitude.tinymall.db.enums.PaymentWayEnum;
import com.attitude.tinymall.db.service.LitemallAddressService;
import com.attitude.tinymall.db.service.LitemallCartService;
import com.attitude.tinymall.db.service.LitemallOrderGoodsService;
import com.attitude.tinymall.db.service.LitemallOrderService;
import com.attitude.tinymall.db.service.LitemallProductService;
import com.attitude.tinymall.db.service.LitemallRegionService;
import com.attitude.tinymall.db.service.LitemallUserService;
import com.attitude.tinymall.db.service.LitemallAdminService;
import com.attitude.tinymall.db.util.OrderHandleOption;
import com.attitude.tinymall.db.util.OrderUtil;
import com.attitude.tinymall.wx.annotation.LoginUser;
import com.attitude.tinymall.wx.util.IpUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.service.WxPayService;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.db.domain.*;
import com.attitude.tinymall.core.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 订单设计
 *
 * 订单状态： 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消 201 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消； 301
 * 商家发货，用户未确认； 401 用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收获后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * 目前不支持订单退货和售后服务
 */
@RestController
@RequestMapping("/wx/{storeId}/order")
@Slf4j
public class WxOrderController {

  private final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

  @Autowired
  private PlatformTransactionManager txManager;
  @Autowired
  private LitemallUserService userService;
  @Autowired
  private LitemallOrderService orderService;
  @Autowired
  private LitemallOrderGoodsService orderGoodsService;
  @Autowired
  private LitemallAddressService addressService;
  @Autowired
  private LitemallCartService cartService;
  @Autowired
  private LitemallRegionService regionService;
  @Autowired
  private LitemallProductService productService;
  @Autowired
  private LitemallAdminService adminService;
  @Autowired
  private WxPayService wxPayService;
  @Autowired
  private WxPayEngine wxPayEngine;
  /**
   * 消息传递信息
   */
  private MessageInfo<Map> messageInfo;

  private Socket client;

  @Autowired
  public void setClient(Socket client) {
    this.client = client;
    this.client.connect();
    messageInfo = new MessageInfo<>();
    //发起端
    messageInfo.setSourceClientId("wx-api");
  }

  public WxOrderController() {
  }

  private String detailedAddress(LitemallAddress tinymallAddress) {
    Integer provinceId = tinymallAddress.getProvinceId();
    Integer cityId = tinymallAddress.getCityId();
    Integer areaId = tinymallAddress.getAreaId();
    String provinceName = regionService.findById(provinceId).getName();
    String cityName = regionService.findById(cityId).getName();
    String areaName = regionService.findById(areaId).getName();
    String fullRegion = provinceName + " " + cityName + " " + areaName;
    return fullRegion + " " + tinymallAddress.getAddress();
  }

  /**
   * 订单列表
   *
   * @param userId 用户ID
   * @param showType 订单信息 0， 全部订单 1，待付款 2，待发货 3，待收货 4，待评价
   * @param page 分页页数
   * @param size 分页大小
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功', data: { data: xxx , count: xxx } } 失败则 { errno:
   * XXX, errmsg: XXX }
   */
  @RequestMapping("list")
  public Object list(@LoginUser Integer userId, Integer showType,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "size", defaultValue = "10") Integer size) {
    if (userId == null) {
      return ResponseUtil.fail401();
    }
    if (showType == null) {
      showType = 1;
    }

    List<OrderStatusEnum> orderStatus = Arrays.asList(OrderStatusEnum.values());
    List<LitemallOrder> orderList = orderService.queryByOrderStatus(userId, orderStatus);
    //对上述的集合进行排序
//    orderList
//        .sort(Comparator.comparing(
//            LitemallOrder::getAddTime)
//            .thenComparing(LitemallOrder::getDeleted)
//            .reversed()
//            .thenComparing(LitemallOrder::getId));
    int count = orderService.countByOrderStatus(userId, orderStatus);

    List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
    for (LitemallOrder order : orderList) {
      Map<String, Object> orderVo = new HashMap<>();
      orderVo.put("id", order.getId());
      orderVo.put("orderSn", order.getOrderSn());
      orderVo.put("actualPrice", order.getActualPrice());
      orderVo.put("orderStatusText", order.getOrderStatus().getMessage());
//      orderVo.put("handleOption", OrderUtil.build(order));

      List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
      List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
      for (LitemallOrderGoods orderGoods : orderGoodsList) {
        Map<String, Object> orderGoodsVo = new HashMap<>();
        orderGoodsVo.put("id", orderGoods.getId());
        orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
        orderGoodsVo.put("number", orderGoods.getNumber());
        orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
        orderGoodsVoList.add(orderGoodsVo);
      }
      orderVo.put("goodsList", orderGoodsVoList);

      orderVoList.add(orderVo);
    }
    Map<String, Object> result = new HashMap<>();
    result.put("count", count);

    result.put("data", orderVoList);

    return ResponseUtil.ok(result);
  }

  /**
   * 订单详情
   *
   * @param userId 用户ID
   * @param orderId 订单信息
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功', data: { orderInfo: xxx , orderGoods: xxx } } 失败则 {
   * errno: XXX, errmsg: XXX }
   */
  @GetMapping("detail")
  public Object detail(@LoginUser Integer userId, Integer orderId) {
    if (userId == null) {
      return ResponseUtil.fail401();
    }
    if (orderId == null) {
      return ResponseUtil.fail402();
    }

    // 订单信息
    LitemallOrder order = orderService.findById(orderId);
    if (null == order) {
      return ResponseUtil.fail(403, "订单不存在");
    }
    if (!order.getUserId().equals(userId)) {
      return ResponseUtil.fail(403, "不是当前用户的订单");
    }
    Map<String, Object> orderVo = new HashMap<String, Object>();
    orderVo.put("id", order.getId());
    orderVo.put("orderSn", order.getOrderSn());
    orderVo.put("addTime", LocalDate.now());
    orderVo.put("consignee", order.getConsignee());
    orderVo.put("mobile", order.getMobile());
    orderVo.put("address", order.getAddress());
    orderVo.put("goodsPrice", order.getGoodsPrice());
    orderVo.put("freightPrice", order.getFreightPrice());
    orderVo.put("actualPrice", order.getActualPrice());
    orderVo.put("orderStatusText",order.getOrderStatus().getMessage());

    List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
    List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
    for (LitemallOrderGoods orderGoods : orderGoodsList) {
      Map<String, Object> orderGoodsVo = new HashMap<>();
      orderGoodsVo.put("id", orderGoods.getId());
      orderGoodsVo.put("orderId", orderGoods.getOrderId());
      orderGoodsVo.put("goodsId", orderGoods.getGoodsId());
      orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
      orderGoodsVo.put("number", orderGoods.getNumber());
      orderGoodsVo.put("retailPrice", orderGoods.getRetailPrice());
      orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
      orderGoodsVo.put("goodsSpecificationValues", orderGoods.getGoodsSpecificationValues());
      orderGoodsVoList.add(orderGoodsVo);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("orderInfo", orderVo);
    result.put("orderGoods", orderGoodsVoList);
    return ResponseUtil.ok(result);

  }

  /**
   * 提交订单 1. 根据购物车ID、地址ID、优惠券ID，创建订单表项 2. 购物车清空 3. TODO 优惠券设置已用 4. 商品货品数量减少
   *
   * @param userId 用户ID
   * @param body 订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功', data: { orderId: xxx } } 失败则 { errno: XXX, errmsg:
   * XXX }
   */
  @PostMapping("submit")
  public Object submit(@LoginUser Integer userId, @RequestBody String body,
      @PathVariable("storeId") String appId) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    if (body == null) {
      return ResponseUtil.badArgument();
    }
    if (appId == null) {
      return ResponseUtil.badArgument();
    }
    Integer cartId = JacksonUtil.parseInteger(body, "cartId");
    Integer addressId = JacksonUtil.parseInteger(body, "addressId");
    Integer couponId = JacksonUtil.parseInteger(body, "couponId");
    Integer modeId = JacksonUtil.parseInteger(body, "modeId");
    String remarkText = JacksonUtil.parseString(body, "remarkText");
    if (cartId == null || addressId == null || couponId == null) {
      return ResponseUtil.badArgument();
    }

    // 收货地址
    LitemallAddress checkedAddress = addressService.findById(addressId);

    // 获取可用的优惠券信息
    // 使用优惠券减免的金额
    BigDecimal couponPrice = new BigDecimal(0.00);

    // 货品价格
    List<LitemallCart> checkedGoodsList = null;
    if (cartId.equals(0)) {
      checkedGoodsList = cartService.queryByUidAndChecked(userId);
    } else {
      LitemallCart cart = cartService.findById(cartId);
      checkedGoodsList = new ArrayList<>(1);
      checkedGoodsList.add(cart);
    }
    if (checkedGoodsList.size() == 0) {
      return ResponseUtil.badArgumentValue();
    }
    BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
    for (LitemallCart checkGoods : checkedGoodsList) {
      checkedGoodsPrice = checkedGoodsPrice
          .add(checkGoods.getRetailPrice().multiply(new BigDecimal(checkGoods.getNumber())));
    }

    // 根据订单商品总价计算运费，满88则免运费，否则8元；
    BigDecimal freightPrice = new BigDecimal(0.00);
//    if (checkedGoodsPrice.compareTo(new BigDecimal(88.00)) < 0) {
//      freightPrice = new BigDecimal(8.00);
//    }

    // 可以使用的其他钱，例如用户积分
    BigDecimal integralPrice = new BigDecimal(0.00);

    // 订单费用
    BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
    BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

    // 开启事务管理
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = txManager.getTransaction(def);
    Integer orderId = null;
    LitemallOrder order = null;
    LitemallAdmin admin = adminService.findAdminByOwnerId(appId);
    try {
      // 订单
      order = new LitemallOrder();
      order.setUserId(userId);
      order.setOrderSn(orderService.generateOrderSn(userId));
      order.setAddTime(LocalDateTime.now());
      PaymentWayEnum paymentWay = PaymentWayEnum.CASH_ON_DELIVERY;
      if (modeId == 1) {
        paymentWay = PaymentWayEnum.ONLINE_WECHAT_PAY;
      }
      order.setOrderStatus(OrderStatusEnum.ONGOING);
      order.setPaymentWay(paymentWay);
      order.setConsignee(checkedAddress.getName());
      order.setMobile(checkedAddress.getMobile());
      //提交时不显示省市区，只显示详细地址
//      String detailedAddress = detailedAddress(checkedAddress);
      order.setAddress(checkedAddress.getAddress());
      order.setGoodsPrice(checkedGoodsPrice);
      order.setFreightPrice(freightPrice);
      order.setCouponPrice(couponPrice);
      order.setIntegralPrice(integralPrice);
      order.setOrderPrice(orderTotalPrice);
      order.setActualPrice(actualPrice);
      order.setAdminId(admin.getId());
      order.setRemark(remarkText);
      // 添加订单表项
      orderService.add(order, appId);
      orderId = order.getId();

      for (LitemallCart cartGoods : checkedGoodsList) {
        // 订单商品
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        orderGoods.setOrderId(order.getId());
        orderGoods.setGoodsId(cartGoods.getGoodsId());
        orderGoods.setGoodsSn(cartGoods.getGoodsSn());
        orderGoods.setProductId(cartGoods.getProductId());
        orderGoods.setGoodsName(cartGoods.getGoodsName());
        orderGoods.setPicUrl(cartGoods.getPicUrl());
        orderGoods.setRetailPrice(cartGoods.getRetailPrice());
        orderGoods.setNumber(cartGoods.getNumber());
        orderGoods.setGoodsSpecificationIds(cartGoods.getGoodsSpecificationIds());
        orderGoods.setGoodsSpecificationValues(cartGoods.getGoodsSpecificationValues());

        // 添加订单商品表项
        orderGoodsService.add(orderGoods);
      }

      // 删除购物车里面的商品信息
      cartService.clearGoods(userId);

      // 商品货品数量减少
//      for (LitemallCart checkGoods : checkedGoodsList) {
//        Integer productId = checkGoods.getProductId();
//        LitemallProduct product = productService.findById(productId);
//
//        Integer remainNumber = product.getGoodsNumber() - checkGoods.getNumber();
//        if (remainNumber < 0) {
//          throw new RuntimeException("下单的商品货品数量大于库存量");
//        }
//        product.setGoodsNumber(remainNumber);
//        productService.updateById(product);
//      }
    } catch (Exception ex) {
      txManager.rollback(status);
//      log.error("系统内部错误", ex);
      return ResponseUtil.fail(403, "下单失败");
    }
    txManager.commit(status);

    Map<String, Object> data = new HashMap<>();
    data.put("orderId", orderId);
    if (modeId != 1) {//货到付款在此处触发
      //想办法提醒管理端进行刷新
      messageInfo.setMsgType("order-submit");
      //目标端
      messageInfo.setTargetClientId("admin-api-" + admin.getId());
      LitemallOrderWithGoods orderWithGoods = new LitemallOrderWithGoods();
      orderWithGoods.setOrder(order);
      // 查找此订单的商品信息
      orderWithGoods.setGoods(orderGoodsService.queryByOid(order.getId()));
      Map<String, Object> dataSoc = new HashMap<>(2);
      dataSoc.put("adminId", admin.getId());
      dataSoc.put("orderData", orderWithGoods);
      messageInfo.setDomainData(dataSoc);
      client.emit(SocketEvent.SUBMIT_ORDER, JSONObject.toJSONString(messageInfo));

    }

    return ResponseUtil.ok(data);
  }

  /**
   * 付款订单的预支付会话标识
   *
   * 1. 检测当前订单是否能够付款 2. 微信支付平台返回支付订单ID 3. 设置订单付款状态
   *
   * @param userId 用户ID
   * @param body 订单信息，{ orderId：xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '模拟付款支付成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("prepay")
  public Object prepay(@LoginUser Integer userId, @RequestBody String body,
      HttpServletRequest request, @PathVariable("storeId") String appId) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    if (appId == null) {
      return ResponseUtil.badArgument();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }

    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgumentValue();
    }
    if (!order.getUserId().equals(userId)) {
      return ResponseUtil.badArgumentValue();
    }
    // 检测是否能够支付

    LitemallUser user = userService.findById(userId);
    String openId = user.getWeixinOpenid();

    if (openId == null) {
      return ResponseUtil.fail(403, "订单不能支付");
    }

    LitemallAdmin admin = adminService.findAdminByOwnerId(appId);
    String currTime = wxPayEngine.getCurrTime();
    //8位日期
    String strTime = currTime.substring(8, currTime.length());
    //四位随机数
    String strRandom = wxPayEngine.buildRandom(4) + "";
    //10位序列号,可以自行调整。
    String nonceStr = strTime + strRandom;
    //附加数据，以一定格式保存userId和activityId。原样返回。
    String attach = userId + "#wx#" + order.getId();
    String spBillCreateIp = IpUtil.client(request);
    String describe = "789便利店-订单支付";
    String detail = "订单支付";
    BigDecimal radix = new BigDecimal(100);
    BigDecimal realFee = order.getActualPrice().multiply(radix);
    String money = String.valueOf(realFee.intValue());
    // TODO 测试用例，1分钱
//    String money = "1";
    SortedMap<String, String> packageParams = new TreeMap<String, String>();
    packageParams.put("appid", admin.getOwnerId());
    packageParams.put("attach", attach);//附加数据
    packageParams.put("body", describe);//商品描述
    packageParams.put("detail", detail);
    packageParams.put("mch_id", admin.getMchId().toString());//商户号
    packageParams.put("nonce_str", nonceStr);//随机数
    packageParams.put("notify_url", admin.getNotifyUrl());
    packageParams.put("openid", openId);
    packageParams.put("out_trade_no", order.getOrderSn());//商户订单号
    packageParams.put("spbill_create_ip", spBillCreateIp);//订单生成的机器 IP
    packageParams.put("total_fee", money);//总金额
    packageParams.put("trade_type", "JSAPI");

    String sign = wxPayEngine.createSign(packageParams, admin.getMchKey());
    String xml = "<xml>" +
        "<appid>" + appId + "</appid>" +
        "<attach>" + attach + "</attach>" +
        "<body><![CDATA[" + describe + "]]></body>" +
        "<detail><![CDATA[" + detail + "]]></detail>" +
        "<mch_id>" + admin.getMchId().toString() + "</mch_id>" +
        "<nonce_str>" + nonceStr + "</nonce_str>" +
        "<sign>" + sign + "</sign>" +
        "<notify_url>" + admin.getNotifyUrl() + "</notify_url>" +
        "<openid>" + openId + "</openid>" +
        "<out_trade_no>" + order.getOrderSn() + "</out_trade_no>" +
        "<spbill_create_ip>" + spBillCreateIp + "</spbill_create_ip>" +
        "<total_fee>" + money + "</total_fee>" +
        "<trade_type>JSAPI</trade_type>" +
        "</xml>";
    String prepay_id = "";
    try {
      prepay_id = wxPayEngine.getPayNo(PAY_URL, xml);
      if (prepay_id.equals("")) {
        //错误提示
//        log.error("统一支付接口获取预支付订单出错");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseUtil.fail(403, "订单不能支付");
    }
    SortedMap<String, String> finalpackage = new TreeMap<String, String>();
    String timestamp = wxPayEngine.getTimeStamp();
    String packages = "prepay_id=" + prepay_id;
    finalpackage.put("appId", appId);
    finalpackage.put("nonceStr", nonceStr);
    finalpackage.put("package", packages);
    finalpackage.put("signType", "MD5");
    finalpackage.put("timeStamp", timestamp);
    String finalsign = wxPayEngine.createSign(finalpackage, admin.getMchKey());

    WxPayMpOrderResult result = new WxPayMpOrderResult();
    result.setNonceStr(nonceStr);
    result.setPackage_(packages);
    result.setPaySign(finalsign);
    result.setSignType("MD5");
    result.setTimeStamp(timestamp);
    orderService.updateById(order);
    return ResponseUtil.ok(result);
  }

  /**
   * 付款成功回调接口 1. 检测当前订单是否是付款状态 2. 设置订单付款成功状态相关信息 3. 响应微信支付平台
   *
   * @return 订单操作结果 成功则 WxPayNotifyResponse.success的XML内容 失败则 WxPayNotifyResponse.fail的XML内容
   *
   * 注意，这里pay-notify是示例地址，开发者应该设立一个隐蔽的回调地址
   */
  @PostMapping("pay-notify")
  public Object payNotify(HttpServletRequest request, HttpServletResponse response,
      @PathVariable("storeId") String appId) {
    try {
      System.out.println("===回调开始===");
      String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
      WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult);
      System.out.println("result："+result);
      String orderSn = result.getOutTradeNo();
      String payId = result.getTransactionId();
      // 分转化成元
      String totalFee = BaseWxPayResult.feeToYuan(result.getTotalFee());

      LitemallOrder order = orderService.findBySn(orderSn);
      if (order == null) {
        throw new Exception("订单不存在 sn=" + orderSn);
      }

      // 检查这个订单是否已经处理过
      if (order.getPayStatus() == PayStatusEnum.PAID && order.getPayId() != null) {
        return WxPayNotifyResponse.success("处理成功!");
      }

      // 检查支付订单金额
      // TODO 这里1分钱需要改成实际订单金额
//      if (!totalFee.equals("0.01")) {
      BigDecimal totalFreePrice = new BigDecimal(totalFee);
      if (!totalFreePrice.equals(order.getActualPrice())) {
        throw new Exception("支付金额不符合 totalFee=" + totalFee);
      }

      order.setPayId(payId);
      order.setPayTime(LocalDateTime.now());
      order.setPayStatus(PayStatusEnum.PAID);
      orderService.updateById(order);

      //想办法提醒管理端进行刷新
      LitemallAdmin admin = adminService.findAdminByOwnerId(appId);
      //目标端
      messageInfo.setTargetClientId("admin-api-" + admin.getId());
      messageInfo.setMsgType("order-submit");
      LitemallOrderWithGoods orderWithGoods = new LitemallOrderWithGoods();
      orderWithGoods.setOrder(order);
      // 查找此订单的商品信息
      orderWithGoods.setGoods(orderGoodsService.queryByOid(order.getId()));
      Map<String, Object> dataSoc = new HashMap<>(2);
      dataSoc.put("adminId", admin.getId());
      dataSoc.put("orderData", orderWithGoods);
      messageInfo.setDomainData(dataSoc);
      client.emit(SocketEvent.SUBMIT_ORDER, JSONObject.toJSONString(messageInfo));

      return WxPayNotifyResponse.success("处理成功!");
    } catch (Exception e) {
//      log.error("微信回调结果异常,异常原因 " + e.getMessage());
      return WxPayNotifyResponse.fail(e.getMessage());
    }
  }

  /**
   * 取消订单 1. 检测当前订单是否能够取消 2. 设置订单取消状态 3. 商品货品数量增加
   *
   * @param userId 用户ID
   * @param body 订单信息，{ orderId：xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("cancel")
  public Object cancel(@LoginUser Integer userId, @PathVariable("storeId") String appId,
      @RequestBody String body) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }
    LitemallAdmin admin = adminService.findAdminByOwnerId(appId);
    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgumentValue();
    }
    if (!order.getUserId().equals(userId)) {
      return ResponseUtil.badArgumentValue();
    }

    //TODO 检测是否能够取消

    // 开启事务管理
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = txManager.getTransaction(def);
    //判断是否为未支付状态取消，如果是，后台不进行实时更新。
    boolean isNotPayCancel = false;
    if (order.getPayStatus() == PayStatusEnum.UNPAID) {
      isNotPayCancel = true;
    }
    try {
      // 设置订单已取消状态
      if (isNotPayCancel){
        order.setOrderStatus(OrderStatusEnum.CUSTOMER_CANCEL);
      }
      order.setEndTime(LocalDateTime.now());
      orderService.update(order);

      // 商品货品数量增加
      List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
      for (LitemallOrderGoods orderGoods : orderGoodsList) {
        Integer productId = orderGoods.getProductId();
        LitemallProduct product = productService.findById(productId);
        Integer number = product.getGoodsNumber() + orderGoods.getNumber();
        product.setGoodsNumber(number);
        productService.updateById(product);
      }
    } catch (Exception ex) {
      txManager.rollback(status);
//      log.error("系统内部错误", ex);
      return ResponseUtil.fail(403, "订单取消失败");
    }
    txManager.commit(status);
    //想办法提醒管理端进行刷新,在线支付未支付订单取消
    if (!isNotPayCancel) {
      messageInfo.setMsgType("order-cancel");
      //目标端
      messageInfo.setTargetClientId("admin-api-" + admin.getId());
      Map<String, Object> socketData = new HashMap<>(2);
      socketData.put("orderData", order);
      socketData.put("adminId", admin.getId());
      messageInfo.setDomainData(socketData);
      client.emit(SocketEvent.CANCEL_ORDER, JacksonUtil.stringifyObject(messageInfo));
    }

    return ResponseUtil.ok();
  }

  /**
   * 订单申请退款 1. 检测当前订单是否能够退款 2. 设置订单申请退款状态
   *
   * @param userId 用户ID
   * @param body 订单信息，{ orderId：xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("refund")
  public Object refund(@LoginUser Integer userId, @PathVariable("storeId") String appId,
      @RequestBody String body) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }
    LitemallAdmin admin = adminService.findAdminByOwnerId(appId);
    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgument();
    }
    if (!order.getUserId().equals(userId)) {
      return ResponseUtil.badArgumentValue();
    }

    //TODO 检查是否能够申请退款

    // 设置订单申请退款状态
    order.setOrderStatus(OrderStatusEnum.MERCHANT_REFUNDING);
    orderService.update(order);
    //想办法提醒管理端进行刷新
    messageInfo.setMsgType("order-refund");
    //目标端
    messageInfo.setTargetClientId("admin-api-" + admin.getId());
    Map<String, Object> socketData = new HashMap<>(2);
    socketData.put("orderData", order);
    socketData.put("adminId", admin.getId());
    messageInfo.setDomainData(socketData);
    client.emit(SocketEvent.REFUND_ORDER, JacksonUtil.stringifyObject(messageInfo));

    return ResponseUtil.ok();
  }

  /**
   * 确认收货 1. 检测当前订单是否能够确认订单 2. 设置订单确认状态
   *
   * @param userId 用户ID
   * @param body 订单信息，{ orderId：xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("confirm")
  public Object confirm(@LoginUser Integer userId, @PathVariable("storeId") String appId,
      @RequestBody String body) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }

    LitemallAdmin admin = adminService.findAdminByOwnerId(appId);
    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgument();
    }
    if (!order.getUserId().equals(userId)) {
      return ResponseUtil.badArgumentValue();
    }

    order.setOrderStatus(OrderStatusEnum.COMPLETE);
    order.setConfirmTime(LocalDateTime.now());
    orderService.update(order);
    //想办法提醒管理端进行刷新
    messageInfo.setMsgType("order-confirm");
    //目标端
    messageInfo.setTargetClientId("admin-api-" + admin.getId());
    Map<String, Object> socketData = new HashMap<>(2);
    socketData.put("orderData", order);
    socketData.put("adminId", admin.getId());
    messageInfo.setDomainData(socketData);
    client.emit(SocketEvent.CONFIRM_ORDER, JacksonUtil.stringifyObject(messageInfo));
    return ResponseUtil.ok();
  }

  /**
   * 删除订单 1. 检测当前订单是否删除 2. 设置订单删除状态
   *
   * @param userId 用户ID
   * @param body 订单信息，{ orderId：xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("delete")
  public Object delete(@LoginUser Integer userId, @RequestBody String body) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }

    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgument();
    }
    if (!order.getUserId().equals(userId)) {
      return ResponseUtil.badArgumentValue();
    }

    //TODO 检查订单是否能够删除

    // 订单order_status没有字段用于标识删除
    // 而是存在专门的delete字段表示是否删除
    orderService.deleteById(orderId);

    return ResponseUtil.ok();
  }

  /**
   * 可以评价的订单商品信息
   *
   * @param userId 用户ID
   * @param orderId 订单ID
   * @param goodsId 商品ID
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功', data: xxx } 失败则 { errno: XXX, errmsg: XXX }
   */
  @GetMapping("comment")
  public Object comment(@LoginUser Integer userId, Integer orderId, Integer goodsId) {
    if (userId == null) {
      return ResponseUtil.unlogin();
    }
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }

    List<LitemallOrderGoods> orderGoodsList = orderGoodsService.findByOidAndGid(orderId, goodsId);
    int size = orderGoodsList.size();

    Assert.state(size < 2, "存在多个符合条件的订单商品");

    if (size == 0) {
      return ResponseUtil.badArgumentValue();
    }

    LitemallOrderGoods orderGoods = orderGoodsList.get(0);
    return ResponseUtil.ok(orderGoods);
  }

  @GetMapping("socekt/{event}")
  public Object soceketTest(@PathVariable String event, @RequestParam String adminId) {
    MessageInfo<Map> messageInfo = new MessageInfo();
    messageInfo.setMsgType("order-confirm");
    //目标端
    messageInfo.setTargetClientId("admin-api-" + adminId);
    Map<String, Object> socketData = new HashMap<>(2);
    socketData.put("orderData", orderService.findById(40));
    socketData.put("adminId", adminId);
    messageInfo.setDomainData(socketData);
    client.emit(event, JacksonUtil.stringifyObject(messageInfo));
    return ResponseUtil.ok();
  }
}
