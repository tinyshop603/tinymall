package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallDeliveryDetailMapper;
import com.attitude.tinymall.dao.LitemallOrderMapper;
import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderExample;
import com.attitude.tinymall.domain.LitemallUser;
import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.*;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.enums.TPDStatusEnum;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.service.LitemallUserService;
import com.attitude.tinymall.service.client.RemoteDadaDeliveryClient;
import com.attitude.tinymall.util.IdGeneratorUtil;
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
  LitemallOrderService litemallOrderService;

  @Autowired
  LitemallOrderMapper litemallOrderMapper;

  @Autowired
  LitemallUserService litemalluserService;

  @Autowired
  BaiduFenceServiceImpl BaiduFenceService;

  @Autowired
  LitemallDeliveryDetailMapper litemallDeliveryDetailMapper;


  /**
   * 新增订单
   *
   * @return status :0 成功   其余: 失败
   */
  public String dadaAddOrder(Integer orderId) {
    LitemallOrder order = litemallOrderService.findById(orderId);
    LitemallUser user = litemalluserService.findById(order.getUserId());
    Location location = BaiduFenceService.geocoding("order.getAddress()").getLocation();

    order.setDeliveryId(IdGeneratorUtil.generateId("dada"));//TODO
    AddOrderParams orderParams = AddOrderParams.builder()
        .shopNo("${delivery.dada.source-id}")
        .cityCode("010")//北京
        .cargoPrice(order.getActualPrice())
        .isPrepay(0)
        .receiverName(user.getUsername())
        .receiverAddress(order.getAddress())
        .receiverLat(Float.parseFloat("" + location.getLat()))
        .receiverLng(Float.parseFloat("" + location.getLng()))
        .receiverPhone(user.getMobile())
        .originId(orderId.toString())
        .build();

    ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
    order.setDeliverFee(Integer.parseInt("" + res.getResult().getFee()));

    //setDeliveryId  setDistance setDeliverFee setFee
    LitemallDeliveryDetail litemallDeliveryDetail = new LitemallDeliveryDetail();
    litemallDeliveryDetail.setDeliveryId(order.getDeliveryId());
    litemallDeliveryDetail.setDistance("" + res.getResult().getDistance());
    litemallDeliveryDetail.setDeliverFee(Integer.parseInt("" + res.getResult().getDeliverFee()));
    litemallDeliveryDetail.setFee(Integer.parseInt("" + res.getResult().getFee()));
    if ("0".equals(res.getStatus())) {
      order.setTpdStatus(TPDStatusEnum.WAITING);
      litemallOrderService.updateById(order);
      litemallDeliveryDetailMapper.insert(litemallDeliveryDetail);
    }
    return res.status;
  }

  //取消订单
  public String dadaFormalCancelOrder(String originId) {
    FormalCancelParams orderParams = FormalCancelParams.builder()
        .build();
    ResponseEntity<FormalCancelOrderResult> res = remoteDadaDeliveryClient
        .formalCancelOrder(orderParams);
    if ("0".equals(res.getStatus())) {
      //TODO 改变订单配送状态
    }
    return res.getStatus();
  }


  @Override
  public LitemallDeliveryDetail getDeliveryDetailByDeliveryId(String deliveryId) {
    return new LitemallDeliveryDetail();
//    QueryOrderParams  orderParams = QueryOrderParams.builder()
//            .orderId(deliveryId)
//            .build();
//    ResponseEntity<QueryOrderStatusResult> res = remoteDadaDeliveryClient.queryOrderStatus(orderParams);
//    LitemallDeliveryDetail litemallDeliveryDetail = new LitemallDeliveryDetail();
//    litemallDeliveryDetail.setDeliveryId(deliveryId);
//    litemallDeliveryDetail.setDmName(res.getResult().getTransporterName());
//    litemallDeliveryDetail.setDmMobile(res.getResult().getTransporterPhone());
//    litemallDeliveryDetail.setDeliveryId(deliveryId);
//    litemallDeliveryDetail.setDistance(""+res.getResult().getDistance());
//    litemallDeliveryDetail.setDeliverFee(Integer.parseInt(""+res.getResult().getDeliveryFee()));
//    litemallDeliveryDetail.setFee(Integer.parseInt(""+res.getResult().getActualFee()));
//    litemallDeliveryDetailMapper.insert(litemallDeliveryDetail);
//    return litemallDeliveryDetail;
  }

  @Override
  public void updateOrderStatus(Integer orderStatus, String delivery) {
    LitemallOrder order = new LitemallOrder();
    order.setTpdStatus(TPDStatusEnum.getByCode(orderStatus));
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderSnEqualTo(delivery).andDeletedEqualTo(false);
    litemallOrderMapper.updateByExampleSelective(order, example);
  }
}
