package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.baidu.fence.ShopFenceResult;
import com.attitude.tinymall.domain.baidu.geocode.GeoCodingAddress;
import com.attitude.tinymall.domain.baidu.geocode.GeoDecodingAddress;

/**
 * @author zhaoguiyang on 2018/12/27.
 * @project Wechat
 */
public interface BaiduFenceService {

  /**
   * 位置转为坐标
   */
  GeoCodingAddress geocoding(String address);

  /**
   * 坐标转换为位置
   *
   * @return 代表该地理编码的位置信息
   */
  GeoDecodingAddress reverseGeocoding(double longitude, double latitude);

  ShopFenceResult createCircleFence(String shopId, String shopAddress, int deliveryRadius);

  /**
   * 创建圆形的地理围栏
   * @param shopId
   * @param location
   * @param deliveryRadius 配送半径 单位：米，取值范围(0,5000]
   * @return
   */
  ShopFenceResult createCircleFence(String shopId, Location location, int deliveryRadius);

  boolean updateCreateCircleFence(int fenceId, String shopAddress, int deliveryRadius);

  /**
   * 更新圆形的地理围栏
   * @param location
   * @param deliveryRadius 配送半径 单位：米，取值范围(0,5000]
   * @return
   */
  boolean updateCreateCircleFence(int fenceId, Location location, int deliveryRadius);

  boolean addMonitorPersonToFence(String userId, int fenceId);

  boolean isValidLocationWithinFence(String userId, String address , int fenceId) throws Exception;

  boolean isValidLocationWithinFence(String userId, Location location, int fenceId) throws Exception;

}
