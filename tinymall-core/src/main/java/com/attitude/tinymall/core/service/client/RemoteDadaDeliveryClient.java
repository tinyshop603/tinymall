package com.attitude.tinymall.core.service.client;

import com.attitude.tinymall.core.config.DadaDeliveryFeignConfig;
import com.attitude.tinymall.core.domain.dada.order.*;
import com.attitude.tinymall.core.domain.dada.ResponseEntity;
import com.attitude.tinymall.core.domain.dada.shop.*;
import org.springframework.cloud.openfeign.FeignClient;
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
  ResponseEntity<QueryDeliverFeeResult> queryDeliverFee(@RequestBody QueryDeliverFeeParams params);

  @PostMapping("/api/order/addTip")
  ResponseEntity<AddTipResult> addTip(@RequestBody AddTipParams params);

  @PostMapping("/api/order/addAfterQuery")
  ResponseEntity<AddAfterQueryResult> addAfterQuery(@RequestBody AddAfterQueryParams params);

  @PostMapping("/api/order/status/query")
  ResponseEntity<QueryOrderResult> queryOrder(@RequestBody QueryOrderParams params);

  @PostMapping("/api/order/formalCancel")
  ResponseEntity<FormalCancelResult> formalCancel(@RequestBody FormalCancelParams params);

  @PostMapping("/api/order//cancel/reasons")
  ResponseEntity<CancelReasonsResult> cancelReasons();

  @PostMapping("/api/order/appoint/exist")
  ResponseEntity existOrder(@RequestBody ExistOrderParams params);

  @PostMapping("/api/order/appoint/cancel")
  ResponseEntity  cancelOrder(@RequestBody CancelOrderParams params);

  @PostMapping("/api/order/appoint/list/transporter")
  ResponseEntity<TransporterResult> transporter(@RequestBody TransporterParams params);

  @PostMapping("/api/complaint/dada")
  ResponseEntity dada(@RequestBody DadaParams params);

  @PostMapping("/api/complaint/reasons")
  ResponseEntity<ComplaintReasonsResult> complaintReasons();

  @PostMapping("/api/order/confirm/goods")
  ResponseEntity goods(@RequestBody GoodsParam params);

  @PostMapping("/api/shop/add")
  ResponseEntity<AddShopResult> addShop(@RequestBody AddShopParams shopParams);

  @PostMapping("/api/cityCode/list")
  ResponseEntity<ListCityResult> addShop();

  @PostMapping("/merchantApi/merchant/add")
  ResponseEntity merchantAdd(@RequestBody MerchantAddParams shopParams);

  @PostMapping("/api/shop/update")
  ResponseEntity shopUpdate(@RequestBody ShopUpdateParams shopParams);

  @PostMapping("/api/shop/detail")
  ResponseEntity<ShopDetailResult> addShop(@RequestBody ShopDetailParams shopParams);



}
