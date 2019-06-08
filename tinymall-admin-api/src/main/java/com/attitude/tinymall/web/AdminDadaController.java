package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.AddOrderParams;
import com.attitude.tinymall.domain.dada.shop.ShopDetailResult;
import com.attitude.tinymall.domain.dada.testorder.AcceptOrderParams;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.util.RegexUtil;
import com.attitude.tinymall.util.ResponseUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping("/admin/{userName}/dada")
@Slf4j
public class AdminDadaController {

  @Autowired
  private LitemallDeliveryDetailService litemallDeliveryDetailService;
  @Autowired
  private LitemallOrderService orderService;

  @PostMapping("/order")
  public Object addDadaOrder(@RequestBody Map<String, Integer> params) {
    // 创建达达订单, 并进行呼叫骑手
    Integer orderId = params.get("orderId");
    log.info("订单: {} 正在创建dada的第三方订单", orderId);
    LitemallOrder currentOrder = orderService.findById(orderId);
    // 能够呼叫达达的订单状态
    if (Arrays.asList(OrderStatusEnum.CUSTOMER_PAIED, OrderStatusEnum.MERCHANT_ACCEPT,
        OrderStatusEnum.MERCHANT_SHIP).contains(currentOrder.getOrderStatus())) {
      if (litemallDeliveryDetailService.dadaAddOrder(currentOrder.getId())) {
        return ResponseUtil.ok();
      }
    }
    log.info("订单: {} 创建第三方个订单失败!!", orderId);
    return ResponseUtil.fail(-1, "无法呼叫第三方配送, 订单状态: " + currentOrder.getOrderStatus().getMessage());
  }

  @GetMapping("/order")
  public Object getDadaOrderDetail(@RequestParam("deliveryId") String deliveryId) {
    return ResponseUtil.ok();
  }


   @GetMapping("/formalCancelOrder")
  public Object formalCancelOrder(Integer orderId,Integer cancelReasonId) {
    //cancelReasonId  36:商家取消配送
     if (true==litemallDeliveryDetailService.formalCancelOrder(orderId,cancelReasonId)){
       log.info("订单: {} 取消订单成功!!", orderId);
       return ResponseUtil.ok();
     }else {
       log.info("订单: {} 取消订单失败!!", orderId);
       return  ResponseUtil.fail();
     }

}
}