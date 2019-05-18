package com.attitude.tinymall;

import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.AddOrderParams;
import com.attitude.tinymall.domain.dada.order.AddOrderResult;
import com.attitude.tinymall.domain.dada.shop.AddShopParams;
import com.attitude.tinymall.domain.dada.shop.AddShopResult;
import com.attitude.tinymall.service.client.RemoteDadaDeliveryClient;
import java.math.BigDecimal;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author zhaoguiyang on 2019/5/11.
 * @project Wechat
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableFeignClients(basePackages = "com.attitude.tinymall")
@Slf4j
public class DadaServiceTest {

  @Autowired
  private RemoteDadaDeliveryClient remoteDadaDeliveryClient;

  @Test
  public void testDadaAddOrder() {
    AddOrderParams orderParams = AddOrderParams
        .builder()
        .shopNo("11047059")
        .cityCode("021")
        .cargoPrice(new BigDecimal(100))
        .isPrepay(0)
        .receiverName("测试")
        .receiverAddress("北京市回龙观")
        .receiverLat(new Double("50.05703033345938"))
        .receiverLng(new Double("116.3084202915042"))
        .receiverPhone("13693002107")
        .originId("A1234568")
        .callback("http://www.abc.com")
        .build();
    ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
    log.error(res.toString());
  }

  @Test
  public void testAddShop() {
    AddShopParams shopParams = AddShopParams
        .builder()
        .stationName("新门店1")
        .originShopId("shoxp001")
        .areaName("浦东新区")
        .contactName("xxx")
        .cityName("上海")
        .business(1)
        .lng(new Float("121.515014"))
        .lat(new Float("31.229081"))
        .phone("13012345678")
        .stationAddress("详细地址")
        .build();

    ResponseEntity<AddShopResult> shopResult = remoteDadaDeliveryClient.addShop(Arrays.asList(shopParams));

    log.info(shopResult.toString());
  }


}
