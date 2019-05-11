package com.attitude.tinymall.core.domain.dada.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhaoguiyang on 2019/5/9.
 * @project Wechat
 */
@Data
public class AddOrderResult {

  private float distance;//配送距离(单位：米)
  private BigDecimal fee;//实际运费(单位：元)，运费减去优惠券费用
  private BigDecimal deliverFee;//运费(单位：元)

}


