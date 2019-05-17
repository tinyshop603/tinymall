package com.attitude.tinymall.domain.dada.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhaoguiyang on 2019/5/9.
 * @project Wechat
 */
@Data
public class AddOrderResult {

  /**
   * 配送距离(单位：米)
   */
  private float distance;
  /**
   * 实际运费(单位：元)，运费减去优惠券费用
   */
  private BigDecimal fee;
  /**
   * 运费(单位：元)
   */
  private BigDecimal deliverFee;

}


