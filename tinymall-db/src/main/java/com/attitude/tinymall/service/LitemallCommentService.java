package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallComment;
import java.util.List;

public interface LitemallCommentService {


  List<LitemallComment> queryGoodsByGid(Integer id, int offset, int limit);

  int countGoodsByGid(Integer id, int offset, int limit);

  List<LitemallComment> query(Byte typeId, Integer valueId, Integer showType, Integer offset,
      Integer limit);

  int count(Byte typeId, Integer valueId, Integer showType, Integer offset, Integer size);

  Integer save(LitemallComment comment);


  void update(LitemallComment comment);


  List<LitemallComment> listAdminCommentsByAdminId(Integer adminId, String userId,
      String valueId, Integer page, Integer size, String sort, String order);

  int countAdminCommentsByAdminId(Integer adminId, String userId, String valueId);


  List<LitemallComment> querySelective(String userId, String valueId, Integer page,
      Integer size, String sort, String order);

  int countSelective(String userId, String valueId, Integer page, Integer size, String sort,
      String order);

  void updateById(LitemallComment comment);

  void deleteById(Integer id);

  void add(LitemallComment comment);

  LitemallComment findById(Integer id);
}
