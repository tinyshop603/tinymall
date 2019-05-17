package com.attitude.tinymall.db.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 * @author zhaoguiyang on 2019/5/16.
 * @project Wechat
 */
public enum OrderStatusEnum{

  /**
   * 订单刚被创建, 处于待付款状态
   */
  PENDING_PAYMENT(""),

  /**
   * 因超时系统自动取消, 订单终态
   */
  SYSTEM_AUTO_CANCEL(""),
  /**
   * 订单进行中
   */
  ONGOING(""),

  /**
   * 订单完成, 即订单终态
   */
  COMPLETE(""),


  /**
   * 商家确认收单
   */
  MERCHANT_ACCEPT(""),
  /**
   * 商家发货
   */
  MERCHANT_SHIP(""),
  /**
   * 商家取消, 订单终态
   */
  MERCHANT_CANCEL(""),

  /**
   * 由用户发起退款, 当前处于退款中
   */
  MERCHANT_REFUNDING(""),


  /**
   *用户取消, 订单终态
   */
  CUSTOMER_CANCEL("");

  private String message;

  OrderStatusEnum(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
