package com.attitude.tinymall.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
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

  @Override
  public void serialize(String s, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    final String eleMeFlag = "elemecdn";
    if (StringUtils.isEmpty(s) || s.contains(eleMeFlag)) {
      jsonGenerator.writeString(s);
      return;
    }
    // 尝试通过内网oss获取阿里云的图片地址, 并兼容外网的地址格式
    String portStringComponent = StringUtils.isEmpty(ossPort) ? "" : ":" + ossPort;
    String defaultUrl = "//" + ossUrl + portStringComponent + "/os/storage/aliyun/" + s;
    String defaultQueryString = "?imageMogr2/thumbnail/300x300/format/webp/quality/85";
    jsonGenerator.writeString(defaultUrl + defaultQueryString);
  }
}
