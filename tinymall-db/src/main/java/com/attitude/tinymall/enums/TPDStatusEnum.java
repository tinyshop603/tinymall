package com.attitude.tinymall.enums;

/**
 * @author zhaoguiyang on 2019/5/16.
 * @project Wechat
 */
public enum TPDStatusEnum {

  /**
   * 配送平台第三方接单中
   */
  WAITING(1, "待接单"),
  /**
   * 重新发单给第三方, 等待第三方接单
   */
  REWAITING(-1, "重新发单"),

  PICK_UP(2, "待取货"),

  /**
   * 第三方配送中
   */
  DELIVERING(3, "配送中"),

  /**
   * 第三方配送完成
   */
  FINISHED(4, "已完成"),

  /**
   * 第三方配送取消订单
   */
  CANCEL(5, "已取消"),

  EXPIRED(7, "已过期"),

  ASSIGN_ORDER(8, "指派单"),

  ABNORMAL_ORDER_RETURNING(9, "妥投异常之物品返回中"),
  ABNORMAL_ORDER_RETURNED(10, "妥投异常之物品返回完成"),

  RIDER_ARRIVE(100, "骑士到店"),

  CREATE_ORDER_FAILED(1000, "创建达达运单失败");


  String message;
  int code;

  TPDStatusEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

 public static TPDStatusEnum getByCode(int code) {
    for (TPDStatusEnum value : TPDStatusEnum.values()) {
      if (value.code == code) {
        return value;
      }
    }
    return null;
  }
}
