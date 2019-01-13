package com.attitude.tinymall.core.domain.baidu.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

  /**
   * lng : 116.3084202915042 lat : 40.05703033345938
   */

  private double lng;
  private double lat;
}
