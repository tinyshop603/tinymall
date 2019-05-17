package com.attitude.tinymall.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author zhaoguiyang on 2018/11/16.
 * @project Wechat
 */
public interface AliyunOssService {

  /**
   * 获取文件url
   *
   * @param fileName 文件名
   * @return 字符串值：文件url
   */
  String getFileUrl(String fileName);

  /**
   * 上传文件
   *
   * @param fileName 文件名
   * @param inputStream 文件输入流
   * @return boolean值，代表上传是否成功
   */
  boolean uploadFile(String fileName, InputStream inputStream);

  /**
   * 下载文件
   *
   * @param fileName 文件名称
   * @return 文件流
   */
  InputStream downloadFileByName(String fileName);

  InputStream downloadGeometricScalingFileByName(String fileName,  int with, int height);

  /**
   * 获得等比缩放的图片
   * @param fileName
   * @param scale 1到100, 100为原图, 超过100显示原图, 50 为原图的一半
   * @return
   */
  String getGeometricScalingFileUrl(String fileName, int scale);

  /**
   * 获取强制的宽和高的缩略图
   * @param fileName
   * @param with 固定宽度
   * @param height 固定高度
   * @return
   */
  String getForeSizeFileUrl(String fileName, int with, int height);

  ByteArrayOutputStream getOutputByQueryString(String fileName,String queryString);
}
