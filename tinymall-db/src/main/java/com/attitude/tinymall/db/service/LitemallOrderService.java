package com.attitude.tinymall.db.service;

import com.attitude.tinymall.db.dao.LitemallAdminMapper;
import com.attitude.tinymall.db.service.LitemallAdminService;
import com.attitude.tinymall.db.domain.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.db.dao.LitemallOrderMapper;
import com.attitude.tinymall.db.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class LitemallOrderService {

  @Resource
  private LitemallOrderMapper orderMapper;
  @Resource
  private LitemallAdminMapper adminMapper;
  @Resource
  private LitemallAdminService adminService;


  public int add(LitemallOrder order, String appId) {
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    if (null != litemallAdmin) {
      // 关联外键
      order.setAdminId(litemallAdmin.getId());
    }
    return orderMapper.insertSelective(order);
  }

  public List<LitemallOrder> query(Integer userId, String appId) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false)
            .andAdminIdEqualTo(litemallAdmin.getId());
    return orderMapper.selectByExample(example);
  }

  public int count(Integer userId) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  public LitemallOrder findById(Integer orderId) {
    return orderMapper.selectByPrimaryKey(orderId);
  }

  private String getRandomNum(Integer num) {
    String base = "0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < num; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  public LitemallOrder queryByOrderSn(Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }

  public int countByOrderSn(Integer userId, String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
  public String generateOrderSn(Integer userId) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
    String now = df.format(LocalDate.now());
    String orderSn = now + getRandomNum(6);
    while (countByOrderSn(userId, orderSn) != 0) {
      orderSn = getRandomNum(6);
    }
    return orderSn;
  }

  public List<LitemallOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus) {
    LitemallOrderExample example = new LitemallOrderExample();
    //未完成 按结束时间排序
    if(orderStatus.get(0)==102 || orderStatus.get(0)==2 || orderStatus.get(0)==203){
      example.orderBy(LitemallOrder.Column.endTime.desc());
    }else if(orderStatus.get(0)==401 || orderStatus.get(0)==4 || orderStatus.get(0)==5){//已完成 按完成时间排序
      example.orderBy(LitemallOrder.Column.confirmTime.desc());
    }else {//其他，按添加时间排序
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

  public int countByOrderStatus(Integer userId, List<Short> orderStatus) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.or();
    criteria.andUserIdEqualTo(userId);
    if (orderStatus != null) {
      criteria.andOrderStatusIn(orderStatus);
    }
    criteria.andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }

  public int update(LitemallOrder order) {
    return orderMapper.updateByPrimaryKeySelective(order);
  }


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
    List<Short> unshowOrderStatus = new ArrayList<Short>();
    unshowOrderStatus.add((short)101);//订单生成，未支付；
    unshowOrderStatus.add((short)102);//下单后未支付用户取消；
    unshowOrderStatus.add((short)103);//下单后未支付超时系统自动取消
//    unshowOrderStatus.add((short)2);//货到付款用户取消订单
    criteria.andOrderStatusNotIn(unshowOrderStatus);

    Page<Object> objects = PageHelper.startPage(page, size);
    return orderMapper.selectByExample(example);
  }

  public List<LitemallOrder> listAdminOrdersByStatus(Integer adminId, List<Short> orderStatus, Integer page, Integer size, String sort, String order) {
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


  public List<LitemallOrderWithGoods> querySelective(Integer userId, String orderSn, Integer page,
                                                     Integer size, String sort, String order) {
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
    List<LitemallOrderWithGoods> odersWithGoods = orderMapper.selectOdersWithGoods(0);

    return odersWithGoods;
  }

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

  public void updateById(LitemallOrder order) {
    orderMapper.updateByPrimaryKeySelective(order);
  }

  public void deleteById(Integer id) {
    orderMapper.logicalDeleteByPrimaryKey(id);
  }

  public int count() {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }


  public int countByAdminId(Integer adminId) {
    LitemallOrderExample example = new LitemallOrderExample();
    LitemallOrderExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    return (int) orderMapper.countByExample(example);
  }


  public List<LitemallOrder> queryUnpaid() {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }

  public List<LitemallOrder> queryUnconfirm() {
    LitemallOrderExample example = new LitemallOrderExample();
    List<Short> unconfirmIds = new ArrayList<Short>();
    unconfirmIds.add(OrderUtil.STATUS_SHIP);
    unconfirmIds.add(OrderUtil.STATUS_AFTER_SHIP);
    example.or().andOrderStatusIn(unconfirmIds).andShipEndTimeIsNotNull()
            .andDeletedEqualTo(false);
    return orderMapper.selectByExample(example);
  }

  public LitemallOrder findBySn(String orderSn) {
    LitemallOrderExample example = new LitemallOrderExample();
    example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
    return orderMapper.selectOneByExample(example);
  }
}
