package com.attitude.tinymall.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.attitude.tinymall.core.domain.baidu.BaiduResponse;
import com.attitude.tinymall.core.domain.baidu.address.Location;
import com.attitude.tinymall.core.domain.baidu.fence.QueryFenceLocationResult;
import com.attitude.tinymall.core.domain.baidu.fence.ShopFenceResult;
import com.attitude.tinymall.core.domain.baidu.geocode.GeoCodingAddress;
import com.attitude.tinymall.core.domain.baidu.geocode.GeoDecodingAddress;
import com.attitude.tinymall.core.service.BaiduFenceService;
import com.attitude.tinymall.core.util.HttpClientUtil;
import com.attitude.tinymall.core.util.HttpClientUtil.HttpClientResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
      BaiduResponse<GeoCodingAddress> result = JSON.parseObject(httpClientResult.getContent(),
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
    params.put("location", longitude + "," + latitude);
    params.put("output", "json");
    params.put("ak", baiduAk);
    params.put("ret_coordtype", mapCoordtype);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doGet("http://api.map.baidu.com/geocoder/v2/", params);
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
  public boolean hangUpPerson(String userId) {
    Map<String, String> params = new HashMap<>(4);
    params.put("entity_name", userId);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/entity/add", params);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      return result.get("status").equals(0);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
    }
    return false;
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
    Map<String, String> params = new HashMap<>(4);
    params.put("ak", baiduAk);
    params.put("service_id", eagleEyeServiceId.toString());
    params.put("fence_id", String.valueOf(fenceId));
    params.put("monitored_person", userId);
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doPost("http://yingyan.baidu.com/api/v3/fence/addmonitoredperson", params);
      Map result = JSON.parseObject(httpClientResult.getContent(), Map.class);
      return result.get("status").equals(0);
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
    QueryFenceLocationResult result = JSON
        .parseObject(httpClientResult.getContent(), QueryFenceLocationResult.class);
    return result.getMonitoredStatuses().get(0).getMonitoredStatus()
        .equals(QueryFenceLocationResult.LOCATION_IN);

  }
}
