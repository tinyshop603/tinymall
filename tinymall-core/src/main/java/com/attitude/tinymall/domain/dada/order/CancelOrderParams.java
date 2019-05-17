package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 取消追加订单
 */
@Data
public class CancelOrderParams {

    @JSONField(name = "order_id")//第三方订单号
    private BigDecimal orderId;

}
