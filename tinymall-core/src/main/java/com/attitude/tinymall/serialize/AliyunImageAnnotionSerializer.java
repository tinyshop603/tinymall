package com.attitude.tinymall.serialize;

import com.attitude.tinymall.service.AliyunOssService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author zhaoguiyang on 2018/12/27.
 * @project Wechat
 */
@Component
public class AliyunImageAnnotionSerializer extends JsonSerializer<String> {

  /**
   * 内网中一定要使用内网的ip
   */
  @Value("${com.attitude.tinymall.os.inner.url}")
  String ossUrl;

  @Value("${com.attitude.tinymall.os.inner.port}")
  String ossPort;

  @Autowired
  private AliyunOssService aliyunOssService;

  @Override
  public void serialize(String s, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    final String eleMeFlag = "elemecdn";
    if (StringUtils.isEmpty(s) || s.contains(eleMeFlag)) {
      jsonGenerator.writeString(s);
      return;
    }
    jsonGenerator.writeString(aliyunOssService.getFilePublicUrl(s, true));
  }
}
