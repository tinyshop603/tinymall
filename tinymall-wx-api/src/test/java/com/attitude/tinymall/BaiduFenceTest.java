package com.attitude.tinymall;

import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.baidu.address.PoiAddress;
import com.attitude.tinymall.service.BaiduFenceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableFeignClients(basePackages = "com.attitude.tinymall")
@Slf4j
public class BaiduFenceTest {

  @Autowired
  private BaiduFenceService baiduFenceService;


  @Test
  public void testJson() {
    assert baiduFenceService.geocoding("北京市海淀区上地十街10号") != null;
    assert baiduFenceService.reverseGeocoding( 116.404197, 39.915654) != null;
  }

  @Test
  public void testCreateFence() {
    assert baiduFenceService.createCircleFence(
        "test_fence_1",
        new Location(116.3084202915042, 40.05703033345938),
        5000).getFenceId() > 5;
  }

  @Test
  public void testCreateFenceByAddress() {
    assert baiduFenceService.createCircleFence("test_fence_1",
        "北京市昌平区回龙观镇龙禧三街北店嘉园南区底商10号",
        5000
    ).getFenceId() > 5;
  }

  @Test
  public void testAddPersonToMonitor() {
    assert baiduFenceService.addMonitorPersonToFence("qwas", 5);
  }

  @Test
  public void testQueryStatusByLocation() throws Exception {
    assert !baiduFenceService.isValidLocationWithinFence(
        "qwas",
        new Location(116.3084202915042, 50.05703033345938), 5);
  }

  @Test
  public void testQueryPlaceApi(){
    try {
      List<PoiAddress> poiAddresses = baiduFenceService.listPlacesByKeywords("万商大厦", "北京");

      System.out.println(poiAddresses.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
