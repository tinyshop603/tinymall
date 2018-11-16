package com.attitude.tinymall.os.service;

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
}
