package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.FormalCancelOrderResult;
import com.attitude.tinymall.domain.dada.order.FormalCancelParams;
import com.attitude.tinymall.domain.dada.order.QueryOrderDeliverFeeResult;
import com.attitude.tinymall.domain.dada.order.QueryOrderStatusResult;
import com.attitude.tinymall.enums.TPDStatusEnum;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zhaoguiyang on 2019/5/23.
 * @project Wechat
 */
public interface LitemallDeliveryDetailService {

    boolean dadaAddOrder( Integer orderId);

    String dadaFormalCancelOrder( String originId);

    LitemallDeliveryDetail getDeliveryDetailByDeliveryId(String deliveryId);

    void updateDeliveryInfo(TPDStatusEnum orderStatus,LitemallDeliveryDetail deliveryDetail);

    /**
     * 根据配送的第三方Id进行初始化订单配送的详情
     * 为false的情况是 达达回调失败或者系统内部错误
     * @param deliveryId
     * @return
     */
    boolean initDeliveryDetailsByDeliveryId(String deliveryId);

    /**
     * 预发布流程 查询达达运费
     * @param
     * @return  Map:String setdeliveryNo String setdeliveryNo
     */
    Object queryDeliverFee4WX(Integer userId , Integer adminId , BigDecimal actualPrice , LitemallAddress address);

    /**
     * 取消订单
     */
    boolean formalCancelOrder(Integer orderId,Integer cancelReasonId);
}
