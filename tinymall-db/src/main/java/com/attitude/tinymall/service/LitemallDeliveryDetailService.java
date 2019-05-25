package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallDeliveryDetail;

/**
 * @author zhaoguiyang on 2019/5/23.
 * @project Wechat
 */
public interface LitemallDeliveryDetailService {

  LitemallDeliveryDetail getDeliveryDetailByOrderId(String orderId);

}
