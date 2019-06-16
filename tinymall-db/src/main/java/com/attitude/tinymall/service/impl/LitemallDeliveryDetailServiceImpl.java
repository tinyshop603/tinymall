package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallDeliveryDetailMapper;
import com.attitude.tinymall.dao.LitemallOrderMapper;
import com.attitude.tinymall.dao.LitemallPreDeliveryDetailMapper;
import com.attitude.tinymall.domain.*;
import com.attitude.tinymall.domain.baidu.address.Location;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.*;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.enums.TPDStatusEnum;
import com.attitude.tinymall.service.*;
import com.attitude.tinymall.service.client.RemoteDadaDeliveryClient;
import com.attitude.tinymall.util.CoodinateCovertorUtil;
import com.attitude.tinymall.util.IdGeneratorUtil;
import com.attitude.tinymall.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private LitemallPreDeliveryDetailMapper litemallPreDeliveryDetailMapper;

    @Autowired
    private LitemallAddressService addressService;

    @Autowired
    private LitemallAdminService adminService;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
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
        if (order.getOrderStatus() != OrderStatusEnum.MERCHANT_ACCEPT){
           throw new RuntimeException(String.format("订单:%s 状态异常,订单状态:%s, 不满足发货条件", order.getId(), order.getOrderStatus().getMessage()));
        }
        LitemallUser user = litemalluserService.findById(order.getUserId());
        Location location = baiduFenceService.geocoding(order.getAddress()).getLocation();
        // 百度坐标转化为高德坐标
        location = CoodinateCovertorUtil.bd09ToGcj02(location);
        LitemallAdmin admin = adminService.findById(order.getAdminId());
        String deliveryId = IdGeneratorUtil.generateId("TPD");
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        StringBuilder orderInfo = new StringBuilder("商品详情:+\t\r\n");
        for(LitemallOrderGoods orderGood : orderGoodsList){
            orderInfo.append(orderGood.getGoodsName()+"\t"+"x"+orderGood.getNumber()+"\t\r\n");
        }
        AddOrderParams orderParams = AddOrderParams.builder()
                .shopNo(admin.getTpdShopNo().toString())
                //北京地区
                .cityCode("010")
                .cargoPrice(order.getActualPrice())
                .isPrepay(0)
                .receiverName(order.getConsignee())
                .receiverAddress(order.getAddress())
                .receiverLat(Float.parseFloat("" + location.getLat()))
                .receiverLng(Float.parseFloat("" + location.getLng()))
                .receiverPhone(order.getMobile())
                .callback(dadaCallbackAddress)
                .originId(deliveryId)
                .info(orderInfo.toString())
                .build();

        ResponseEntity<AddOrderResult> res = remoteDadaDeliveryClient.addOrder(orderParams);
        if (res.isSuccess()) {
            order.setDeliverFee(res.getResult().getFee());
            LitemallDeliveryDetail litemallDeliveryDetail = new LitemallDeliveryDetail();
            litemallDeliveryDetail.setDeliveryId(deliveryId);
            litemallDeliveryDetail.setDistance("" + res.getResult().getDistance());
            litemallDeliveryDetail.setDeliverFee(new BigDecimal(res.getResult().getDeliverFee().intValue()));
            litemallDeliveryDetail.setFee(new BigDecimal(res.getResult().getFee().intValue()));
            litemallDeliveryDetail.setCreateTime(LocalDateTime.now());
            litemallDeliveryDetail.setUpdateTime(litemallDeliveryDetail.getCreateTime());
            order.setDeliveryId(deliveryId);
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
        switch (orderStatus){
            case RIDER_ARRIVE:
                order.setShipStartTime(LocalDateTime.now());
                break;
            case FINISHED:
                order.setShipEndTime(LocalDateTime.now());
                break;
            default:
                break;
        }
        litemallOrderMapper.updateByPrimaryKeySelective(order);
        deliveryDetail.setUpdateTime(LocalDateTime.now());
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
        litemallDeliveryDetail.setDeliverFee(res.getResult().getDeliveryFee());
        litemallDeliveryDetail.setFee(res.getResult().getActualFee());
        return litemallDeliveryDetailMapper.insert(litemallDeliveryDetail) > 0;
    }

    @Override
    public Object queryDeliverFee4WX(Integer userId , Integer adminId, BigDecimal actualPrice
            ,LitemallAddress checkedAddress ) {
        LitemallUser user = litemalluserService.findById(userId);
        // 百度坐标转化为高德坐标
        Location location = baiduFenceService.geocoding(checkedAddress.getAddress()).getLocation();
        location = CoodinateCovertorUtil.bd09ToGcj02(location);
        LitemallAdmin admin = adminService.findById(adminId);

        String deliveryId = IdGeneratorUtil.generateId("TPD");
        QueryDeliverFeeParams orderParams = QueryDeliverFeeParams.builder()
                .shopNo(admin.getTpdShopNo().toString())
                //北京地区
                .cityCode("010")
                .cargoPrice(actualPrice)
                .isPrepay(0)
                .receiverName(checkedAddress.getName())
                .receiverAddress(checkedAddress.getAddress())
                .receiverLat(Float.parseFloat("" + location.getLat()))
                .receiverLng(Float.parseFloat("" + location.getLng()))
                .receiverPhone(checkedAddress.getMobile())
                .callback(dadaCallbackAddress)
                .originId(deliveryId)
                .build();
        ResponseEntity<QueryOrderDeliverFeeResult> res ;
        try {
                res = remoteDadaDeliveryClient.queryOrderDeliverFee(orderParams);
                if (res.isSuccess()) {
                    //setDeliveryId  setDistance setDeliverFee setFee
                    LitemallPreDeliveryDetail litemallPreDeliveryDetail = new LitemallPreDeliveryDetail();
                    litemallPreDeliveryDetail.setDeliveryId(deliveryId);
                    litemallPreDeliveryDetail.setDistance("" + res.getResult().getDistance());
                    litemallPreDeliveryDetail.setDeliverFee(new BigDecimal(res.getResult().getDeliverFee().intValue()));
                    litemallPreDeliveryDetail.setFee(res.getResult().getFee().intValue());
                    litemallPreDeliveryDetail.setDeliveryNo(res.getResult().getDeliveryNo());
                    litemallPreDeliveryDetail.setDeliverFee(res.getResult().getDeliverFee());
                    litemallPreDeliveryDetail.setCouponFee(res.getResult().getCouponFee());
                    litemallPreDeliveryDetail.setTips(res.getResult().getTips());
                    litemallPreDeliveryDetail.setInsuranceFee(res.getResult().getInsuranceFee());
                    litemallPreDeliveryDetail.setCreateTime(LocalDateTime.now());
                    litemallPreDeliveryDetail.setUpdateTime(litemallPreDeliveryDetail.getCreateTime());
                    litemallPreDeliveryDetailMapper.insert(litemallPreDeliveryDetail);
                    return ResponseUtil.ok(res.getResult().getDeliverFee());
                }else{
                    log.error(String.format("查询运费失败 原因:%s", res.getMsg()));
                    return ResponseUtil.fail(-1,res.getMsg());
                }
        }catch(Exception e){
              e.printStackTrace();
        }
        log.error("与达达通讯失败");
        return ResponseUtil.fail(-1,"与达达通讯失败");
    }

    @Override
   public boolean formalCancelOrder(Integer orderId,Integer cancelReasonId){
       LitemallOrder order = litemallOrderService.findById(orderId);
       FormalCancelParams orderParams = FormalCancelParams.builder()
               .orderId(order.getDeliveryId())
               .cancelReasonId(cancelReasonId)
               .build();
       ResponseEntity<FormalCancelOrderResult> res = remoteDadaDeliveryClient.formalCancelOrder(orderParams);
       if(res.isSuccess()==true){
           order.setTpdStatus(TPDStatusEnum.CANCEL);
           order.setOrderStatus(OrderStatusEnum.MERCHANT_CANCEL);
           litemallOrderService.updateById(order);
           return res.isSuccess();
       }else{
           throw new RuntimeException(String.format("取消订单失败 原因:%s", res.getMsg()));
       }
    }
}
