package com.attitude.tinymall.domain.dada.testorder;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AbnormalBackOrderParams {
    @JSONField(name = "order_id")
    private String orderId;
}

