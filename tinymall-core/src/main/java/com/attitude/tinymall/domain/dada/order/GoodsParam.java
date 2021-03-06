package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 妥投异常之物品返回完成
 */
@Data
@Builder
public class GoodsParam {
    @JSONField(name = "order_id")
    private String orderId;
}
