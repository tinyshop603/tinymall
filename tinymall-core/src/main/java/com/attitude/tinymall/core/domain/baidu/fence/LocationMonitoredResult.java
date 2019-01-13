package com.attitude.tinymall.core.domain.baidu.fence;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoguiyang on 2019/1/13.
 * @project Wechat
 */
@NoArgsConstructor
@Data
public class LocationMonitoredResult {

  /**
   * status : 0
   * message : 成功
   * size : 1
   * monitored_statuses : [{"fence_id":2,"monitored_status":"in"}]
   */

  private int status;
  private String message;
  private int size;
  @JSONField(name = "monitored_statuses")
  private List<MonitoredStatuses> monitoredStatuses;

  @NoArgsConstructor
  @Data
  public static class MonitoredStatuses {

    /**
     * fence_id : 2
     * monitored_status : in
     */
    @JSONField(name = "fence_id")
    private int fenceId;
    @JSONField(name = "monitored_status")
    private String monitoredStatus;
  }
}
