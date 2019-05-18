package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallCart;
import java.util.List;

public interface LitemallCartService {


  LitemallCart queryExist(Integer goodsId, Integer productId, Integer userId);

  void add(LitemallCart cart);

  void update(LitemallCart cart);

  List<LitemallCart> queryByUid(int userId);


  List<LitemallCart> queryByUidAndChecked(Integer userId);

  List<LitemallCart> queryByUidAndSid(int userId, String sessionId);

  int delete(List<Integer> productIdList, int userId);

  LitemallCart findById(Integer id);

  int updateCheck(Integer userId, List<Integer> idsList, Boolean checked);

  void clearGoods(Integer userId);


  List<LitemallCart> listAdminCartsByAdminId(Integer adminId, Integer userId,
      Integer goodsId,
      Integer page, Integer limit, String sort, String order);

  int countAdminCartByAdminId(Integer adminId, Integer userId, Integer goodsId);

  List<LitemallCart> querySelective(Integer userId, Integer goodsId, Integer page,
      Integer limit, String sort, String order);

  int countSelective(Integer userId, Integer goodsId, Integer page, Integer limit,
      String sort, String order);

  void deleteById(Integer id);
}
