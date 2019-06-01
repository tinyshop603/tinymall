package com.attitude.tinymall.domain.dada.shop;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 门店详情
 */
@Data
@Builder
public class ShopDetailParams {
    @JSONField(name = "origin_shop_id")
    private String originShopId;
}
