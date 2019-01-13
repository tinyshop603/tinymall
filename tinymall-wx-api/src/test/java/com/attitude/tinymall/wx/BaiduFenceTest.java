package com.attitude.tinymall.wx;

import com.attitude.tinymall.core.domain.baidu.address.Location;
import com.attitude.tinymall.core.service.BaiduFenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BaiduFenceTest {

  @Autowired
  private BaiduFenceService baiduFenceService;

  @Test
  public void testJson() {
    assert baiduFenceService.geocoding("北京市海淀区上地十街10号") != null;
    assert baiduFenceService.reverseGeocoding(39.915654, 116.404197) != null;
  }

  @Test
  public void testAddPerson() {
    assert baiduFenceService.hangUpPerson("qwas");
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
  public void testQueryStatusByLocation() {
    assert !baiduFenceService.isValidLocationWithinFence(
        "qwas",
        new Location(116.3084202915042, 50.05703033345938), 5);
  }


}
