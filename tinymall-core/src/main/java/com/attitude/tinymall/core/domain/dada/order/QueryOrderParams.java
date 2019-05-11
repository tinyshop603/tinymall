package com.attitude.tinymall.core.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * @author yangsong 20190511
 */
@Builder
@Data
public class QueryOrderParams {
    @JSONField(name = "order_id")
    private String orderId;
}
