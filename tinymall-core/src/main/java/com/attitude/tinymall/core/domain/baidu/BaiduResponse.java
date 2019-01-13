package com.attitude.tinymall.core.domain.baidu;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaoguiyang on 2019/1/11.
 * @project Wechat
 */
@NoArgsConstructor
@Data
public class BaiduResponse<T> {

  private int status;
  private String message;
  private T result;
}
