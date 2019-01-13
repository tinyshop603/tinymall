package com.attitude.tinymall.core.domain.baidu.geocode;

import com.alibaba.fastjson.annotation.JSONField;
import com.attitude.tinymall.core.domain.baidu.address.Location;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 * {
 *     "location": {
 *         "lng": 116.40419699999994,
 *         "lat": 39.91565407698539
 *     },
 *     "formatted_address": "北京市东城区中华路甲10号",
 *     "business": "天安门,前门,和平门",
 *     "addressComponent": {
 *         "country": "中国",
 *         "country_code": 0,
 *         "country_code_iso": "CHN",
 *         "country_code_iso2": "CN",
 *         "province": "北京市",
 *         "city": "北京市",
 *         "city_level": 2,
 *         "district": "东城区",
 *         "town": "",
 *         "adcode": "110101",
 *         "street": "中华路",
 *         "street_number": "甲10号",
 *         "direction": "附近",
 *         "distance": "39"
 *     },
 *     "pois": [],
 *     "roads": [],
 *     "poiRegions": [{
 *         "direction_desc": "内",
 *         "name": "天安门",
 *         "tag": "旅游景点;风景区",
 *         "uid": "65e1ee886c885190f60e77ff"
 *     }],
 *     "sematic_description": "天安门内,端门南139米",
 *     "cityCode": 131
 * }
 */
@NoArgsConstructor
@Data
public class GeoDecodingAddress {

  private Location location;
  @JSONField(name = "formatted_address")
  private String formattedAddress;
  private String business;
  private AddressComponent addressComponent;
  @JSONField(name = "sematic_description")
  private String sematicDescription;
  private int cityCode;
  private List<?> pois;
  private List<?> roads;
  private List<PoiRegions> poiRegions;

  @NoArgsConstructor
  @Data
  public static class AddressComponent {

    private String country;
    @JSONField(name = "country_code")
    private int countryCode;
    @JSONField(name = "country_code_iso")
    private String countryCodeIso;
    @JSONField(name = "country_code_iso2")
    private String countryCodeIso2;
    private String province;
    private String city;
    @JSONField(name = "city_level")
    private int cityLevel;
    private String district;
    private String town;
    private String adcode;
    private String street;
    @JSONField(name = "street_number")
    private String streetNumber;
    private String direction;
    private String distance;
  }

  @NoArgsConstructor
  @Data
  public static class PoiRegions {

    @JSONField(name = "direction_desc")
    private String directionDesc;
    private String name;
    private String tag;
    private String uid;
  }

}
