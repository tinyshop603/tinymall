package com.attitude.tinymall.domain.baidu.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Location {

  /**
   * lng : 116.3084202915042 lat : 40.05703033345938
   */

  private double lng;
  private double lat;
}
