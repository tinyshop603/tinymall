package com.attitude.tinymall.core.domain.dada.shop;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 门店详情
 */
@Data
public class ShopDetailParams {
    @JSONField(name = "origin_shop_id")
    private String originS0hopId;
}
