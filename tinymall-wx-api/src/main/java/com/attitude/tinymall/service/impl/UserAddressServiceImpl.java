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
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhaoguiyang on 2019/6/8.
 * @project Wechat
 */
@Service
@Slf4j
public class UserAddressServiceImpl implements IUserAddressService {

  @Autowired
  private BaiduFenceService baiduFenceService;
  @Autowired
  private LitemallAdminService adminService;

  private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
      .setNameFormat("baidu-request-pool-%d").build();
  /**
   * 当前百度返回的默认的最多天数
   */
  private final int CORE_SIZE = 5;
  private ExecutorService executorService = new ThreadPoolExecutor(CORE_SIZE, CORE_SIZE,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(), namedThreadFactory);

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

      final int endIndex = poiAddresses.size() > 5 ? 4 : poiAddresses.size() - 1;
      poiAddresses = poiAddresses.subList(0, endIndex);

      final CountDownLatch countDownLatch = new CountDownLatch(poiAddresses.size());
      List<PoiAddressVO> poiAddressVOs = Collections.synchronizedList(new ArrayList<>(5));

      for (int i = 0; i < poiAddresses.size(); i++) {
        PoiAddress it = poiAddresses.get(i);
        int finalIndex = i;
        executorService.execute(() -> {
          boolean validLocationWithinFence = false;
          try {
            validLocationWithinFence = baiduFenceService
                .isValidLocationWithinFence(temUserId, it.getLocation(), shopFenceId);
          } catch (Exception e) {
            e.printStackTrace();
            log.info("批量判断是否在配送范围内出错: {}", e.getMessage());
          } finally {
            countDownLatch.countDown();
          }
          poiAddressVOs.add(finalIndex,
              new PoiAddressVO(it.getAddress(), it.getName(), validLocationWithinFence));
        });
      }

      countDownLatch.await();
      locationVO.setKeywordsNearbyAddresses(poiAddressVOs);
      // 从围栏中删除监控对象
      baiduFenceService.deleteMonitorPersonToFence(temUserId, shopFenceId);
      return locationVO;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
