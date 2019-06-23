package com.attitude.tinymall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoiAddressVO {

  /**
   * 排序索引
   */
  private Integer orderIndex;

  private String province;
  private String city;
  private String area;
  private String address;
  private String name;
  private Boolean inDistributionScope;

}
