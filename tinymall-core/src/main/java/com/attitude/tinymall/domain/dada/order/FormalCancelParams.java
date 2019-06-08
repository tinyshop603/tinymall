package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * @a yangsong 20190511
 */
@Data
@Builder
public class FormalCancelParams {
    @JSONField(name = "origin_id")//第三方订单编号
    private String  orderId;

    @JSONField(name = "cancel_reason_id")//取消原因ID（取消原因列表）
    private Integer cancelReasonId;

    @JSONField(name = "cancel_reason")//取消原因(当取消原因ID为其他时，此字段必填)
    private String cancelReason;
}
