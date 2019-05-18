package com.attitude.tinymall.enums;

/**
 * @author zhaoguiyang on 2019/5/16.
 * @project Wechat
 */
public enum TPDStatusEnum {

  /**
   * 配送平台第三方接单中
   */
  WAITING(""),
  /**
   * 重新发单给第三方, 等待第三方接单
   */
  REWAITING(""),
  /**
   * 第三方已接单
   */
  ACCEPTED(""),
  /**
   * 第三方配送取消订单
   */
  CANCEL(""),
  /**
   * 第三方配送中
   */
  DELIVERING(""),
  /**
   * 第三方配送完成
   */
  FINISHED("");


  String message;

  TPDStatusEnum(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
