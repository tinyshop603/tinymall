package com.attitude.tinymall.VO;

import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.LitemallOrder;
import lombok.Data;

/**
 * @author zhaoguiyang on 2019/5/25.
 * @project Wechat
 */
@Data
public class OrderVO {

  private LitemallOrder order;
  private LitemallDeliveryDetail deliveryDetail;
}
