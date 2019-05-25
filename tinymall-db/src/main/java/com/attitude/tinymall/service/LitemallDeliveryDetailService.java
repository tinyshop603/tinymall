package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.dada.order.QueryOrderStatusResult;

/**
 * @author zhaoguiyang on 2019/5/23.
 * @project Wechat
 */
public interface LitemallDeliveryDetailService {

    String dadaAddOrder( Integer orderId);

    String dadaFormalCancelOrder( String originId);

    LitemallDeliveryDetail getDeliveryDetailByDeliveryId(String deliveryId);

    void updateOrderStatus(Integer orderStatus,String delivery);
}
