package com.attitude.tinymall.core.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 投诉达达
 */
@Data
@Builder
public class DadaParams {
    @JSONField(name = "order_id")//第三方订单编号
    private String orderId;

    @JSONField(name = "reason_id")//投诉原因ID
    private Integer reasonId;
}

