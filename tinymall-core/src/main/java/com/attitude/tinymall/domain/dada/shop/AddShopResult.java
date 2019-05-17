package com.attitude.tinymall.domain.dada.shop;

import java.util.List;
import lombok.Data;

/**
 * @author zhaoguiyang on 2019/5/11.
 * @project Wechat
 */
@Data
public class AddShopResult {

  private int success;
  private List<SuccessList> successList;
  private List<FailedList> failedList;

  @Data
  public static class SuccessList {

    private String phone;
    private int business;
    private double lng;
    private double lat;
    private String stationName;
    private String originShopId;
    private String contactName;
    private String stationAddress;
    private String cityName;
    private String areaName;

  }

  @Data
  public static class FailedList {

    private String phone;
    private int business;
    private double lng;
    private double lat;
    private String stationName;
    private String originShopId;
    private String contactName;
    private String stationAddress;
    private String cityName;
    private String areaName;

  }
}
