package com.attitude.tinymall.core.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 查询追加配送员
 *
 */
@Data
@Builder
public class TransporterParams {
    @JSONField(name = "shop_no")
    private String  shopNo;
}
