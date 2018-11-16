package com.attitude.tinymall.os.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhaoguiyang on 2018/11/16.
 * @project Wechat
 */
@Slf4j
@Service
public class AliyunOssServiceImpl implements AliyunOssService {

  @Value("${oss.endpoint}")
  String endpoint;
  @Value("${oss.accessKeyId}")
  String accessKeyId;
  @Value("${oss.accessKeySecret}")
  String accessKeySecret;
  @Value("${oss.bucket}")
  String bucket;

  private OSS oss;

  @PostConstruct
  void init() {
    this.oss = new OSSClient(endpoint, accessKeyId, accessKeySecret);
  }

  @Override
  public String getFileUrl(String fileName) {
    try {
      log.info("generate signed url from oss, file: [${fileName}]");
      boolean isExist = oss.doesObjectExist(bucket, fileName);

      if (isExist) {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 5);
        return oss.generatePresignedUrl(bucket, fileName, nowTime.getTime()).toString();

      }
    } catch (Exception e) {
      log.error("generate signed url from oss error, file: [${fileName}]", e);
    }
    return null;
  }

  @Override
  public boolean uploadFile(String fileName, InputStream inputStream) {
    try {
      log.info("upload file to oss, file: [${fileName}], length: [${inputStream.available()}]");
      return oss.putObject(bucket, fileName, inputStream) == null;
    } catch (Exception e) {
      log.error(
          "upload file to oss error, file: [${fileName}], length: [${inputStream.available()}]", e);
    }
    return false;
  }

  @Override
  public InputStream downloadFileByName(String fileName) {
    try {
      log.info("download file from oss, file: [${fileName}]");
      return oss.getObject(bucket, fileName).getObjectContent();
    } catch (Exception e) {
      log.error("download file from oss error, file: [${fileName}]", e);
    }
    return null;
  }
}

