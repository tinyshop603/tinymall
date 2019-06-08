package com.attitude.tinymall.service;

import com.attitude.tinymall.vo.LocationVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhaoguiyang on 2019/6/8.
 * @project Wechat
 */
public interface IUserAddressService {

  /**
   *
   * @param lng 当前用户位置gcj02
   * @param lat 当前用户位置gcj02
   * @param keyword 搜索的关键字
   * @param appId 微信的appId
   * @return
   */
  LocationVO getLocationDetailByGeoParams(
      String lng,
      String lat,
      String keyword,
      String appId);
}
