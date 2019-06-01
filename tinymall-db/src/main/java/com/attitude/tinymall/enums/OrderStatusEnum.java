package com.attitude.tinymall.enums;

/**
 *
 * @author zhaoguiyang on 2019/5/16.
 * @project Wechat
 */
public enum OrderStatusEnum{

  /**
   * 订单刚被创建, 处于待付款状态
   */
  PENDING_PAYMENT("等待用户支付"),

  /**
   * 因超时系统自动取消, 订单终态
   */
  SYSTEM_AUTO_CANCEL("系统超时自动取消"),
  /**
   * 订单进行中
   */
  ONGOING("订单进行中"),

  /**
   * 订单完成, 即订单终态
   */
  COMPLETE("订单已完成"),

  /**
   * 因超时系统自动完成, 订单终态
   */
  SYSTEM_AUTO_COMPLETE("系统自动取消"),
  /*
   * 订单退款完成, 即订单终态
   */
  REFUND_COMPLETE("订单退款已完成"),

  /**
   * 商家确认收单
   */
  MERCHANT_ACCEPT("商家确认接受订单"),
  /**
   * 商家发货
   */
  MERCHANT_SHIP("商家已发货"),
  /**
   * 商家取消, 订单终态
   */
  MERCHANT_CANCEL("商家取消订单"),

  /**
   * 由用户发起退款, 当前处于退款中
   */
  MERCHANT_REFUNDING("用户申请退款"),


  /**
   *用户取消, 订单终态
   */
  CUSTOMER_CANCEL("用户取消订单"),

  CUSTOMER_PAIED("用户已付款");

  private String message;

  OrderStatusEnum(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
