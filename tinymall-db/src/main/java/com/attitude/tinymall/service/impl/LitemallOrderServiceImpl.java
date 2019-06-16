package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallAdminMapper;
import com.attitude.tinymall.dao.LitemallOrderMapper;
import com.attitude.tinymall.dao.manual.LitemallOrderManualMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderExample;
import com.attitude.tinymall.domain.LitemallOrderGoods;
import com.attitude.tinymall.domain.LitemallOrderWithGoods;
import com.attitude.tinymall.domain.LitemallProduct;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.enums.PayStatusEnum;
import com.attitude.tinymall.enums.TPDStatusEnum;
import com.attitude.tinymall.service.LitemallOrderGoodsService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.service.LitemallProductService;
import com.attitude.tinymall.util.ResponseUtil;
import com.attitude.tinymall.util.WxPayEngine;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.models.auth.In;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class LitemallOrderServiceImpl implements LitemallOrderService {

  @Autowired
  private LitemallOrderMapper orderMapper;
  @Autowired
  private LitemallAdminMapper adminMapper;
  @Autowired
  private LitemallAdminServiceImpl adminService;
  @Autowired
  private LitemallOrderManualMapper orderManualMapper;
  @Autowired
  private LitemallProductService productService;
  @Autowired
  private LitemallOrderGoodsService orderGoodsService;
  @Autowired
  private WxPayEngine wxPayEngine;


  @Override
  public int add(LitemallOrder order, String appId) {
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    if (null != litemallAdmin) {
      // 关联外键
      order.setAdminId(litemallAdmin.getId());
    }
    return orderMapper.insertSelective(order);
  }

  @Override
  public List<LitemallOrder> query(Integer userId, String appId) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
        .andAdminIdEqualTo(litemallAdmin.getId());
    return orderMapper.selectByExample(example);
  }

  @Override
  public int count(Integer userId) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public LitemallOrder findById(Integer orderId) {
    return orderMapper.selectByPrimaryKey(orderId);
  }

  @Override
  public String getRandomNum(Integer num) {
    String base = "0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < num; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  @Override
  public LitemallOrder queryByOrderSn(Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }

  @Override
  public int countByOrderSn(Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public List<LitemallOrder> queryByOrderStatus(Integer userId, List<OrderStatusEnum> orderStatus) {
    LitemallOrderExample example = new LitemallOrderExample();
    OrderStatusEnum currentOrderStatus = orderStatus.get(0);
    //未完成 按结束时间排序
    if (currentOrderStatus == OrderStatusEnum.ONGOING) {
      example.orderBy(LitemallOrder.Column.endTime.desc());
    } else if (currentOrderStatus == OrderStatusEnum.COMPLETE) {//已完成 按完成时间排序
      example.orderBy(LitemallOrder.Column.confirmTime.desc());
    } else {//其他，按添加时间排序
      example.orderBy(LitemallOrder.Column.addTime.desc());
    }

    LitemallOrderExample.Criteria criteria = example.or();
    criteria.andUserIdEqualTo(userId);
    if (orderStatus != null) {
      criteria.andOrderStatusIn(orderStatus);
    }
    criteria.andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }

  @Override
  public int countByOrderStatus(Integer userId, List<OrderStatusEnum> orderStatus) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.or();
    criteria.andUserIdEqualTo(userId);
    if (orderStatus != null) {
      criteria.andOrderStatusIn(orderStatus);
    }
    criteria.andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public int update(LitemallOrder order) {
    return orderMapper.updateByPrimaryKeySelective(order);
  }

  @Override
  public List<LitemallOrder> listAdminOrdersByAdminId(Integer adminId, Integer userId,
      String orderSn, Integer page, Integer size, String sort, String order) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.orderBy("add_time DESC");
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);
    criteria.andOrderStatusNotEqualTo(OrderStatusEnum.PENDING_PAYMENT);
    return orderMapper.selectByExample(example);
  }

  @Override
  public List<LitemallOrder> listAdminOrdersByStatus(Integer adminId,
      List<OrderStatusEnum> orderStatus, Integer page, Integer size, String sort, String order) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.orderBy("add_time DESC");
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    criteria.andOrderStatusIn(orderStatus);
    Page<Object> objects = PageHelper.startPage(page, size);
    return orderMapper.selectByExample(example);
  }

  @Override
  public int countAdminOrdersByAdminId(Integer adminId, Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andOrderStatusNotEqualTo(OrderStatusEnum.PENDING_PAYMENT);
    criteria.andDeletedEqualTo(false);

    return (int) orderMapper.countByExample(example);
  }

  @Override
  public List<LitemallOrderWithGoods> querySelective(Integer userId, String orderSn, Integer page,
      Integer size, String sort, String order) {
    // TODO 为啥这个接口暂时没有用到
    LitemallOrderExample example = new LitemallOrderExample();
    example.orderBy("add_time DESC");
    LitemallOrderExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);

    Page<Object> objects = PageHelper.startPage(page, size);
    List<LitemallOrderWithGoods> odersWithGoods = orderManualMapper
        .selectOdersWithGoodsByAdminId(1);

    return odersWithGoods;
  }

  @Override
  public int countSelective(Integer userId, String orderSn, Integer page, Integer size, String sort,
      String order) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);

    return (int) orderMapper.countByExample(example);
  }

  @Override
  public void updateById(LitemallOrder order) {
    orderMapper.updateByPrimaryKeySelective(order);
  }

  @Override
  public void deleteById(Integer id) {
    orderMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public int count() {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public int countByAdminId(Integer adminId) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public List<LitemallOrder> queryUnPaid() {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderStatusEqualTo(OrderStatusEnum.PENDING_PAYMENT).andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }

  @Override
  public List<LitemallOrder> queryUnConfirm() {
    LitemallOrderExample example = new LitemallOrderExample();
    List<OrderStatusEnum> unconfirmIds = new ArrayList<>(2);
    unconfirmIds.add(OrderStatusEnum.ONGOING);
    unconfirmIds.add(OrderStatusEnum.MERCHANT_SHIP);
    LocalDateTime now = LocalDateTime.now();
    example.or()
        .andAddTimeBetween(now.minusHours(12), now.plusHours(12))
        .andPayStatusEqualTo(PayStatusEnum.PAID)
        .andTpdStatusEqualTo(TPDStatusEnum.FINISHED)
        .andOrderStatusIn(unconfirmIds)
        .andShipEndTimeIsNotNull()
        .andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }

  @Override
  public LitemallOrder findBySn(String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }

  @Override
  public LitemallOrder findByDeliveryId(String deliveryId) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andDeliveryIdEqualTo(deliveryId).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void refundOrderGoodsByOrderId(Integer orderId) {
    List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
    for (LitemallOrderGoods orderGoods : orderGoodsList) {
      Integer productId = orderGoods.getProductId();
      LitemallProduct product = productService.findById(productId);
      Integer number = product.getGoodsNumber() + orderGoods.getNumber();
      product.setGoodsNumber(number);
      productService.updateById(product);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean refundOrder(Integer orderId) {
    final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    LitemallOrder order = this.findById(orderId);
    LitemallAdmin admin = adminService.findAllColunmById(order.getAdminId());

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

    boolean isSuccess = false;

    //解析结果
    try {
      Map map = wxPayEngine.doXMLParse(resultStr);
      String returnCode = map.get("return_code").toString();
      if (returnCode.equals("SUCCESS")) {
        String resultCode = map.get("result_code").toString();
        if (resultCode.equals("SUCCESS")) {
          log.info("退款成功");
          refundOrderGoodsByOrderId(orderId);
          order.setOrderStatus(OrderStatusEnum.REFUND_COMPLETE);
          order.setPayStatus(PayStatusEnum.REFUNDED);
          order.setEndTime(LocalDateTime.now());
          this.update(order);
          isSuccess = true;
        } else {
          log.error("退款失败：" + map.get("return_msg").toString());
        }
      } else {
        log.error("退款失败：" + map.get("return_msg").toString());
      }
    } catch (Exception e) {
      log.error("退款失败", e);
    }
    return isSuccess;

  }
}
