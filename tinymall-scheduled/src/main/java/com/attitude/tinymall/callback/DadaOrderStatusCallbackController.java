package com.attitude.tinymall.callback;

import com.attitude.tinymall.callback.params.DadaCallbackParams;
import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.enums.TPDStatusEnum;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
  private LitemallDeliveryDetailService litemallDeliveryDetailService;
  @Autowired
  private LitemallOrderService orderService;


  @PostMapping("/callback/status")
  public Object callBackOrderStatus(@RequestBody DadaCallbackParams callbackParams) {
    // TODO singure feature的校验, 以及订单状态的变更
    log.info("dada的回调参数如下:{} ", callbackParams.toString());
    TPDStatusEnum tpdStatusEnum = TPDStatusEnum.getByCode(callbackParams.getOrderStatus());
    String deliveryId = callbackParams.getOrderId();
    if (orderService.findByDeliveryId(deliveryId) == null) {
      log.info("当前配送Id找不到订单: {}", deliveryId);
      return ResponseUtil.ok();
    }

    LitemallDeliveryDetail currentDeliveyDetail = litemallDeliveryDetailService
        .getDeliveryDetailByDeliveryId(deliveryId);
    if (currentDeliveyDetail == null) {
      log.info("初始化订单的deliveryId: {}", deliveryId);
      if (!litemallDeliveryDetailService.initDeliveryDetailsByDeliveryId(deliveryId)) {
        log.info("达达回调初始化订单失败!");
        return ResponseUtil.ok();
      }
    }
    log.info("更新订单状态deliveryId; {}", deliveryId);

    LitemallDeliveryDetail newInfo = new LitemallDeliveryDetail();
    newInfo.setCancelFrom(callbackParams.getCancelFrom() + "");
    newInfo.setCancelReason(callbackParams.getCancelReason());
    newInfo.setClientId(callbackParams.getClientId());
    newInfo.setDmId(callbackParams.getDmId() + "");
    newInfo.setDmName(callbackParams.getDmName());
    newInfo.setDmMobile(callbackParams.getDmMobile());
    newInfo.setDeliveryId(deliveryId);
    litemallDeliveryDetailService.updateDeliveryInfo(tpdStatusEnum, newInfo);
    return ResponseUtil.ok();
  }
}
