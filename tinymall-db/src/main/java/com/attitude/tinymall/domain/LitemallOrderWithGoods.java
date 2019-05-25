package com.attitude.tinymall.domain;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 订单和对应商品的实体类
 */
@Data
public class LitemallOrderWithGoods implements Serializable{
  /**
   * 订单信息
   */
  private LitemallOrder order;
  /**
   * 订单对应的商品数据
   */
  private List<com.attitude.tinymall.domain.LitemallOrderGoods> goods;

  private LitemallDeliveryDetail deliveryDetail;

}
