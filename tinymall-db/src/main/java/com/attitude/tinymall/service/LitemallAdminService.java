package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallAdmin;
import java.util.List;

public interface LitemallAdminService {

  LitemallAdmin findAdmin(String username);

  LitemallAdmin findAdminByOwnerId(String ownerId);


  List<LitemallAdmin> querySelective(String username, Integer page, Integer limit,
      String sort, String order);

  int countSelective(String username, Integer page, Integer size, String sort,
      String order);

  void updateById(LitemallAdmin admin);

  void deleteById(Integer id);

  void add(LitemallAdmin admin);

  LitemallAdmin findById(Integer id);

  LitemallAdmin findAllColunmById(Integer id);

  LitemallAdmin findByShopId(Integer shopId);

}
