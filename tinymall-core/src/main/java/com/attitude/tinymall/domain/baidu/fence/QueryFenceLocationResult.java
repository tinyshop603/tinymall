package com.attitude.tinymall.domain.baidu.fence;

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
public class QueryFenceLocationResult {

  public static final String LOCATION_IN = "in";
  public static final String LOCATION_OUT = "out";

  /**
   * status : 0
   * message : 成功
   * size : 1
   * monitored_statuses : [{"fence_id":5,"monitored_status":"out"}]
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
     * fence_id : 5
     * monitored_status : out
     */
    @JSONField(name = "fence_id")
    private int fenceId;
    @JSONField(name = "monitored_status")
    private String monitoredStatus;
  }
}
