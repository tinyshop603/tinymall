package com.attitude.tinymall.db.enums;

import org.omg.CORBA.UNKNOWN;

/**
 * @author zhaoguiyang on 2019/5/16.
 * @project Wechat
 */
public enum PayStatusEnum {

  /**
   * 付款中
   */
  PAYING(""),
  /**
   * 已付款
   */
  PAID(""),
  /**
   * 未付款
   */
  UNPAID(""),

  REFUNDED(""),

  /**
   * 未知异常状态
   */
  UNKNOWN("");

  private String message;

  PayStatusEnum(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
