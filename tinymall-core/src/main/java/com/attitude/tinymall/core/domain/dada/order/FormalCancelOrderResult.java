package com.attitude.tinymall.core.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangsong 20190511
 */
@Data
public class FormalCancelOrderResult {
     @JSONField(name = " deduct_fee")//扣除的违约金(单位：元)
     private BigDecimal deductFee;
}
