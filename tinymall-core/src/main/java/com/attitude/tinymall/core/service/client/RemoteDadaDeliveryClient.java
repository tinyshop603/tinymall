package com.attitude.tinymall.core.service.client;

import com.attitude.tinymall.core.config.DadaDeliveryFeignConfig;
import com.attitude.tinymall.core.domain.dada.order.*;
import com.attitude.tinymall.core.domain.dada.ResponseEntity;
import com.attitude.tinymall.core.domain.dada.shop.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhaoguiyang on 2019/5/8.
 * @project Wechat
 */
@FeignClient(name = "dada-delivery", url = "${delivery.dada.host}", configuration = DadaDeliveryFeignConfig.class)
public interface RemoteDadaDeliveryClient {

  @PostMapping("/api/order/addOrder")
  ResponseEntity<AddOrderResult> addOrder(@RequestBody AddOrderParams params);

  @PostMapping("/api/order/reAddOrder")
  ResponseEntity<ReAddOrderResult> reAddOrder(@RequestBody ReAddOrderParams params);

  @PostMapping("/api/order/queryDeliverFee")
  ResponseEntity<QueryOrderDeliverFeeResult> queryOrderDeliverFee(@RequestBody QueryDeliverFeeParams params);

  @PostMapping("/api/order/addTip")
  ResponseEntity<AddOrderTipResult> addOrderTip(@RequestBody AddTipParams params);

  @PostMapping("/api/order/addAfterQuery")
  ResponseEntity<AfterAddOrderQueryResult> addOrderAfterQuery(@RequestBody AddAfterQueryParams params);

  @PostMapping("/api/order/status/query")
  ResponseEntity<QueryOrderStatusResult> queryOrderStatus(@RequestBody QueryOrderParams params);

  @PostMapping("/api/order/formalCancel")
  ResponseEntity<FormalCancelOrderResult> formalCancelOrder(@RequestBody FormalCancelParams params);

  @PostMapping("/api/order/cancel/reasons")
  ResponseEntity<CancelOrderReasonsResult> cancelOrderReasons();

  @PostMapping("/api/order/appoint/exist")
  ResponseEntity existOrderAppoint(@RequestBody ExistOrderParams params);

  @PostMapping("/api/order/appoint/cancel")
  ResponseEntity cancelOrderAppoint(@RequestBody CancelOrderParams params);

  @PostMapping("/api/order/appoint/list/transporter")
  ResponseEntity<TransporterOrderAppointResult> transporterOrderAppoint(@RequestBody TransporterParams params);

  @PostMapping("/api/complaint/dada")
  ResponseEntity dadaComplaint(@RequestBody DadaParams params);

  @PostMapping("/api/complaint/reasons")
  ResponseEntity<ComplaintReasonsResult> complaintReasons();

  @PostMapping("/api/order/confirm/goods")
  ResponseEntity confirmOrderGoods(@RequestBody GoodsParam params);

  @PostMapping("/api/shop/add")
  ResponseEntity<AddShopResult> addShop(@RequestBody AddShopParams shopParams);

  @GetMapping("/api/cityCode/list")
  ResponseEntity<ListCityResult> getCityCodeList();

  @PostMapping("/merchantApi/merchant/add")
  ResponseEntity addMerchant(@RequestBody MerchantAddParams shopParams);

  @PostMapping("/api/shop/update")
  ResponseEntity shopUpdate(@RequestBody ShopUpdateParams shopParams);

  @GetMapping("/api/shop/detail")
  ResponseEntity<ShopDetailResult> getShopDetail(@RequestBody ShopDetailParams shopParams);



}
