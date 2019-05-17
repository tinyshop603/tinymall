package com.attitude.tinymall.domain.dada.order;

import lombok.Data;

import java.math.BigDecimal;

/**
* @auther yangsong 20190511
*/
@Data
public class ReAddOrderResult {
    public float distance;//配送距离(单位：米)
    public BigDecimal fee;//实际运费(单位：元)，运费减去优惠券费用
    public BigDecimal deliverFee;//运费(单位：元)
}
