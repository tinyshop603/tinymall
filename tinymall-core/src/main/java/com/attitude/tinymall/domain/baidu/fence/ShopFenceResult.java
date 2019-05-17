package com.attitude.tinymall.domain.baidu.fence;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoguiyang on 2019/1/13.
 * @project Wechat
 */
@NoArgsConstructor
@Data
public class ShopFenceResult {

  /**
   * status : 0
   * message : 成功
   * fence_id : 5
   */

  private int status;
  private String message;
  @JSONField(name = "fence_id")
  private int fenceId;
}
