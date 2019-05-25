package com.attitude.tinymall.ao;

import com.attitude.tinymall.enums.OrderStatusEnum;
import lombok.Data;

/**
 * @author zhaoguiyang on 2019/5/25.
 * @project Wechat
 */
@Data
public class OrderStatusAO {

  private Integer orderId;

  private OrderStatusEnum orderStatus;

}
