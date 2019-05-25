package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallUser;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.*;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.service.LitemallUserService;
import com.attitude.tinymall.service.client.RemoteDadaDeliveryClient;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

/**
 * @author zhaoguiyang on 2019/5/23.
 * @project Wechat
 */
@Service
public class LitemallDeliveryDetailServiceImpl implements LitemallDeliveryDetailService {

  @Autowired
  RemoteDadaDeliveryClient remoteDadaDeliveryClient;

  @Autowired
  LitemallOrderService  litemallOrderService;

  @Autowired
  LitemallUserService litemalluserService;


    /**
     * 新增订单
     * @param orderId
     * @return  status :0 成功   其余: 失败
     */
  public String dadaAddOrder( Integer orderId){
    LitemallOrder order =  litemallOrderService.findById(orderId);
    LitemallUser user = litemalluserService.findById(order.getUserId());
    AddOrderParams  orderParams = AddOrderParams.builder()
            .shopNo("${delivery.dada.source-id}")
            .cityCode("010")//北京
            .cargoPrice(order.getActualPrice())
            .isPrepay(0)
            .receiverName(user.getUsername() )
            .receiverAddress(order.getAddress())
            //    .receiverLat(order.get)
            //   .receiverLng(new Float("116.3084202915042"))
            .receiverPhone(user.getMobile())
            .originId(orderId.toString())
            .callback("http://39.107.81.107:8084/dada-order/callback/status")
            .build();

    ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
    if ("0".equals(res.getStatus())) {
      //TODO 插订单配送状态
    }
    return res.status;
  }
  //取消订单
  public String dadaFormalCancelOrder( String originId){
      FormalCancelParams  orderParams = FormalCancelParams.builder()
              .build();
    ResponseEntity<FormalCancelOrderResult> res = remoteDadaDeliveryClient.formalCancelOrder(orderParams);
    if ("0".equals(res.getStatus())) {
      //TODO 改变订单配送状态
    }
      return res.getStatus();
  }

  //查询订单详情
  public QueryOrderStatusResult dadaQueryOrderStatus( String originId){
      QueryOrderParams  orderParams = QueryOrderParams.builder()
              .build();
    ResponseEntity<QueryOrderStatusResult> res = remoteDadaDeliveryClient.queryOrderStatus(orderParams);
    return res.getResult();
  }

  @Override
  public LitemallDeliveryDetail getDeliveryDetailByDeliveryId(String deliveryId) {
    return null;
  }

}
