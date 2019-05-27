package com.attitude.tinymall.domain.dada.testorder;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TestCancelOrderParams {
    @JSONField(name = "order_id")
    private String orderId;

    private String  reason;
}
