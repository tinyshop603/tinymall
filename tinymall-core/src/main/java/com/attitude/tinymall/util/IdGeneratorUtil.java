package com.attitude.tinymall.util;

import java.util.Locale;
import java.util.Random;

/**
 * @author zhaoguiyang on 2019/5/18.
 * @project Wechat
 */
public class IdGeneratorUtil {

  static Random random = new Random();

  public static String generateId(String prefix) {
    return String
        .format(Locale.US, prefix + "%d%04d", System.currentTimeMillis(), random.nextInt(10000));
  }
}
