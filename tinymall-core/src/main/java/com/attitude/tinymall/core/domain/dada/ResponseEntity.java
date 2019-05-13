package com.attitude.tinymall.core.domain.dada;

import lombok.Data;

/**
 * @author zhaoguiyang on 2019/5/9.
 * @project Wechat
 */
@Data
public class ResponseEntity<T> {

  public String status;
  public T result;
  public int code;
  public String msg;
  public Integer errorCode;

  public boolean isSuccess() {
    return "success".equalsIgnoreCase(this.status);
  }
}
