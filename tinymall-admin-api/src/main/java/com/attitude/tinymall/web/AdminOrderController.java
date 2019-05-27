package com.attitude.tinymall.web;

import com.attitude.tinymall.ao.OrderStatusAO;
import com.attitude.tinymall.vo.OrderVO;
import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderGoods;
import com.attitude.tinymall.domain.LitemallOrderWithGoods;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.enums.PayStatusEnum;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderGoodsService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.service.LitemallProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.util.JacksonUtil;
import com.attitude.tinymall.util.OrderUtil;
import com.attitude.tinymall.util.ResponseUtil;
import com.attitude.tinymall.util.WxPayEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.text.DecimalFormat;

@RestController
@RequestMapping("/admin/{userName}/order")
@Slf4j
public class AdminOrderController {

  private final Log logger = LogFactory.getLog(AdminOrderController.class);

  private final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

  private final String MSGTMPL_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send";

  private final String ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

  private final String MSG_TMPL = "zY8NQA7bTPtcE6vDDhabeAUWi-G3ZfnLbPVWnZP4D4U";

  @Autowired
  private PlatformTransactionManager txManager;

  @Autowired
  private LitemallOrderGoodsService orderGoodsService;
  @Autowired
  private LitemallOrderService orderService;
  @Autowired
  private LitemallProductService productService;
  @Autowired
  private WxPayEngine wxPayEngine;
  @Autowired
  private LitemallAdminService adminService;
  @Autowired
  private LitemallDeliveryDetailService detailService;


  @GetMapping("/list")
  public Object list(@LoginAdmin Integer adminId,
      Integer userId, String orderSn,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      String sort, String order) {
    if (adminId == null) {
      return ResponseUtil.fail401();
    }
    List<LitemallOrder> orderList = orderService
        .listAdminOrdersByAdminId(adminId, userId, orderSn, page, limit, sort, order);
    List<LitemallOrderGoods> orderGoodsList = orderGoodsService
        .listOrderWithGoodsByOrder(orderList);

    List<LitemallOrderWithGoods> orderWithGoodsList = new ArrayList<LitemallOrderWithGoods>();
    for (LitemallOrder curOrder : orderList) {
      LitemallOrderWithGoods itemallOrderWithGoods = new LitemallOrderWithGoods();
      List<LitemallOrderGoods> curOrderGoodsList = new ArrayList<LitemallOrderGoods>();
      LitemallDeliveryDetail deliveryDetail = detailService
          .getDeliveryDetailByDeliveryId(curOrder.getDeliveryId());
      itemallOrderWithGoods.setDeliveryDetail(deliveryDetail);
      for (LitemallOrderGoods curOrderGoods : orderGoodsList) {
        if (curOrderGoods.getOrderId().equals(curOrder.getId())) {
          curOrderGoodsList.add(curOrderGoods);
        }
      }
      itemallOrderWithGoods.setOrder(curOrder);
      itemallOrderWithGoods.setGoods(curOrderGoodsList);
      orderWithGoodsList.add(itemallOrderWithGoods);
    }
    int total = orderService.countAdminOrdersByAdminId(adminId, userId, orderSn);

    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("items", orderWithGoodsList);

    return ResponseUtil.ok(data);
  }

  @GetMapping("/wxlist")
  public Object wxlist(@LoginAdmin Integer adminId, Integer showType,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      String sort, String order) {
    if (adminId == null) {
      return ResponseUtil.fail401();
    }
    if (showType == null) {
      showType = 0;
    }
    List<OrderStatusEnum> orderStatus = Arrays.asList(OrderStatusEnum.values());
    List<LitemallOrder> orderList = orderService
        .listAdminOrdersByStatus(adminId, orderStatus, page, limit, sort, order);
    //对数据进行处理
    for (LitemallOrder curOrder : orderList) {
      //地址长度检查
      if (curOrder.getAddress().length() > 18) {
        curOrder.setAddress(curOrder.getAddress().substring(0, 18));
      }
      //姓名长度检查
      if (curOrder.getConsignee().length() > 8) {
        curOrder.setConsignee(curOrder.getConsignee().substring(0, 8));
      }
    }

    return ResponseUtil.ok(orderList);
  }

  @GetMapping("/wxdetail")
  public Object wxdetail(@LoginAdmin Integer adminId, Integer orderId) {
    if (adminId == null) {
      return ResponseUtil.fail401();
    }
    if (orderId == null) {
      return ResponseUtil.fail401();
    }
    List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
    return ResponseUtil.ok(orderGoodsList);
  }

  /*
   * 目前的逻辑不支持管理员创建
   */
  @PostMapping("/create")
  public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallOrder order) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    return ResponseUtil.unsupport();
  }

  @GetMapping("/read")
  public Object read(@LoginAdmin Integer adminId, Integer id) {
    if (adminId == null) {
      return ResponseUtil.fail401();
    }

    LitemallOrder order = orderService.findById(id);
    return ResponseUtil.ok(order);
  }

  @GetMapping("/detail")
  public Object getOrderWithdeliveryMsgByOrderId(@LoginAdmin Integer adminId, Integer id) {
    if (adminId == null) {
      return ResponseUtil.fail401();
    }

    LitemallOrder order = orderService.findById(id);
    LitemallDeliveryDetail deliveryDetail = detailService
        .getDeliveryDetailByDeliveryId(order.getDeliveryId());
    OrderVO orderVO = new OrderVO();
    orderVO.setOrder(order);
    orderVO.setDeliveryDetail(deliveryDetail);
    return ResponseUtil.ok(orderVO);
  }


  /*
   * 目前仅仅支持管理员设置发货相关的信息
   */
  @PostMapping("/update")
  public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallOrder order) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    Integer orderId = order.getId();
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }

    LitemallOrder tinymallOrder = orderService.findById(orderId);
    if (tinymallOrder == null) {
      return ResponseUtil.badArgumentValue();
    }

    if (tinymallOrder.getPayStatus() == PayStatusEnum.PAID
        || tinymallOrder.getOrderStatus() == OrderStatusEnum.MERCHANT_SHIP) {
      LitemallOrder newOrder = new LitemallOrder();
      newOrder.setId(orderId);
      newOrder.setShipChannel(order.getShipChannel());
      newOrder.setShipSn(order.getOrderSn());
      newOrder.setShipStartTime(order.getShipStartTime());
      newOrder.setShipEndTime(order.getShipEndTime());
      newOrder.setOrderStatus(OrderStatusEnum.MERCHANT_SHIP);
      orderService.update(newOrder);
    } else {
      return ResponseUtil.badArgumentValue();
    }

    tinymallOrder = orderService.findById(orderId);
    return ResponseUtil.ok(tinymallOrder);
  }

  /**
   * 更新订单的状态信息
   */
  @PostMapping("/update/status/")
  public Object updateOrderStatues(@LoginAdmin Integer adminId, @RequestBody OrderStatusAO ao) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    Integer orderId = ao.getOrderId();
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }

    LitemallOrder currentOrder = orderService.findById(orderId);
    if (currentOrder == null) {
      return ResponseUtil.badArgumentValue();
    }
    OrderStatusEnum targetStatus = ao.getOrderStatus();
    switch (targetStatus) {
      case MERCHANT_ACCEPT:
        // 商家能接单的情况
        if (!Arrays.asList(OrderStatusEnum.MERCHANT_ACCEPT, OrderStatusEnum.CUSTOMER_PAIED)
            .contains(currentOrder.getOrderStatus())) {
          return ResponseUtil
              .fail(-1, "当前订单无法接单, 订单状态为: " + currentOrder.getOrderStatus().getMessage());
        }
        break;
      case MERCHANT_CANCEL:
        // 商家能取消的情况
        if (!Arrays.asList(OrderStatusEnum.PENDING_PAYMENT,
            OrderStatusEnum.CUSTOMER_PAIED,
            OrderStatusEnum.MERCHANT_ACCEPT,
            OrderStatusEnum.MERCHANT_SHIP
        ).contains(currentOrder.getOrderStatus())) {
          return ResponseUtil
              .fail(-1, "当前订单无法接单, 订单状态为: " + currentOrder.getOrderStatus().getMessage());
        }
        break;
      default:
        break;
    }

    // 设置订单已取消状态
    currentOrder.setOrderStatus(ao.getOrderStatus());
    orderService.updateById(currentOrder);
    return ResponseUtil.ok(currentOrder);
  }


  @PostMapping("/delete")
  public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallOrder order) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    return ResponseUtil.unsupport();
  }


  /**
   * 订单退款确认 1. 检测当前订单是否能够退款确认 2. 设置订单退款确认状态 3. 订单商品恢复
   *
   * @param adminId 管理员ID
   * @param body 订单信息，{ orderId：xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("refundConfirm")
  public Object refundConfirm(@LoginAdmin Integer adminId, @RequestBody String body) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    if (orderId == null) {
      return ResponseUtil.badArgument();
    }
//    return refundConf(adminId, orderId);
//  }

    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgument();
    }
    LitemallAdmin admin = adminService.findAllColunmById(adminId);
    if (admin == null) {
      return ResponseUtil.badArgument();
    }
    if (!order.getAdminId().equals(adminId)) {
      return ResponseUtil.badArgumentValue();
    }
    if (order.getOrderStatus() != OrderStatusEnum.MERCHANT_REFUNDING) {
      return ResponseUtil.fail(403, "订单不能退款成功");
    }
    String currTime = wxPayEngine.getCurrTime();
    String strTime = currTime.substring(8, currTime.length());
    String strRandom = wxPayEngine.buildRandom(4) + "";
    String nonceStr = strTime + strRandom;
    String outRefundNo = "wx@re@" + wxPayEngine.getTimeStamp();
    String outTradeNo = "";
    DecimalFormat df = new DecimalFormat("######0");
    BigDecimal radix = new BigDecimal(100);
    BigDecimal realFee = order.getActualPrice().multiply(radix);
    Integer fee = realFee.intValue();
    //TODO 测试用例,上线改成实际数值
//     Integer fee = 1;
    SortedMap<String, String> packageParams = new TreeMap<String, String>();
    packageParams.put("appid", admin.getOwnerId());
    packageParams.put("mch_id", admin.getMchId().toString());//微信支付分配的商户号
    packageParams.put("nonce_str", nonceStr);//随机字符串，不长于32位
    packageParams.put("op_user_id", admin.getId().toString());//操作员帐号, 默认为商户号
    //out_refund_no只能含有数字、字母和字符_-|*@
    packageParams.put("out_refund_no", outRefundNo);//商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
    packageParams.put("out_trade_no", order.getOrderSn());//商户侧传给微信的订单号32位
    packageParams.put("refund_fee", fee.toString());
    packageParams.put("total_fee", fee.toString());
    packageParams.put("transaction_id", order.getPayId());//微信生成的订单号，在支付通知中有返回
    String sign = wxPayEngine.createSign(packageParams, admin.getMchKey());

    String xmlParam = "<xml>" +
        "<appid>" + admin.getOwnerId() + "</appid>" +
        "<mch_id>" + admin.getMchId().toString() + "</mch_id>" +
        "<nonce_str>" + nonceStr + "</nonce_str>" +
        "<op_user_id>" + admin.getId().toString() + "</op_user_id>" +
        "<out_refund_no>" + outRefundNo + "</out_refund_no>" +
        "<out_trade_no>" + order.getOrderSn() + "</out_trade_no>" +
        "<refund_fee>" + fee + "</refund_fee>" +
        "<total_fee>" + fee + "</total_fee>" +
        "<transaction_id>" + order.getPayId() + "</transaction_id>" +
        "<sign>" + sign + "</sign>" +
        "</xml>";
    String resultStr = wxPayEngine.post(REFUND_URL, xmlParam, admin.getMchId().toString());
    Map<String, Object> result = new HashMap<String, Object>();
    //解析结果
    try {
      Map map = wxPayEngine.doXMLParse(resultStr);
      String returnCode = map.get("return_code").toString();
      if (returnCode.equals("SUCCESS")) {
        String resultCode = map.get("result_code").toString();
        if (resultCode.equals("SUCCESS")) {
          logger.info("退款成功");
        } else {
          logger.info("退款失败：" + map.get("return_msg").toString());
          return ResponseUtil.fail(403, "订单退款失败");
        }
      } else {
        logger.info("退款失败：" + map.get("return_msg").toString());
        return ResponseUtil.fail(403, "订单退款失败");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseUtil.fail(403, "订单退款失败");
    }
    // 开启事务管理
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = txManager.getTransaction(def);
    try {
      // 退款完成
      order.setOrderStatus(OrderStatusEnum.COMPLETE);
      order.setPayStatus(PayStatusEnum.REFUNDED);
      order.setEndTime(LocalDateTime.now());
      orderService.update(order);

      // 商品货品数量增加
//      List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
//      for (LitemallOrderGoods orderGoods : orderGoodsList) {
//        Integer productId = orderGoods.getProductId();
//        LitemallProduct product = productService.findById(productId);
//        Integer number = product.getGoodsNumber() + orderGoods.getNumber();
//        product.setGoodsNumber(number);
//        productService.updateById(product);
//      }
    } catch (Exception ex) {
      txManager.rollback(status);
      logger.error("系统内部错误", ex);
      return ResponseUtil.fail(403, "订单退款失败");
    }
    txManager.commit(status);

    return ResponseUtil.ok(order);
  }

  /**
   * 发货 1. 检测当前订单是否能够发货 2. 设置订单发货状态
   *
   * @param adminId 管理员ID
   * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
   * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
   */
  @PostMapping("ship")
  public Object ship(@LoginAdmin Integer adminId, @RequestBody String body) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    Integer orderId = JacksonUtil.parseInteger(body, "orderId");
    String shipSn = JacksonUtil.parseString(body, "shipSn");
    String shipChannel = JacksonUtil.parseString(body, "shipChannel");
    if (orderId == null || shipSn == null || shipChannel == null) {
      return ResponseUtil.badArgument();
    }

    LitemallOrder order = orderService.findById(orderId);
    if (order == null) {
      return ResponseUtil.badArgument();
    }
    if (!order.getUserId().equals(adminId)) {
      return ResponseUtil.badArgumentValue();
    }

    // 如果订单不是已付款状态，则不能发货
    if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
      return ResponseUtil.fail(403, "订单不能确认收货");
    }

    order.setOrderStatus(OrderStatusEnum.MERCHANT_SHIP);
    order.setShipSn(shipSn);
    order.setShipChannel(shipChannel);
    order.setShipStartTime(LocalDateTime.now());
    orderService.update(order);

    return ResponseUtil.ok();
  }

}
