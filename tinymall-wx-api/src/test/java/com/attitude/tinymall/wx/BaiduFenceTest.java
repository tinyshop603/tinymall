package com.attitude.tinymall.wx;

import com.attitude.tinymall.core.domain.baidu.address.Location;
import com.attitude.tinymall.core.domain.dada.AddOrderParams;
import com.attitude.tinymall.core.domain.dada.AddOrderResult;
import com.attitude.tinymall.core.domain.dada.ResponseEntity;
import com.attitude.tinymall.core.service.BaiduFenceService;
import com.attitude.tinymall.core.service.client.RemoteDadaDeliveryClient;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
  @Autowired
  private RemoteDadaDeliveryClient remoteDadaDeliveryClient;

  @Test
  public void testJson() {
    assert baiduFenceService.geocoding("北京市海淀区上地十街10号") != null;
    assert baiduFenceService.reverseGeocoding(39.915654, 116.404197) != null;
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
  public void testBaiduRes() {
    AddOrderParams orderParams = AddOrderParams
        .builder()
        .shopNo("11047059")
        .cityCode("021")
        .cargoPrice(new BigDecimal(100))
        .isPrepay(0)
        .receiverName("测试")
        .receiverAddress("北京市回龙观")
        .receiverLat(new BigDecimal("50.05703033345938"))
        .receiverLng(new BigDecimal("116.3084202915042"))
        .receiverPhone("13693002107")
        .originId("A1234568")
        .callback("http://www.abc.com")
        .build();
    ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
    log.error(res.toString());
  }


}
