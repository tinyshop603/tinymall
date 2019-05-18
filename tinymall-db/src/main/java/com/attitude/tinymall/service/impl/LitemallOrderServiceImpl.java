package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallAdminMapper;
import com.attitude.tinymall.dao.LitemallOrderMapper;
import com.attitude.tinymall.dao.manual.LitemallOrderManualMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderExample;
import com.attitude.tinymall.domain.LitemallOrderWithGoods;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.service.LitemallOrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallOrderServiceImpl implements LitemallOrderService {

  @Autowired
  private LitemallOrderMapper orderMapper;
  @Autowired
  private LitemallAdminMapper adminMapper;
  @Autowired
  private LitemallAdminServiceImpl adminService;
  @Autowired
  private LitemallOrderManualMapper orderManualMapper;


  @Override
  public int add(LitemallOrder order, String appId) {
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    if (null != litemallAdmin) {
      // 关联外键
      order.setAdminId(litemallAdmin.getId());
    }
    return orderMapper.insertSelective(order);
  }
  @Override
  public List<LitemallOrder> query(Integer userId, String appId) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
            .andAdminIdEqualTo(litemallAdmin.getId());
    return orderMapper.selectByExample(example);
  }
  @Override
  public int count(Integer userId) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }
  @Override
  public LitemallOrder findById(Integer orderId) {
    return orderMapper.selectByPrimaryKey(orderId);
  }
  @Override
  public String getRandomNum(Integer num) {
    String base = "0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < num; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }
  @Override
  public LitemallOrder queryByOrderSn(Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }
  @Override
  public int countByOrderSn(Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
  @Override
  public String generateOrderSn(Integer userId) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
    String now = df.format(LocalDate.now());
    String orderSn = now + getRandomNum(6);
    while (countByOrderSn(userId, orderSn) != 0) {
      orderSn = getRandomNum(6);
    }
    return orderSn;
  }
  @Override
  public List<LitemallOrder> queryByOrderStatus(Integer userId, List<OrderStatusEnum> orderStatus) {
    LitemallOrderExample example = new LitemallOrderExample();
    OrderStatusEnum currentOrderStatus = orderStatus.get(0);
    //未完成 按结束时间排序
    if (currentOrderStatus == OrderStatusEnum.ONGOING) {
      example.orderBy(LitemallOrder.Column.endTime.desc());
    } else if (currentOrderStatus == OrderStatusEnum.COMPLETE) {//已完成 按完成时间排序
      example.orderBy(LitemallOrder.Column.confirmTime.desc());
    } else {//其他，按添加时间排序
      example.orderBy(LitemallOrder.Column.addTime.desc());
    }

    LitemallOrderExample.Criteria criteria = example.or();
    criteria.andUserIdEqualTo(userId);
    if (orderStatus != null) {
      criteria.andOrderStatusIn(orderStatus);
    }
    criteria.andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }
  @Override
  public int countByOrderStatus(Integer userId, List<OrderStatusEnum> orderStatus) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.or();
    criteria.andUserIdEqualTo(userId);
    if (orderStatus != null) {
      criteria.andOrderStatusIn(orderStatus);
    }
    criteria.andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }
  @Override
  public int update(LitemallOrder order) {
    return orderMapper.updateByPrimaryKeySelective(order);
  }

  @Override
  public List<LitemallOrder> listAdminOrdersByAdminId(Integer adminId, Integer userId,
                                                               String orderSn, Integer page, Integer size, String sort, String order) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.orderBy("add_time DESC");
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);
    List<OrderStatusEnum> unshowOrderStatus = new ArrayList<OrderStatusEnum>();
    //订单生成，未支付；
    unshowOrderStatus.add(OrderStatusEnum.PENDING_PAYMENT);
    //下单后未支付用户取消；
    unshowOrderStatus.add(OrderStatusEnum.CUSTOMER_CANCEL);
    //下单后未支付超时系统自动取消
    unshowOrderStatus.add(OrderStatusEnum.SYSTEM_AUTO_CANCEL);
    criteria.andOrderStatusNotIn(unshowOrderStatus);

    Page<Object> objects = PageHelper.startPage(page, size);
    return orderMapper.selectByExample(example);
  }
  @Override
  public List<LitemallOrder> listAdminOrdersByStatus(Integer adminId, List<OrderStatusEnum> orderStatus, Integer page, Integer size, String sort, String order) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.orderBy("add_time DESC");
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    criteria.andOrderStatusIn(orderStatus);
    Page<Object> objects = PageHelper.startPage(page, size);
    return orderMapper.selectByExample(example);
  }
  @Override
  public int countAdminOrdersByAdminId(Integer adminId, Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);

    return (int) orderMapper.countByExample(example);
  }

  @Override
  public List<LitemallOrderWithGoods> querySelective(Integer userId, String orderSn, Integer page,
                                                     Integer size, String sort, String order) {
    // TODO 为啥这个接口暂时没有用到
    LitemallOrderExample example = new LitemallOrderExample();
    example.orderBy("add_time DESC");
    LitemallOrderExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);

    Page<Object> objects = PageHelper.startPage(page, size);
    List<LitemallOrderWithGoods> odersWithGoods = orderManualMapper.selectOdersWithGoodsByAdminId(1);

    return odersWithGoods;
  }
  @Override
  public int countSelective(Integer userId, String orderSn, Integer page, Integer size, String sort,
                            String order) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(orderSn)) {
      criteria.andOrderSnEqualTo(orderSn);
    }
    criteria.andDeletedEqualTo(false);

    return (int) orderMapper.countByExample(example);
  }
  @Override
  public void updateById(LitemallOrder order) {
    orderMapper.updateByPrimaryKeySelective(order);
  }
  @Override
  public void deleteById(Integer id) {
    orderMapper.logicalDeleteByPrimaryKey(id);
  }
  @Override
  public int count() {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public int countByAdminId(Integer adminId) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  @Override
  public List<LitemallOrder> queryUnpaid() {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderStatusEqualTo(OrderStatusEnum.PENDING_PAYMENT).andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }
  @Override
  public List<LitemallOrder> queryUnconfirm() {
    LitemallOrderExample example = new LitemallOrderExample();
    List<OrderStatusEnum> unconfirmIds = new ArrayList<OrderStatusEnum>();
    unconfirmIds.add(OrderStatusEnum.MERCHANT_SHIP);
    example.or().andOrderStatusIn(unconfirmIds).andShipEndTimeIsNotNull()
            .andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }
  @Override
  public LitemallOrder findBySn(String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }
}
