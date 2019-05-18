package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallCollect;
import java.util.List;

public interface LitemallCollectService {

  int count(int uid, Integer gid);

  List<LitemallCollect> queryByType(Integer userId, Integer typeId, Integer page,
      Integer size);

  int countByType(Integer userId, Integer typeId);

  LitemallCollect queryByTypeAndValue(Integer userId, Integer typeId, Integer valueId);

  void deleteById(Integer id);

  int add(LitemallCollect collect);

  List<LitemallCollect> querySelective(String userId, String valueId, Integer page,
      Integer size, String sort, String order);

  int countSelective(String userId, String valueId, Integer page, Integer size, String sort,
      String order);

  void updateById(LitemallCollect collect);

  LitemallCollect findById(Integer id);
}
