package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.dada.order.QueryOrderStatusResult;
import com.attitude.tinymall.enums.TPDStatusEnum;

/**
 * @author zhaoguiyang on 2019/5/23.
 * @project Wechat
 */
public interface LitemallDeliveryDetailService {

    boolean dadaAddOrder( Integer orderId);

    String dadaFormalCancelOrder( String originId);

    LitemallDeliveryDetail getDeliveryDetailByDeliveryId(String deliveryId);

    void updateOrderStatus(TPDStatusEnum orderStatus,String delivery);

    /**
     * 根据配送的第三方Id进行初始化订单配送的详情
     * 为false的情况是 达达回调失败或者系统内部错误
     * @param deliveryId
     * @return
     */
    boolean initDeliveryDetailsByDeliveryId(String deliveryId);
}
