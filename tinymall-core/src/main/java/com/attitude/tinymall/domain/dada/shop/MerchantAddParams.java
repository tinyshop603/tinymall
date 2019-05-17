package com.attitude.tinymall.domain.dada.shop;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * 商户注册
 */
@Builder
@Data
public class MerchantAddParams {

    private String mobile;

    @JSONField(name = "city_name")
    private String cityName;

    @JSONField(name = "enterprise_name")
    private String enterpriseName;

    @JSONField(name = "enterprise_address")
    private String enterpriseAddress;

    @JSONField(name = "contact_name")
    private String contactName;

    @JSONField(name = "contact_phone")
    private String contactPhone;

    private String email;
}
