package com.attitude.tinymall.domain.dada.shop;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 门店详情
 */
public class ShopDetailResult {
    @JSONField(name = "origin_shop_id")
    private String originShopId;

    @JSONField(name = "station_name")
    private String stationName;

    private Integer business;

    @JSONField(name = "city_name")
    private String cityName;

    @JSONField(name = "area_name")
    private String areaName;

    @JSONField(name = "station_address")
    private String stationAddress;

    private float lng;

    private float lat;

    @JSONField(name = "contact_name")
    private String contactName;

    private String phone;

    @JSONField(name = "id_card")
    private String idCard;

    private Integer status;
}
