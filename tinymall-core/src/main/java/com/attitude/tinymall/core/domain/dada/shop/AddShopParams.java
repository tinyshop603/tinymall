package com.attitude.tinymall.core.domain.dada.shop;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhaoguiyang on 2019/5/11.
 * @project Wechat
 */
@Data
@Builder
public class AddShopParams {

  @JSONField(name = "station_name")
  private String stationName;

  @JSONField(name = "origin_shop_id")
  private String originShopId;

  @JSONField(name = "area_name")
  private String areaName;

  @JSONField(name = "station_address")
  private String stationAddress;

  @JSONField(name = "contact_name")
  private String contactName;

  @JSONField(name = "city_name")
  private String cityName;

  private int business;
  private float lng;
  private float lat;
  private String phone;

}
