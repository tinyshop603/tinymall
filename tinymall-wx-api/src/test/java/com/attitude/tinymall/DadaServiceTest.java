package com.attitude.tinymall;

import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.*;
import com.attitude.tinymall.domain.dada.order.CancelOrderParams;
import com.attitude.tinymall.domain.dada.shop.*;
import com.attitude.tinymall.domain.dada.testorder.*;
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
            .receiverLat(new Float("50.05703033345938"))
            .receiverLng(new Float("116.3084202915042"))
            .receiverPhone("13693002107")
            .originId("AA001")
            .callback("http://39.107.81.107:8084/dada-order/callback/status")
            .info("test")
            .build();
    ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
    log.error(res.toString());
  }
  @Test
  public void testDadaReAddOrder() {
    ReAddOrderParams orderParams = ReAddOrderParams
            .builder()
            .shopNo("11047059")
            .cityCode("021")
            .cargoPrice(new BigDecimal(100))
            .isPrepay(0)
            .receiverName("测试")
            .receiverAddress("北京市回龙观")
            .receiverLat(new Float("50.05703033345938"))
            .receiverLng(new Float("116.3084202915042"))
            .receiverPhone("13693002107")
            .originId("A1234568")
            .callback("http://www.abc.com")
            .info("test")
            .build();
    ResponseEntity<ReAddOrderResult> res = remoteDadaDeliveryClient.reAddOrder(orderParams);
    log.error(res.toString());
  }
  @Test
  public void queryOrderDeliverFee() {
    QueryDeliverFeeParams orderParams = QueryDeliverFeeParams
            .builder()
            .shopNo("11047059")
            .cityCode("021")
            .cargoPrice(new BigDecimal(100))
            .isPrepay(0)
            .receiverName("测试")
            .receiverAddress("北京市回龙观")
            .receiverLat(new Float("50.05703033345938"))
            .receiverLng(new Float("116.3084202915042"))
            .receiverPhone("13693002107")
            .originId("AA23")
            .callback("http://39.107.81.107:8084/dada-order/callback/status")
            .build();
    ResponseEntity<QueryOrderDeliverFeeResult> res = remoteDadaDeliveryClient.queryOrderDeliverFee(orderParams);
    log.error(res.toString());
  }

  @Test
  public void addOrderTip() {
    AddTipParams orderParams = AddTipParams
            .builder()
            .tips(new BigDecimal(10))
            .cityCode("021")
            .orderId("A1234568")
            .info("test")
            .build();
    ResponseEntity<AddOrderTipResult> res = remoteDadaDeliveryClient.addOrderTip(orderParams);
    log.error(res.toString());
  }
  @Test
  public void addOrderAfterQuery() {
    AddAfterQueryParams orderParams = AddAfterQueryParams
            .builder()
            .deliveryNo("123456879")
            .build();
    ResponseEntity<AfterAddOrderQueryResult> res = remoteDadaDeliveryClient.addOrderAfterQuery(orderParams);
    log.error(res.toString());
  }
  @Test
  public void queryOrderStatus() {
    QueryOrderParams orderParams = QueryOrderParams
            .builder()
            .orderId("123456879")
            .build();
    ResponseEntity<QueryOrderStatusResult> res = remoteDadaDeliveryClient.queryOrderStatus(orderParams);
    log.error(res.toString());
  }

  @Test
  public void formalCancelOrder() {
    FormalCancelParams orderParams = FormalCancelParams
            .builder()
            .orderId("123456879")
            .cancelReasonId("1")
            .cancelReason("fail")
            .build();
    ResponseEntity<FormalCancelOrderResult> res = remoteDadaDeliveryClient.formalCancelOrder(orderParams);
    log.error(res.toString());
  }

  @Test
  public void cancelOrderReasons() {
    ResponseEntity<CancelOrderReasonsResult> res = remoteDadaDeliveryClient.cancelOrderReasons();
    log.error(res.toString());
  }

  @Test
  public void existOrderAppoint() {
    ExistOrderParams orderParams = ExistOrderParams
            .builder()
            .shopNo("366782")
            .transporterId(92257)
            .orderId("123")
            .build();
    ResponseEntity res = remoteDadaDeliveryClient.existOrderAppoint(orderParams);
    log.error(res.toString());
  }
  @Test
  public void cancelOrderAppoint() {
    CancelOrderParams orderParams = CancelOrderParams
            .builder()
            .orderId("123")
            .build();
    ResponseEntity res = remoteDadaDeliveryClient.cancelOrderAppoint(orderParams);
    log.error(res.toString());
  }

  @Test
  public void transporterOrderAppoint() {
    TransporterParams orderParams = TransporterParams
            .builder()
            .shopNo("123")
            .build();
    ResponseEntity<TransporterOrderAppointResult> res = remoteDadaDeliveryClient.transporterOrderAppoint(orderParams);
    log.error(res.toString());
  }

  @Test
  public void dadaComplaint() {
    DadaParams orderParams = DadaParams
            .builder()
            .orderId("123")
            .reasonId(123456)
            .build();
    ResponseEntity<TransporterOrderAppointResult> res = remoteDadaDeliveryClient.dadaComplaint(orderParams);
    log.error(res.toString());
  }
  @Test
  public void complaintReasons() {
    ResponseEntity<ComplaintReasonsResult> res = remoteDadaDeliveryClient.complaintReasons();
    log.error(res.toString());
  }

  @Test
  public void confirmOrderGoods() {
    GoodsParam orderParams = GoodsParam
            .builder()
            .orderId("123456")
            .build();
    ResponseEntity res = remoteDadaDeliveryClient.confirmOrderGoods(orderParams);
    log.error(res.toString());
  }


  @Test
  public void testAddShop() {
    AddShopParams shopParams = AddShopParams
            .builder()
            .stationName("新门店1")
            .originShopId("shoxp002")
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
  @Test
  public void getCityCodeList() {

    ResponseEntity<ListCityResult> shopResult = remoteDadaDeliveryClient.getCityCodeList();

    log.info(shopResult.toString());
  }
  @Test
  public void addMerchant() {
    MerchantAddParams   shopParams = MerchantAddParams
            .builder()
            .mobile("13942471958")
            .cityName("北京")
            .enterpriseName("烟酒茶行")
            .enterpriseAddress("回龙观")
            .contactName("xxx")
            .contactPhone("13054897542")
            .email("101987@qq.com")
            .build();
    ResponseEntity shopResult = remoteDadaDeliveryClient.addMerchant(shopParams);

    log.info(shopResult.toString());
  }

  @Test
  public void shopUpdate() {
    ShopUpdateParams shopParams = ShopUpdateParams
            .builder()
            .originShopId("6666666")
            .build();
    ResponseEntity shopResult = remoteDadaDeliveryClient.shopUpdate(shopParams);

    log.info(shopResult.toString());
  }
  @Test
  public void getShopDetail() {
    ShopDetailParams shopParams = ShopDetailParams
            .builder()
            .originShopId("6666666")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.getShopDetail(shopParams);

    log.info(shopResult.toString());
  }

  /**
   * 下面的是Order测试环境调试方法 仅供测试环境使用 生产上不调用
   */
  @Test
  public void abnormalBackOrder() {
    AbnormalBackOrderParams orderParams = AbnormalBackOrderParams
            .builder()
            .orderId("6666666")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.abnormalBackOrder(orderParams);

    log.info(shopResult.toString());
  }
  @Test
  public void acceptOrder() {
    AcceptOrderParams orderParams = AcceptOrderParams
            .builder()
            .orderId("6666666")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.acceptOrder(orderParams);

    log.info(shopResult.toString());
  }
  @Test
  public void cancelOrder() {
    TestCancelOrderParams orderParams = TestCancelOrderParams
            .builder()
            .orderId("6666666")
            .reason("fall")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.cancelOrder(orderParams);

    log.info(shopResult.toString());
  }
  @Test
  public void expireOrder() {
    ExpireOrderParams orderParams = ExpireOrderParams
            .builder()
            .orderId("6666666")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.expireOrder(orderParams);

    log.info(shopResult.toString());
  }
  @Test
  public void fetchOrder() {
    FetchOrderParams orderParams = FetchOrderParams
            .builder()
            .orderId("6666666")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.fetchOrder(orderParams);

    log.info(shopResult.toString());
  }
  @Test
  public void finishOrder() {
    FinishOrderParams orderParams = FinishOrderParams
            .builder()
            .orderId("6666666")
            .build();
    ResponseEntity<ShopDetailResult> shopResult = remoteDadaDeliveryClient.finishOrder(orderParams);

    log.info(shopResult.toString());
  }

}
