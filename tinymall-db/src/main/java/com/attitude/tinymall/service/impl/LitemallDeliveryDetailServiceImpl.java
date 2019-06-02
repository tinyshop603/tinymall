package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallDeliveryDetailMapper;
import com.attitude.tinymall.dao.LitemallOrderMapper;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallUser;
import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.*;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.enums.TPDStatusEnum;
import com.attitude.tinymall.service.LitemallAddressService;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.service.LitemallUserService;
import com.attitude.tinymall.service.client.RemoteDadaDeliveryClient;
import com.attitude.tinymall.util.IdGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhaoguiyang on 2019/5/23.
 * @project Wechat
 */
@Service
@Slf4j
public class LitemallDeliveryDetailServiceImpl implements LitemallDeliveryDetailService {

    @Autowired
    private RemoteDadaDeliveryClient remoteDadaDeliveryClient;

    @Autowired
    private LitemallOrderService litemallOrderService;

    @Autowired
    private LitemallOrderMapper litemallOrderMapper;

    @Autowired
    private LitemallUserService litemalluserService;

    @Autowired
    private BaiduFenceServiceImpl baiduFenceService;

    @Autowired
    private LitemallDeliveryDetailMapper litemallDeliveryDetailMapper;

    @Autowired
    private LitemallAddressService addressService;

    @Value("${delivery.dada.source-id}")
    public String shopNo;

    @Value("${delivery.dada.callback-address}")
    public String dadaCallbackAddress;

    /**
     * 新增达达的订单,
     * 订单
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dadaAddOrder(Integer orderId) {
        LitemallOrder order = litemallOrderService.findById(orderId);
        if (order.getOrderStatus() != OrderStatusEnum.CUSTOMER_PAIED){
           throw new RuntimeException(String.format("订单:%s 状态异常,订单状态:%s, 不满足发货条件", order.getId(), order.getOrderStatus().getMessage()));
        }
        LitemallUser user = litemalluserService.findById(order.getUserId());
        Location location = baiduFenceService.geocoding(order.getAddress()).getLocation();
        LitemallAddress userDefaultAddress = addressService.findDefault(order.getUserId());
        //TODO 真实的shopNo, 可以tinymall admin 中获取
        order.setDeliveryId(IdGeneratorUtil.generateId("TPD"));
        AddOrderParams orderParams = AddOrderParams.builder()
                .shopNo(shopNo)
                //北京地区
                .cityCode("010")
                .cargoPrice(order.getActualPrice())
                .isPrepay(0)
                .receiverName(user.getUsername())
                .receiverAddress(order.getAddress())
                .receiverLat(Float.parseFloat("" + location.getLat()))
                .receiverLng(Float.parseFloat("" + location.getLng()))
                .receiverPhone(userDefaultAddress.getMobile())
                .callback(dadaCallbackAddress)
                .originId(order.getDeliveryId())
                .build();

        ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
        if (res.isSuccess()) {
            order.setDeliverFee(res.getResult().getFee().intValue());

            //setDeliveryId  setDistance setDeliverFee setFee
            LitemallDeliveryDetail litemallDeliveryDetail = new LitemallDeliveryDetail();
            litemallDeliveryDetail.setDeliveryId(order.getDeliveryId());
            litemallDeliveryDetail.setDistance("" + res.getResult().getDistance());
            litemallDeliveryDetail.setDeliverFee(res.getResult().getDeliverFee().intValue());
            litemallDeliveryDetail.setFee(res.getResult().getFee().intValue());

            order.setTpdStatus(TPDStatusEnum.WAITING);
            order.setOrderStatus(OrderStatusEnum.ONGOING);
            litemallOrderService.updateById(order);
            litemallDeliveryDetailMapper.insert(litemallDeliveryDetail);
        }
        return res.isSuccess();
    }

    @Override
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
        return litemallDeliveryDetailMapper.selectByPrimaryKey(deliveryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeliveryInfo(TPDStatusEnum orderStatus, LitemallDeliveryDetail deliveryDetail) {
        // 更新订单信息
        LitemallOrder order = litemallOrderService.findByDeliveryId(deliveryDetail.getDeliveryId());
        order.setTpdStatus(orderStatus);
        litemallOrderMapper.updateByPrimaryKeySelective(order);
        litemallDeliveryDetailMapper.updateByPrimaryKeySelective(deliveryDetail);
    }

    @Override
    public boolean initDeliveryDetailsByDeliveryId(String deliveryId) {

        QueryOrderParams orderParams = QueryOrderParams.builder()
                .orderId(deliveryId)
                .build();
        ResponseEntity<QueryOrderStatusResult> res = remoteDadaDeliveryClient
                .queryOrderStatus(orderParams);
        if (!res.isSuccess()) {
            log.info("达达订单初始化失败:{}", res.toString());
            return false;
        }
        LitemallDeliveryDetail litemallDeliveryDetail = new LitemallDeliveryDetail();
        litemallDeliveryDetail.setDeliveryId(deliveryId);
        litemallDeliveryDetail.setDmName(res.getResult().getTransporterName());
        litemallDeliveryDetail.setDmMobile(res.getResult().getTransporterPhone());
        litemallDeliveryDetail.setDeliveryId(deliveryId);
        litemallDeliveryDetail.setDistance("" + res.getResult().getDistance());
        litemallDeliveryDetail.setDeliverFee(Integer.parseInt("" + res.getResult().getDeliveryFee()));
        litemallDeliveryDetail.setFee(Integer.parseInt("" + res.getResult().getActualFee()));
        return litemallDeliveryDetailMapper.insert(litemallDeliveryDetail) > 0;
    }
}
