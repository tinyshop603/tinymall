package com.attitude.tinymall.core.serialize;

import com.alibaba.fastjson.JSON;
import com.attitude.tinymall.core.annotion.AliyunImage;
import com.attitude.tinymall.core.util.HttpClientUtil;
import com.attitude.tinymall.core.util.HttpClientUtil.HttpClientResult;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.util.Objects;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author zhaoguiyang on 2018/12/27.
 * @project Wechat
 */
@Component
@Log4j
public class AliyunImageAnnotionSerializer extends JsonSerializer<String> {

  /**
   * 内网中一定要使用内网的ip
   */
  @Value("${com.attitude.tinymall.os.inner.url}")
  String ossUrl;

  @Value("${com.attitude.tinymall.os.inner.port}")
  String ossPort;


  @Override
  public void serialize(String s, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    if (StringUtils.isEmpty(s)) {
      jsonGenerator.writeString(s);
      return;
    }
    // 尝试获取阿里云的图片地址
    try {
      HttpClientResult httpClientResult = HttpClientUtil
          .doGet("http://" + ossUrl + ":" + ossPort + "/os/storage/aliyun/" + s);
      final int responseCodeOk = 200;
      if (httpClientResult.getCode() == responseCodeOk && httpClientResult.getContent() != null) {
        DataContent content = JSON.parseObject(httpClientResult.getContent(), DataContent.class);
        if (content != null) {
          jsonGenerator.writeString(content.data);
          return;
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    jsonGenerator.writeString(s);

  }

  @Data
  static class DataContent {

    String errno;
    String data;
    String errmsg;
  }
}
