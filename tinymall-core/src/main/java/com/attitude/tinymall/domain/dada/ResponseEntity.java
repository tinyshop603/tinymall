package com.attitude.tinymall.domain.dada;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhaoguiyang on 2019/5/9.
 * @project Wechat
 */
@Data
@ToString
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
