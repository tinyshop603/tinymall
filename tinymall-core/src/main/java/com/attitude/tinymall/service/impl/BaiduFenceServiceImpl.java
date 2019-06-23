package com.attitude.tinymall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.attitude.tinymall.domain.baidu.BaiduResponse;
import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.baidu.address.PoiAddress;
import com.attitude.tinymall.domain.baidu.fence.QueryFenceLocationResult;
import com.attitude.tinymall.domain.baidu.fence.ShopFenceResult;
import com.attitude.tinymall.domain.baidu.geocode.GeoCodingAddress;
import com.attitude.tinymall.domain.baidu.geocode.GeoDecodingAddress;
import com.attitude.tinymall.service.BaiduFenceService;
import com.attitude.tinymall.util.HttpClientUtil;
import com.attitude.tinymall.util.HttpClientUtil.HttpClientResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoguiyang on 2019/1/6.
 * @project Wechat
 */
@Service
@Slf4j
public class BaiduFenceServiceImpl implements BaiduFenceService {

  @Value("${baidu.service.ak}")
  private String baiduAk;

  @Value("${baidu.eagle.eye.service.id}")
  private Integer eagleEyeServiceId;

  @Value("${baidu.map.coordtype}")
  private String mapCoordtype;

  @Override
  public GeoCodingAddress geocoding(String address) {
    Map<String, String> params = new HashMap<>(4);
    params.put("address", address);
    params.put("output", "json");
    params.put("ak", baiduAk);
    params.put("ret_coordtype", mapCoordtype);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doGet("http://api.map.baidu.com/geocoder/v2/", params);

      String response = httpClientResult.getContent();
      log.info("geocoder: {}", response);
      BaiduResponse<GeoCodingAddress> result = JSON.parseObject(response,
          new TypeReference<BaiduResponse<GeoCodingAddress>>() {
          });
      return result.getResult();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return null;
  }

  @Override
  public GeoDecodingAddress reverseGeocoding(double longitude, double latitude) {
    Map<String, String> params = new HashMap<>(4);
    params.put("location", latitude + "," +  longitude);
    params.put("output", "json");
    params.put("ak", baiduAk);
    params.put("ret_coordtype", mapCoordtype);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doGet("http://api.map.baidu.com/geocoder/v2/", params);
      String response = httpClientResult.getContent();
      log.info("reverseGeocoding: {}", response);
      BaiduResponse<GeoDecodingAddress> result = JSON.parseObject(httpClientResult.getContent(),
          new TypeReference<BaiduResponse<GeoDecodingAddress>>() {
          });
      return result.getResult();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return null;
  }

  @Override
  public GeoDecodingAddress reverseGeocoding(Location location) {
    return reverseGeocoding(location.getLng(), location.getLat());
  }

  @Override
  public ShopFenceResult createCircleFence(String shopId, String shopAddress,
      int deliveryRadius) {
    GeoCodingAddress codingAddress = this.geocoding(shopAddress);
    if (codingAddress != null && codingAddress.getLocation() != null) {
      return this.createCircleFence(shopId, codingAddress.getLocation(), deliveryRadius);
    }
    return null;
  }

  @Override
  public ShopFenceResult createCircleFence(String shopId, Location location, int deliveryRadius) {
    final int maxRadius = 5000;
    if (deliveryRadius > maxRadius) {
      return null;
    }
    Map<String, String> params = new HashMap<>(8);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    params.put("fence_name", shopId);
    params.put("longitude", String.valueOf(location.getLng()));
    params.put("latitude", String.valueOf(location.getLat()));
    params.put("radius", String.valueOf(deliveryRadius));
    params.put("coord_type", mapCoordtype);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/fence/createcirclefence", params);

      String response = httpClientResult.getContent();
      log.info("createCircleFence: {}", response);
      return JSON.parseObject(httpClientResult.getContent(), ShopFenceResult.class);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return null;
  }

  @Override
  public boolean updateCreateCircleFence(int fenceId, String shopAddress, int deliveryRadius) {
    GeoCodingAddress codingAddress = this.geocoding(shopAddress);
    if (codingAddress != null && codingAddress.getLocation() != null) {
      return this.updateCreateCircleFence(fenceId, codingAddress.getLocation(), deliveryRadius);
    }
    return false;
  }

  @Override
  public boolean updateCreateCircleFence(int fenceId, Location location, int deliveryRadius) {
    final int maxRadius = 5000;
    if (deliveryRadius > maxRadius) {
      return false;
    }
    Map<String, String> params = new HashMap<>(8);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    params.put("fence_id", String.valueOf(fenceId));
    params.put("longitude", String.valueOf(location.getLng()));
    params.put("latitude", String.valueOf(location.getLat()));
    params.put("radius", String.valueOf(deliveryRadius));
    params.put("coord_type", mapCoordtype);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/fence/updatecirclefence ", params);
      String response = httpClientResult.getContent();
      log.info("updateCreateCircleFence: {}", response);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      return result.get("status").equals(0);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return false;
  }

  @Override
  public boolean addMonitorPersonToFence(String userId, int fenceId) {
    if (hangUpPerson(userId)) {
      Map<String, String> params = new HashMap<>(4);
      params.put("ak", baiduAk);
      params.put("service_id", eagleEyeServiceId.toString());
      params.put("fence_id", String.valueOf(fenceId));
      params.put("monitored_person", userId);
      try {
        HttpClientResult httpClientResult = HttpClientUtil
            .doPost("http://yingyan.baidu.com/api/v3/fence/addmonitoredperson", params);
        String response = httpClientResult.getContent();
        log.info("addMonitorPersonToFence: {}", response);
        Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
        log.info("add the user: {}, to the fence: {}, result is: {}", userId, fenceId, result.toString());
        return result.get("status").equals(0);
      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
      }
    }
    return false;
  }

  @Override
  public boolean deleteMonitorPersonToFence(String userId, int fenceId) {
    Map<String, String> params = new HashMap<>(4);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    params.put("fence_id", String.valueOf(fenceId));
    params.put("monitored_person", userId);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/fence/deletemonitoredperson", params);
      String response = httpClientResult.getContent();
      log.info("deleteMonitorPersonToFence: {}", response);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      log.info("delete the user: {}, to the fence: {}, result is: {}", userId, fenceId, result.toString());
      // 删除终端的实体对象
      return result.get("status").equals(0) && deleteHangUpPersion(userId);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return false;
  }

  @Override
  public boolean isValidLocationWithinFence(String userId, String address, int fenceId)
      throws Exception {
    GeoCodingAddress geocoding = this.geocoding(address);
    return this.isValidLocationWithinFence(userId, geocoding.getLocation(), fenceId);
  }

  @Override
  public boolean isValidLocationWithinFence(String userId, Location location,
      int fenceId) throws Exception {
    Map<String, String> params = new HashMap<>(8);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    params.put("fence_ids", String.valueOf(fenceId));
    params.put("monitored_person", userId);
    params.put("longitude", String.valueOf(location.getLng()));
    params.put("latitude", String.valueOf(location.getLat()));
    params.put("coord_type", mapCoordtype);
    HttpClientResult httpClientResult = HttpClientUtil
        .doGet("http://yingyan.baidu.com/api/v3/fence/querystatusbylocation", params);
    String response = httpClientResult.getContent();
    log.info("isValidLocationWithinFence: {}", response);
    QueryFenceLocationResult result = JSON
        .parseObject(httpClientResult.getContent(), QueryFenceLocationResult.class);
    return result.getMonitoredStatuses().get(0).getMonitoredStatus()
        .equals(QueryFenceLocationResult.LOCATION_IN);

  }

  @Override
  public List<PoiAddress> listPlacesByKeywords(String keywords, String region) throws Exception{
    Map<String, String> params = new HashMap<>(8);
    params.put("ak", baiduAk);
    params.put("timestamp", String.valueOf(System.currentTimeMillis()));
    // 1是简单信息, 2 是详细信息
    params.put("scope", "1");
    params.put("output", "json");
    params.put("city_limit", "true");
    params.put("filter", "sort_name:distance|sort_rule:1");
    params.put("coord_type", mapCoordtype);
    params.put("region", region);
    params.put("query", keywords);
    HttpClientResult httpClientResult = HttpClientUtil.doGet("http://api.map.baidu.com/place/v2/search", params);
    String response = httpClientResult.getContent();
    log.info("listPlacesByKeywords: {}", response);
    BaiduResponse<List<PoiAddress>> result = JSON.parseObject(httpClientResult.getContent(),
            new TypeReference<BaiduResponse<List<PoiAddress>>>() {
            });

    if (result.getStatus() != 0){
      return Collections.emptyList();
    }
    return result.getResults();
  }

  @Override
  public List<PoiAddress> listCirclePlacesByLocation(Location location, String radius)
      throws Exception {
    Map<String, String> params = new HashMap<>(8);
    params.put("ak", baiduAk);
    params.put("timestamp", String.valueOf(System.currentTimeMillis()));
    // 1是简单信息, 2 是详细信息
    params.put("scope", "1");
    params.put("output", "json");

    params.put("coord_type", mapCoordtype);

    params.put("location", location.getLat() + "," +  location.getLng());
    params.put("radius", radius);
    params.put("filter", "sort_name:distance|sort_rule:1");

    params.put("radius_limit", "true");

    params.put("query", "住宅区$宿舍$写字楼$政府机构$公司企业");
    HttpClientResult httpClientResult = HttpClientUtil.doGet("http://api.map.baidu.com/place/v2/search", params);
    String response = httpClientResult.getContent();
    log.info("listCirclePlacesByLocation: {}", response);
    BaiduResponse<List<PoiAddress>> result = JSON.parseObject(httpClientResult.getContent(),
        new TypeReference<BaiduResponse<List<PoiAddress>>>() {
        });

    if (result.getStatus() != 0){
      return Collections.emptyList();
    }
    return result.getResults();
  }


  /**
   * 增加可监控的顾客, 判断该顾客是否在配送范围内, 目前需求, 仅需要创建一个顾客, 改变其地理位置即可, 但为了以后的需求考虑, 每个顾客挂接到监控网上, 方便后续调取
   */
  private boolean hangUpPerson(String userId) {
    Map<String, String> params = new HashMap<>(4);
    params.put("entity_name", userId);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/entity/add", params);
      String response = httpClientResult.getContent();
      log.info("hangUpPerson: {}", response);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      return result.get("status").equals(0) || result.get("status").equals(3005);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return false;
  }

  private boolean deleteHangUpPersion(String userId){
    Map<String, String> params = new HashMap<>(4);
    params.put("entity_name", userId);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/entity/delete", params);
      String response = httpClientResult.getContent();
      log.info("deleteHangUpPersion: {}", response);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      return result.get("status").equals(0) || result.get("status").equals(3005);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return false;
  }


}
