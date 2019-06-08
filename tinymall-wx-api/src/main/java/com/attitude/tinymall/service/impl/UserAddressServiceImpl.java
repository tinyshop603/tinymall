package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.baidu.address.PoiAddress;
import com.attitude.tinymall.domain.baidu.geocode.GeoDecodingAddress;
import com.attitude.tinymall.service.BaiduFenceService;
import com.attitude.tinymall.service.IUserAddressService;
import com.attitude.tinymall.service.LitemallAddressService;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.service.LitemallRegionService;
import com.attitude.tinymall.util.CoodinateCovertorUtil;
import com.attitude.tinymall.util.IdGeneratorUtil;
import com.attitude.tinymall.vo.LocationVO;
import com.attitude.tinymall.vo.PoiAddressVO;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhaoguiyang on 2019/6/8.
 * @project Wechat
 */
@Service
public class UserAddressServiceImpl implements IUserAddressService {

  @Autowired
  private LitemallAddressService addressService;
  @Autowired
  private LitemallRegionService regionService;
  @Autowired
  private BaiduFenceService baiduFenceService;
  @Autowired
  private LitemallAdminService adminService;

  @Override
  public LocationVO getLocationDetailByGeoParams(
      String lng, String lat,
      String keyword, String appId) {
    Location bd09Location = CoodinateCovertorUtil
        .gcj02ToBd09(new Location(Double.valueOf(lng), Double.valueOf(lat)));
    try {
      GeoDecodingAddress geoDecodingAddress = baiduFenceService.reverseGeocoding(bd09Location);
      LocationVO locationVO = new LocationVO();
      BeanUtils.copyProperties(geoDecodingAddress, locationVO);
      String temUserId = IdGeneratorUtil.generateId("TEM");
      Integer shopFenceId = adminService.findAdminByOwnerId(appId).getShopFenceId();
      baiduFenceService.addMonitorPersonToFence(temUserId, shopFenceId);
      boolean isValidAddress = baiduFenceService
          .isValidLocationWithinFence(temUserId, geoDecodingAddress.getLocation(), shopFenceId);

      locationVO.setDistributionStatus(isValidAddress ? "in" : "out");
      List<PoiAddress> poiAddresses;
      if (StringUtils.isEmpty(keyword)) {
        // TODO region应根据店铺位置来, 目前只是在北京
        poiAddresses = baiduFenceService.listCirclePlacesByLocation(bd09Location, "3000");
      } else {
        // TODO region应根据店铺位置来, 目前只是在北京
        poiAddresses = baiduFenceService.listPlacesByKeywords(keyword, "北京");

      }

      locationVO.setKeywordsNearbyAddresses(poiAddresses
          .stream()
          .map(it -> {
            boolean validLocationWithinFence = false;
            try {
              validLocationWithinFence = baiduFenceService
                  .isValidLocationWithinFence(temUserId, it.getLocation(), shopFenceId);
            } catch (Exception e) {
              e.printStackTrace();
            }
            return new PoiAddressVO(it.getAddress(), it.getName(), validLocationWithinFence);
          })
          .collect(Collectors.toList()));
      // 从围栏中删除监控对象
      baiduFenceService.deleteMonitorPersonToFence(temUserId, shopFenceId);

      return locationVO;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
