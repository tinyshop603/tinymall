package com.attitude.tinymall.wx.service.client;

import static feign.Util.checkNotNull;

import com.attitude.tinymall.wx.service.client.RemoteDeliveryClient.FeignConfiguration;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhaoguiyang on 2019/5/8.
 * @project Wechat
 */
@FeignClient(name = "fake-delivery", url = "https://www.baidu.com/", configuration = FeignConfiguration.class)
public interface RemoteDeliveryClient {

  @GetMapping
  String testBaidu();


  class AuthRequestInterceptor implements RequestInterceptor {

    private final String accessKey;
    private final String secretKey;

    AuthRequestInterceptor(String accessKey, String secretKey) {
      checkNotNull(accessKey, "accessKey");
      checkNotNull(secretKey, "secretKey");
      this.accessKey = accessKey;
      this.secretKey = secretKey;
    }


    @Override
    public void apply(RequestTemplate template) {
      // TODO 发请求的时候进行拦截, 加入head等验证信息
    }
  }

  class FeignConfiguration {

    @Bean
    static AuthRequestInterceptor authRequestInterceptor(
        @Value("") String accessKey,
        @Value("") String secretKey
    ) {
      return new AuthRequestInterceptor(accessKey, secretKey);
    }

    @Bean
    static Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
      return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
  }
}
