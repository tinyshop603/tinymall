package com.attitude.tinymall.service.client;

import com.attitude.tinymall.config.DadaDeliveryFeignConfig;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.AddAfterQueryParams;
import com.attitude.tinymall.domain.dada.order.AddOrderParams;
import com.attitude.tinymall.domain.dada.order.AddOrderResult;
import com.attitude.tinymall.domain.dada.order.AddOrderTipResult;
import com.attitude.tinymall.domain.dada.order.AddTipParams;
import com.attitude.tinymall.domain.dada.order.CancelOrderParams;
import com.attitude.tinymall.domain.dada.order.CancelOrderReasonsResult;
import com.attitude.tinymall.domain.dada.order.ComplaintReasonsResult;
import com.attitude.tinymall.domain.dada.order.DadaParams;
import com.attitude.tinymall.domain.dada.order.ExistOrderParams;
import com.attitude.tinymall.domain.dada.order.FormalCancelOrderResult;
import com.attitude.tinymall.domain.dada.order.FormalCancelParams;
import com.attitude.tinymall.domain.dada.order.GoodsParam;
import com.attitude.tinymall.domain.dada.order.QueryDeliverFeeParams;
import com.attitude.tinymall.domain.dada.order.QueryOrderDeliverFeeResult;
import com.attitude.tinymall.domain.dada.order.QueryOrderParams;
import com.attitude.tinymall.domain.dada.order.QueryOrderStatusResult;
import com.attitude.tinymall.domain.dada.order.ReAddOrderParams;
import com.attitude.tinymall.domain.dada.order.ReAddOrderResult;
import com.attitude.tinymall.domain.dada.order.TransporterOrderAppointResult;
import com.attitude.tinymall.domain.dada.order.TransporterParams;
import com.attitude.tinymall.domain.dada.shop.AddShopParams;
import com.attitude.tinymall.domain.dada.shop.AddShopResult;
import com.attitude.tinymall.domain.dada.shop.CityResult;
import com.attitude.tinymall.domain.dada.shop.MerchantAddParams;
import com.attitude.tinymall.domain.dada.shop.ShopDetailParams;
import com.attitude.tinymall.domain.dada.shop.ShopDetailResult;
import com.attitude.tinymall.domain.dada.shop.ShopUpdateParams;
import java.util.List;

import com.attitude.tinymall.domain.dada.testorder.*;
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
  ResponseEntity addOrderAfterQuery(@RequestBody AddAfterQueryParams params);

  @PostMapping("/api/order/status/query")
  ResponseEntity<QueryOrderStatusResult> queryOrderStatus(@RequestBody QueryOrderParams params);

  @PostMapping("/api/order/formalCancel")
  ResponseEntity<FormalCancelOrderResult> formalCancelOrder(@RequestBody FormalCancelParams params);

  @PostMapping("/api/order/cancel/reasons")
  ResponseEntity<List<CancelOrderReasonsResult>> cancelOrderReasons();

  @PostMapping("/api/order/appoint/exist")
  ResponseEntity existOrderAppoint(@RequestBody ExistOrderParams params);

  @PostMapping("/api/order/appoint/cancel")
  ResponseEntity cancelOrderAppoint(@RequestBody CancelOrderParams params);

  @PostMapping("/api/order/appoint/list/transporter")
  ResponseEntity<TransporterOrderAppointResult> transporterOrderAppoint(@RequestBody TransporterParams params);

  @PostMapping("/api/complaint/dada")
  ResponseEntity dadaComplaint(@RequestBody DadaParams params);

  @PostMapping("/api/complaint/reasons")
  ResponseEntity<List<ComplaintReasonsResult>> complaintReasons();

  @PostMapping("/api/order/confirm/goods")
  ResponseEntity confirmOrderGoods(@RequestBody GoodsParam params);

  @PostMapping("/api/shop/add")
  ResponseEntity<AddShopResult> addShop(@RequestBody List<AddShopParams> shopParams);

  @GetMapping("/api/cityCode/list")
  ResponseEntity<List<CityResult>> getCityCodeList();

  @PostMapping("/merchantApi/merchant/add")
  ResponseEntity addMerchant(@RequestBody MerchantAddParams shopParams);

  @PostMapping("/api/shop/update")
  ResponseEntity shopUpdate(@RequestBody ShopUpdateParams shopParams);

  @GetMapping("/api/shop/detail")
  ResponseEntity<ShopDetailResult> getShopDetail(@RequestBody ShopDetailParams shopParams);

/**
 * 下面的方法仅供测试环境使用
 */
  //模拟接受订单
  @PostMapping("/api/order/accept")
  ResponseEntity abnormalBackOrder(@RequestBody AbnormalBackOrderParams orderParams);

  //模拟完成取货
  @PostMapping("/api/order/fetch")
  ResponseEntity acceptOrder(@RequestBody AcceptOrderParams orderParams);

  //完成订单
  @PostMapping("/api/order/finish")
  ResponseEntity cancelOrder(@RequestBody TestCancelOrderParams orderParams);

  //取消订单
  @PostMapping("/api/order/cancel")
  ResponseEntity expireOrder(@RequestBody ExpireOrderParams orderParams);

  //模拟订单过期
  @PostMapping("/api/shop/update")
  ResponseEntity fetchOrder(@RequestBody FetchOrderParams orderParams);
  //模拟异常妥投物品返还中
  @PostMapping("/api/order/delivery/abnormal/back")
  ResponseEntity finishOrder(@RequestBody FinishOrderParams orderParams);

}
