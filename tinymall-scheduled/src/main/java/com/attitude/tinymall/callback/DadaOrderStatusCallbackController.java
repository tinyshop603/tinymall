package com.attitude.tinymall.callback;

import com.attitude.tinymall.callback.params.DadaCallbackParams;
import com.attitude.tinymall.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
@RestController
@RequestMapping("dada-order/")
@Slf4j
public class DadaOrderStatusCallbackController {

  @PostMapping("callback/status")
  public Object callBackOrderStatus(@RequestBody DadaCallbackParams callbackParams){
    // TODO singure feature的校验, 以及订单状态的变更
    log.info(callbackParams.toString());
    return ResponseUtil.ok();
  }
}
