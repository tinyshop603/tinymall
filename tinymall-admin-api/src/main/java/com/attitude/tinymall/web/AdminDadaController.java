package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.AddOrderParams;
import com.attitude.tinymall.domain.dada.shop.ShopDetailResult;
import com.attitude.tinymall.domain.dada.testorder.AcceptOrderParams;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.util.RegexUtil;
import com.attitude.tinymall.util.ResponseUtil;
import java.util.Arrays;
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
public class AdminDadaController {

  @Autowired
  private LitemallDeliveryDetailService litemallDeliveryDetailService;
  @Autowired
  private LitemallOrderService orderService;

  //申请配送
  @RequestMapping("/addOrder")
  public Object dadaAddOrder(Integer orderId) {
    String status = (String) litemallDeliveryDetailService.dadaAddOrder(orderId);
    if ("0".equals(status)) {
      return ResponseUtil.ok();
    } else {
      return ResponseUtil.fail();
    }
  }

  @PostMapping("/order")
  public Object addDadaOrder(@RequestBody Map<String, Integer> params) {
    // 创建达达订单, 并进行呼叫骑手
    Integer orderId = params.get("orderId");

    LitemallOrder currentOrder = orderService.findById(orderId);
    // 能够呼叫达达的订单状态
    if (Arrays.asList(OrderStatusEnum.CUSTOMER_PAIED, OrderStatusEnum.MERCHANT_ACCEPT,
        OrderStatusEnum.MERCHANT_SHIP).contains(currentOrder.getOrderStatus())) {
      return ResponseUtil.ok();
    }
    return ResponseUtil.fail(-1, "无法呼叫第三方配送, 叮当状态: " + currentOrder.getOrderStatus().getMessage());
  }

  @GetMapping("/order")
  public Object getDadaOrderDetail(@RequestParam("deliveryId") String deliveryId) {
    return ResponseUtil.ok();
  }


}
