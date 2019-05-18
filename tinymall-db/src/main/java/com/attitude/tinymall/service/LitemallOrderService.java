package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallAdminMapper;
import com.attitude.tinymall.dao.manual.LitemallOrderManualMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderExample;
import com.attitude.tinymall.domain.LitemallOrderWithGoods;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallOrderMapper;
import com.attitude.tinymall.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface LitemallOrderService {


   int add(LitemallOrder order, String appId) ;

   List<LitemallOrder> query(Integer userId, String appId) ;

   int count(Integer userId) ;

   LitemallOrder findById(Integer orderId);
   String getRandomNum(Integer num) ;

   LitemallOrder queryByOrderSn(Integer userId, String orderSn) ;

   int countByOrderSn(Integer userId, String orderSn) ;

  // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
   String generateOrderSn(Integer userId);

   List<LitemallOrder> queryByOrderStatus(Integer userId, List<OrderStatusEnum> orderStatus) ;

   int countByOrderStatus(Integer userId, List<OrderStatusEnum> orderStatus) ;

   int update(LitemallOrder order);


   List<LitemallOrder> listAdminOrdersByAdminId(Integer adminId, Integer userId,
                                                               String orderSn, Integer page, Integer size, String sort, String order) ;

   List<LitemallOrder> listAdminOrdersByStatus(Integer adminId, List<OrderStatusEnum> orderStatus, Integer page, Integer size, String sort, String order);
    

   int countAdminOrdersByAdminId(Integer adminId, Integer userId, String orderSn) ;


   List<LitemallOrderWithGoods> querySelective(Integer userId, String orderSn, Integer page,
                                                     Integer size, String sort, String order) ;
    int countSelective(Integer userId, String orderSn, Integer page, Integer size, String sort,
                       String order);

   void updateById(LitemallOrder order);

   void deleteById(Integer id) ;

   int count() ;

   int countByAdminId(Integer adminId) ;

   List<LitemallOrder> queryUnpaid() ;

   List<LitemallOrder> queryUnconfirm() ;

   LitemallOrder findBySn(String orderSn) ;
}
