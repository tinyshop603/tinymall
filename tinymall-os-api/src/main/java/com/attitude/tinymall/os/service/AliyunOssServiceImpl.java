package com.attitude.tinymall.os.service;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.attitude.tinymall.core.util.ResponseUtil;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName);
    return getFileUrlByProcessRequest(fileName, request);
  }

  @Override
  public boolean uploadFile(String fileName, InputStream inputStream) {
    try {
      log.info("upload file to oss, file: {}, length: {} ", fileName, inputStream.available());
      return oss.putObject(bucket, fileName, inputStream) != null;
    } catch (Exception e) {
      log.error(
          "upload file to oss error, file: {}, detail: {}", fileName, e.getMessage());
    }
    return false;
  }

  @Override
  public InputStream downloadFileByName(String fileName) {
    try {
      log.info("download file from oss, file: {}", fileName);
      return oss.getObject(bucket, fileName).getObjectContent();
    } catch (Exception e) {
      log.error("download file from oss error, file: {}, detail: {}", fileName, e.getMessage());
    }
    return null;
  }


  @Override
  public InputStream downloadGeometricScalingFileByName(String fileName, int with, int height) {
    try {
      log.info("download file from oss, file: {}", fileName);
      GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, fileName);
      if (with == -1 || height == -1){
        getObjectRequest.setProcess("image/resize,p_100");
      }else {
        getObjectRequest.setProcess("image/resize,m_fixed,w_" + with + ",h_" + height);
      }
      return oss.getObject(getObjectRequest).getObjectContent();
    } catch (Exception e) {
      log.error("download file from oss error, file: {}, detail: {}", fileName, e.getMessage());
    }
    return null;
  }

  @Override
  public String getGeometricScalingFileUrl(String fileName, int scale) {
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName);
    request.setProcess("image/resize,p_" + scale);
    return getFileUrlByProcessRequest(fileName, request);
  }

  @Override
  public String getForeSizeFileUrl(String fileName, int with, int height) {
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName);
    request.setProcess("image/resize,m_fixed,w_" + with + ",h_" + height);
    return getFileUrlByProcessRequest(fileName, request);
  }

  @Override
  public ByteArrayOutputStream getOutputByQueryString(String fileName, String queryString) {
    // 兼容eleme抓取的图片格式, quality 在本api中没有作用, 但是后期可能有用, 则暂时必传
    //fuss10.elemecdn.com/5/90/06a0dfdbf59c3d27c8df43203207ejpeg.jpeg?imageMogr2/thumbnail/100x100/format/webp/quality/85
    // 逾期size格式为100X100
    String size = null;
    int with = -1;
    int height = -1;
    if (!StringUtils.isEmpty(queryString)) {
      String[] params = queryString.split("/");
      final int paramSize = 7;
      if (params.length == paramSize) {
        size = params[2];
      }
    }

    if (size != null) {
      boolean isValidSize = size.contains("x") && size.split("x").length == 2 &&
          (StringUtils.isNumeric(size.split("x")[0]) && StringUtils.isNumeric(size.split("x")[0]));
      if (isValidSize) {
        with = Integer.valueOf(size.split("x")[0]);
        height = Integer.valueOf(size.split("x")[1]);
      }
    }

    try {
      InputStream inputStream = downloadGeometricScalingFileByName(fileName, with, height);
      ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
      int ch;
      while ((ch = inputStream.read()) != -1) {
        swapStream.write(ch);
      }
      return swapStream;
    }catch (Exception e){
      log.error(e.getMessage());
    }
    return null;
  }

  private String getFileUrlByProcessRequest(String fileName, GeneratePresignedUrlRequest request) {
    try {
      log.info("generate signed url from oss, file: {}", fileName);
      boolean isExist = oss.doesObjectExist(bucket, fileName);

      if (isExist) {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.YEAR, Integer.MAX_VALUE);
        request.setExpiration(nowTime.getTime());
        return oss.generatePresignedUrl(request)
            .toString();

      }
    } catch (Exception e) {
      log.error("generate signed url from oss error, file: {}, detail: {}", fileName,
          e.getMessage());
    }
    return null;
  }
}

