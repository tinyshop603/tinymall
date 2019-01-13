package com.attitude.tinymall.core.domain.baidu.geocode;

import com.attitude.tinymall.core.domain.baidu.address.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 */
@NoArgsConstructor
@Data
public class GeoCodingAddress {

  /**
   * status : 0 result : {"location":{"lng":116.3084202915042,"lat":40.05703033345938},"precise":1,"confidence":80,"comprehension":100,"level":"道路"}
   *
   *
   * /** location : {"lng":116.3084202915042,"lat":40.05703033345938} precise : 1 confidence : 80
   * comprehension : 100 level : 道路
   */

  private Location location;
  private int precise;
  private int confidence;
  private int comprehension;
  private String level;



}
