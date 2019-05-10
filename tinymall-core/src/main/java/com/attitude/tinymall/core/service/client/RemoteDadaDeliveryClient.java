package com.attitude.tinymall.core.service.client;

import com.attitude.tinymall.core.config.DadaDeliveryFeignConfig;
import com.attitude.tinymall.core.domain.dada.AddOrderParams;
import com.attitude.tinymall.core.domain.dada.AddOrderResult;
import com.attitude.tinymall.core.domain.dada.ResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhaoguiyang on 2019/5/8.
 * @project Wechat
 */
@FeignClient(name = "dada-delivery", url = "${delivery.dada.host}", configuration = DadaDeliveryFeignConfig.class)
public interface RemoteDadaDeliveryClient {

  @PostMapping("/api/order/addOrder")
  ResponseEntity<AddOrderResult> addOrder(@RequestBody AddOrderParams params);

}
