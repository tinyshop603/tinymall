package com.attitude.tinymall.core.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;
import com.attitude.tinymall.core.util.MD5Utils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.form.spring.SpringFormEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import springfox.documentation.spring.web.json.Json;
import feign.Request;
import feign.Retryer;

import static feign.Util.checkNotNull;

import feign.codec.Encoder;

/**
 * @author zhaoguiyang on 2019/5/9.
 * @project Wechat
 */
public class DadaDeliveryFeignConfig {

  @Bean
  AuthRequestInterceptor authRequestInterceptor(
      @Value("${delivery.dada.app-key}") String appKey,
      @Value("${delivery.dada.app-secret}") String appSecret,
      @Value("${delivery.dada.source-id}") String sourceId) {
    return new AuthRequestInterceptor(appKey, appSecret, sourceId);
  }

  @Bean
  Encoder feignFormEncoder() {
    final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(
        getFastJsonConverter());
    // 加入fastjson 的支持
    return new SpringFormEncoder(new SpringEncoder(() -> httpMessageConverters));
  }

  @Bean
  public Request.Options options() {
    int connectTimeOutMillis = 3000;
    int readTimeOutMillis = 9000;
    return new Request.Options(connectTimeOutMillis, readTimeOutMillis);
  }

  @Bean
  public Retryer feignRetryer(){
    return new Retryer.Default(100, 1000, 2);
  }


  private FastJsonHttpMessageConverter getFastJsonConverter() {
    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

    List<MediaType> supportedMediaTypes = new ArrayList<>();
    MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE);
    supportedMediaTypes.add(mediaTypeJson);
    converter.setSupportedMediaTypes(supportedMediaTypes);
    FastJsonConfig config = new FastJsonConfig();
    config.getSerializeConfig().put(Json.class, new SwaggerJsonSerializer());
    config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
    converter.setFastJsonConfig(config);

    return converter;
  }

  class AuthRequestInterceptor implements RequestInterceptor {

    private final String appKey;
    private final String appSecret;
    private final String sourceId;

    AuthRequestInterceptor(String appKey, String appSecret, String sourceId) {
      checkNotNull(appKey, "appKey");
      checkNotNull(appSecret, "appSecret");
      checkNotNull(sourceId, "sourceId");
      this.appKey = appKey;
      this.appSecret = appSecret;
      this.sourceId = sourceId;
    }


    @Override
    public void apply(RequestTemplate template) {
      // TODO 发请求的时候进行拦截, 加入head等验证信息

      Map<String, String> postBody = new HashMap<String, String>(8);
      postBody.put("format", "json");
      postBody.put("timestamp", String.valueOf(System.currentTimeMillis()));
      // 需要抽取到配置文件
      postBody.put("app_key", appKey);
      postBody.put("v", "1.0");
      // 需要抽取到配置文件 , 测试环境默认为：73753
      postBody.put("source_id", sourceId);
      postBody.put("body", new String(template.body(), Charset.forName("UTF-8")));
      postBody.put("signature", getSign(postBody, appSecret));

      template.body(JSON.toJSONString(postBody));

    }

    private String getSign(Map<String, String> requestParams, String appSecret) {
      //请求参数键值升序排序
      Map<String, String> sortedParams = new TreeMap<String, String>(requestParams);
      Set<Entry<String, String>> entrySets = sortedParams.entrySet();

      //拼参数字符串
      StringBuilder signStr = new StringBuilder();
      signStr.append(appSecret);
      entrySets.forEach(entry -> signStr.append(entry.getKey()).append(entry.getValue()));
      signStr.append(appSecret);
      //MD5签名并校验
      return MD5Utils.encode(signStr.toString()).toUpperCase();
    }




  }
}
