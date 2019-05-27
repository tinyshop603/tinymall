package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author  yangsong 20190511
 */
@Data
@Builder
public class AddTipParams {
    @JSONField(name = "order_id") //第三方订单编号
    private String orderId;

    private BigDecimal tips;//小费金额(单位：元)

    @JSONField(name = "city_code")//订单城市区号
    private String cityCode;

    private String info;//备注(字段最大长度：512)
}
