package com.attitude.tinymall.task;

import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.enums.OrderStatusEnum;
import com.attitude.tinymall.service.LitemallOrderService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
@Slf4j
@Component
public class AutoCompleteOrderTask {


  @Autowired
  private LitemallOrderService orderService;


  /**
   * 自动取消订单
   *
   * 定时检查订单未付款情况，如果超时半个小时则自动取消订单 定时时间是每次相隔半个小时。
   *
   * 注意，因为是相隔半小时检查，因此导致有订单是超时一个小时以后才设置取消状态。 TODO 这里可以进一步地配合用户订单查询时订单未付款检查，如果订单超时半小时则取消。
   * 这里暂时取消自动检查订单的逻辑
   */
  @Scheduled(fixedDelay = 30 * 60 * 1000)
  @Transactional(rollbackFor = Exception.class)
  public void checkOrderUnpaid() {
    log.info(LocalDateTime.now().toString());

    List<LitemallOrder> orderList = orderService.queryUnPaid();
    for (LitemallOrder order : orderList) {
      LocalDateTime add = order.getAddTime();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expired = add.plusMinutes(30);
      if (expired.isAfter(now)) {
        continue;
      }

      // 开启事务管理
      DefaultTransactionDefinition def = new DefaultTransactionDefinition();
      def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
      // 设置订单已取消状态
      order.setOrderStatus(OrderStatusEnum.SYSTEM_AUTO_CANCEL);
      order.setEndTime(LocalDateTime.now());
      orderService.updateById(order);

      // 商品货品数量增加
      orderService.refundOrderGoodsByOrderId(order.getId());

    }
  }

  /**
   * 自动确认订单
   *
   * 定时检查订单未确认情况，如果超时七天则自动确认订单 定时时间是每天凌晨3点。
   */
  @Scheduled(cron = "0 0 3 * * ?")
  public void checkOrderUnconfirm() {
    log.info(LocalDateTime.now().toString());
    List<LitemallOrder> orderList = orderService.queryUnConfirm();
    for (LitemallOrder order : orderList) {
      LocalDateTime shipEnd = order.getShipEndTime();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expired = shipEnd.plusHours(2);
      if (expired.isAfter(now)) {
        continue;
      }
      order.setOrderStatus(OrderStatusEnum.SYSTEM_AUTO_COMPLETE);
      order.setConfirmTime(now);
      orderService.updateById(order);
    }
  }

}
