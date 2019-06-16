package com.attitude.tinymall.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.domain.*;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.enums.PayStatusEnum;
import com.attitude.tinymall.service.*;
import com.attitude.tinymall.util.*;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.text.DecimalFormat;
import com.attitude.tinymall.enums.OrderStatusEnum.*;

import static com.attitude.tinymall.enums.OrderStatusEnum.ONGOING;
import static com.attitude.tinymall.enums.OrderStatusEnum.SYSTEM_AUTO_CANCEL;
import static com.attitude.tinymall.enums.OrderStatusEnum.SYSTEM_AUTO_COMPLETE;

@RestController
@RequestMapping("/admin/{userName}/order")
@Slf4j
public class AdminOrderController {

  private final Log logger = LogFactory.getLog(AdminOrderController.class);


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
  private LitemallUserService userService;

//  private LitemallUser

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
            OrderStatusEnum.CUSTOMER_PAIED
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
    if (Arrays.asList(OrderStatusEnum.CUSTOMER_PAIED, OrderStatusEnum.MERCHANT_REFUNDING)
        .contains(order.getOrderStatus()) && PayStatusEnum.PAID == order.getPayStatus()) {
      return ResponseUtil.fail(403, "订单不能退款成功");
    }
    boolean refundSuccess = orderService.refundOrder(orderId);
    if (!refundSuccess) {
      return ResponseUtil.fail(403, "退款失败, 请联系工程师");
    }
    return ResponseUtil.ok();
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

  /**
   * [服务通知] 调用接口
   *
   * @param adminId 商户ID
   * @param orderId 发送短信订单ID 模板数据： 1.订单号    orderSn 2.订单状态  调用者提供 3.订单内容  orderDetail 4.更新时间 curTime
   * 5.备注      remark
   */
  public void sendTmplMsg(Integer adminId, Integer orderId, String orderStatus) {
    // 获取接口调用凭证
    String access_token = getAccessToken(adminId);

    LitemallOrder tinymallOrder = orderService.findById(orderId);
    // 获取用户openId
    Integer userId = tinymallOrder.getUserId();
    LitemallUser user = userService.findById(userId);
    String touser = user.getWeixinOpenid();
    // 获取支付prepay_id
    String form_id = tinymallOrder.getTransactionId();//tinymallOrder.getPayId();
    //获取购买详情
    String orderDetail = getOrderDetailStr(orderId);
    // 填入模板数据
    JSONObject data = new JSONObject();
    JSONObject keyword1 = new JSONObject();
    JSONObject keyword2 = new JSONObject();
    JSONObject keyword3 = new JSONObject();
    JSONObject keyword4 = new JSONObject();
    JSONObject keyword5 = new JSONObject();
    keyword1.put("value", tinymallOrder.getOrderSn());
    keyword2.put("value", orderStatus);
    keyword3.put("value", orderDetail);
    keyword4.put("value", LocalDate.now());
    keyword5.put("value", tinymallOrder.getRemark());
//        data.put("keyword1", tinymallOrder.getOrderSn());
//        data.put("keyword2", orderStatus);
//        data.put("keyword3", (orderDetail==null)? "test":orderDetail);
//        data.put("keyword4", LocalDate.now());
//        data.put("keyword5",(tinymallOrder.getRemark()==null)? "test":tinymallOrder.getRemark());
    data.put("keyword1", keyword1);
    data.put("keyword2", keyword2);
    data.put("keyword3", keyword3);
    data.put("keyword4", keyword4);
    data.put("keyword5", keyword5);
    //调用通知接口
    JSONObject params = new JSONObject();
    params.put("touser", touser); // 必填 接收者openid
    params.put("template_id", MSG_TMPL); // 必填 下发模板消息id
    params.put("form_id", form_id);  // 必填 表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
    //    params.put("page", page);     // 选填 模板跳转功能
    params.put("data", data);     // 选填 模板内容
    //    params.put("emphasis_keyword", emphasis_keyword);  // 选填 放大关键词
    try {
      //      String post = HttpUtil .post(MSGTMPL_URL+"?access_token=" + access_token, params, 3000);
      String test = HttpClientUtil
          .sendPost(MSGTMPL_URL + "?access_token=" + access_token, params.toString());
      Map result = JSON.parseObject(test, Map.class);
      log.info(test);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
  }

  /**
   * 获取[服务通知]接口调用凭证
   */
  private String getAccessToken(Integer adminId) {
    LitemallAdmin admin = adminService.findAllColunmById(adminId);
    String param = "?grant_type=client_credential&appid=" + admin.getOwnerId() + "&secret=" + admin
        .getAppSecret();
    try {
      HttpClientUtil.HttpClientResult httpClientResult = HttpClientUtil
          .doGet(ACCESSTOKEN_URL + param);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      return result.get("access_token").toString();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return null;
  }

  private String getOrderDetailStr(Integer orderId) {
    String orderDetailStr = "";
    StringBuilder orderDetailSb = new StringBuilder();
    List<LitemallOrderGoods> goodsList = orderGoodsService.queryByOid(orderId);
    if (goodsList.isEmpty()) {
      log.error("获取订单" + orderId + "详情失败");
    } else {

      for (int i = 0; i < goodsList.size(); i++) {
        LitemallOrderGoods goods = goodsList.get(i);
        orderDetailSb.append(goods.getGoodsName());
        orderDetailSb.append(" (");
        orderDetailSb.append(goods.getNumber());
        orderDetailSb.append("份) ");
      }
    }
    if (orderDetailStr.length() > 40) {
      orderDetailStr = orderDetailSb.toString().substring(0, 40) + "...";
    } else {
      orderDetailStr = orderDetailSb.toString();
    }
    return orderDetailStr;
  }


}
