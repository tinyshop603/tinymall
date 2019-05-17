package com.attitude.tinymall.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 订单和对应商品的实体类
 */
public class LitemallOrderWithGoods implements Serializable{

  /**
   * 订单信息
   */
  private LitemallOrder order;
  /**
   * 订单对应的商品数据
   */
  private List<com.attitude.tinymall.domain.LitemallOrderGoods> goods;


  public LitemallOrder getOrder() {
    return order;
  }

  public void setOrder(LitemallOrder order) {
    this.order = order;
  }

  public List<LitemallOrderGoods> getGoods() {
    return goods;
  }

  public void setGoods(List<LitemallOrderGoods> goods) {
    this.goods = goods;
  }
}
