package com.attitude.tinymall.domain.dada.shop;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 门店更新
 */
@Data
@Builder
public class ShopUpdateParams {
    @JSONField(name = "origin_shop_id")
    private Integer originShopId;
}
