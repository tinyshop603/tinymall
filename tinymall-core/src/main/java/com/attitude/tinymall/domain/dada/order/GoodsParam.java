package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 妥投异常之物品返回完成
 */
@Data
public class GoodsParam {
    @JSONField(name = "order_id")
    private Integer orderId;
}
