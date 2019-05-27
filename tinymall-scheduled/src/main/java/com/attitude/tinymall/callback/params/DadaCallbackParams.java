package com.attitude.tinymall.callback.params;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhaoguiyang on 2019/5/18.
 * @project Wechat
 */
@Data
@ToString
public class DadaCallbackParams {

  private String signature;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("order_id")
  private String orderId;

  @JsonProperty("order_status")
  private int orderStatus;

  @JsonProperty("cancel_reason")
  private String cancelReason;

  @JsonProperty("cancel_from")
  private int cancelFrom;

  @JsonProperty("dm_id")
  private int dmId;

  @JsonProperty("dm_name")
  private String dmName;

  @JsonProperty("dm_mobile")
  private String dmMobile;

  @JsonProperty("update_time")
  private int updateTime;


}
