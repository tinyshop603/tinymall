package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 追加订单
 */
@Data
@Builder
public class ExistOrderParams {
    @JSONField(name = "shop_no")//追加订单的门店编码
    private String shopNo;

    @JSONField(name = "transporter_id")//追加的配送员ID
    private Integer transporterId;

    @JSONField(name = "order_id")//追加的第三方订单ID
    private String a;
}
