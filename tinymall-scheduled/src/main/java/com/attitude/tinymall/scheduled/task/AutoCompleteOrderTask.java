package com.attitude.tinymall.scheduled.task;

import com.attitude.tinymall.db.domain.LitemallOrder;
import com.attitude.tinymall.db.domain.LitemallOrderGoods;
import com.attitude.tinymall.db.domain.LitemallProduct;
import com.attitude.tinymall.db.service.LitemallOrderGoodsService;
import com.attitude.tinymall.db.service.LitemallOrderService;
import com.attitude.tinymall.db.service.LitemallProductService;
import com.attitude.tinymall.db.util.OrderUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
@Slf4j
@Component
public class AutoCompleteOrderTask {

  @Autowired
  private PlatformTransactionManager txManager;

  @Autowired
  private LitemallOrderGoodsService orderGoodsService;
  @Autowired
  private LitemallOrderService orderService;
  @Autowired
  private LitemallProductService productService;

  /**
   * 自动取消订单
   *
   * 定时检查订单未付款情况，如果超时半个小时则自动取消订单 定时时间是每次相隔半个小时。
   *
   * 注意，因为是相隔半小时检查，因此导致有订单是超时一个小时以后才设置取消状态。 TODO 这里可以进一步地配合用户订单查询时订单未付款检查，如果订单超时半小时则取消。
   * 这里暂时取消自动检查订单的逻辑
   */
  @Scheduled(fixedDelay = 30 * 60 * 1000)
  public void checkOrderUnpaid() {
    log.debug(LocalDateTime.now().toString());

    List<LitemallOrder> orderList = orderService.queryUnpaid();
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
      TransactionStatus status = txManager.getTransaction(def);
      try {
        // 设置订单已取消状态
        order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
        order.setEndTime(LocalDateTime.now());
        orderService.updateById(order);

        // 商品货品数量增加
        Integer orderId = order.getId();
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
          Integer productId = orderGoods.getProductId();
          LitemallProduct product = productService.findById(productId);
          Integer number = product.getGoodsNumber() + orderGoods.getNumber();
          product.setGoodsNumber(number);
          productService.updateById(product);
        }
      } catch (Exception ex) {
        txManager.rollback(status);
        log.error("系统内部错误", ex);
      }
      txManager.commit(status);
    }
  }

  /**
   * 自动确认订单
   *
   * 定时检查订单未确认情况，如果超时七天则自动确认订单 定时时间是每天凌晨3点。
   *
   * 注意，因为是相隔一天检查，因此导致有订单是超时八天以后才设置自动确认。 这里可以进一步地配合用户订单查询时订单未确认检查，如果订单超时7天则自动确认。
   * 但是，这里可能不是非常必要。相比订单未付款检查中存在商品资源有限所以应该 早点清理未付款情况，这里八天再确认是可以的。
   *
   * TODO 目前自动确认是基于管理后台管理员所设置的商品快递到达时间，见orderService.queryUnconfirm。 那么在实际业务上有可能存在商品寄出以后商品因为一些原因快递最终没有到达，
   * 也就是商品快递失败而shipEndTime一直是空的情况，因此这里业务可能需要扩展，以防止订单一直 处于发货状态。
   */
  @Scheduled(cron = "0 0 3 * * ?")
  public void checkOrderUnconfirm() {
    log.debug(LocalDateTime.now().toString());

    List<LitemallOrder> orderList = orderService.queryUnconfirm();
    for (LitemallOrder order : orderList) {
      LocalDateTime shipEnd = order.getShipEndTime();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expired = shipEnd.plusDays(7);
      if (expired.isAfter(now)) {
        continue;
      }
      // 设置订单已取消状态
      if (order.getOrderStatus() == 301) {
        order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
      } else {
        order.setOrderStatus(OrderUtil.STATUS_AFTER_AUTO_CONFIRM);
      }
      order.setConfirmTime(now);
      orderService.updateById(order);
    }
  }

}
