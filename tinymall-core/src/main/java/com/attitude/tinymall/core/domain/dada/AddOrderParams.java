package com.attitude.tinymall.core.domain.dada;

import com.alibaba.fastjson.annotation.JSONField;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhaoguiyang on 2019/5/9.
 * @project Wechat
 */
@Builder
@Data
public class AddOrderParams {

  @JSONField(name = "shop_no")
  private String shopNo;

  @JSONField(name = "origin_id")
  private String originId;

  @JSONField(name = "city_code")
  private String cityCode;

  @JSONField(name = "cargo_price")
  private BigDecimal cargoPrice;

  @JSONField(name = "is_prepay")
  private Integer isPrepay;

  @JSONField(name = "receiver_name")
  private String receiverName;

  @JSONField(name = "receiver_address")
  private String receiverAddress;

  @JSONField(name = "receiver_lat")
  private BigDecimal receiverLat;

  @JSONField(name = "receiver_lng")
  private BigDecimal receiverLng;

  private String callback;

  @JSONField(name = "receiver_phone")
  private String receiverPhone;

}
