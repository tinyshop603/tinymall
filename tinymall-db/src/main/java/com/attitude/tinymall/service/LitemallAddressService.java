package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallAdMapper;
import com.attitude.tinymall.dao.LitemallAdminMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallAddressMapper;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallAddressExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallAddressService {


  List<LitemallAddress> queryByUid(Integer uid);

  LitemallAddress findById(Integer id);

  int add(LitemallAddress address, String appId);

  int update(LitemallAddress address);

  void delete(Integer id);

  LitemallAddress findDefault(Integer userId);

  void resetDefault(Integer userId);

  List<LitemallAddress> listAdminAddressByAdminId(Integer adminId, Integer userId,
      String name, Integer page,
      Integer limit, String sort, String order);

  int countAdminAddressByAdminId(Integer adminId, Integer userId, String name);

  List<LitemallAddress> querySelective(Integer userId, String name, Integer page,
      Integer limit, String sort, String order);

  int countSelective(Integer userId, String name, Integer page, Integer limit, String sort,
      String order);

  void updateById(LitemallAddress address);

  String getFullDetailAddress(LitemallAddress address);

}
