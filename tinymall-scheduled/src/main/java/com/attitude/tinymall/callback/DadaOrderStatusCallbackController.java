package com.attitude.tinymall.callback;

import com.attitude.tinymall.callback.params.DadaCallbackParams;
import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.enums.TPDStatusEnum;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
@RestController
@RequestMapping("/dada-order")
@Slf4j
public class DadaOrderStatusCallbackController {

  @Autowired
  LitemallDeliveryDetailService litemallDeliveryDetailService;

  @PostMapping("/callback/status")
  public Object callBackOrderStatus(@RequestBody DadaCallbackParams callbackParams) {
    // TODO singure feature的校验, 以及订单状态的变更
    log.info("dada的回调参数如下: ", callbackParams.toString());
    TPDStatusEnum tpdStatusEnum = TPDStatusEnum.getByCode(callbackParams.getOrderStatus());
    String deliveryId = callbackParams.getOrderId();
    LitemallDeliveryDetail currentDeliveyDetail = litemallDeliveryDetailService
        .getDeliveryDetailByDeliveryId(deliveryId);
    if (currentDeliveyDetail == null) {
      log.info("初始化订单的deliveryId: {}", deliveryId);
      litemallDeliveryDetailService.initDeliveryDetailsByDeliveryId(deliveryId);
    }
    log.info("更新订单状态deliveryId; ${}", deliveryId);
    litemallDeliveryDetailService.updateOrderStatus(tpdStatusEnum, deliveryId);
    return ResponseUtil.ok();
  }
}
