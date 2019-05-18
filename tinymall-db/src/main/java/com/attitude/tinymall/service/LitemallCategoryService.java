package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallCategory;
import java.util.List;

public interface LitemallCategoryService {


  String getAdminCategoryIdByUserName(String userName);

  String getAdminCategoryIdByAdminId(Integer adminId);

  /**
   * 获取店铺的所有大分类信息 此处进行了分页的行为
   */
  List<LitemallCategory> getAdminMallCategoryByAdminId(Integer adminId, Integer page,
      Integer limit, String sort, String order);

  /**
   * 获取店铺分类的总数
   */
  int countAdminMallCategoryByAdminId(Integer adminId, Integer page, Integer limit,
      String sort, String order);

  List<Integer> getAdminMallCategoryIdsByAdminId(Integer adminId);

  List<LitemallCategory> queryIdByPid(int pid);

  List<LitemallCategory> queryIdByPid(int pid, String level);

  List<LitemallCategory> queryL1WithoutRecommend(int offset, int limit);

  List<LitemallCategory> queryL1(int offset, int limit);

  List<LitemallCategory> queryL1();

  List<LitemallCategory> queryByPid(Integer pid);

  List<LitemallCategory> queryL2ByIds(List<Integer> ids);

  LitemallCategory findById(Integer id);

  List<LitemallCategory> querySelective(String id, String name, String parentId,
      Integer page, Integer size, String sort, String order);

  int countSelective(String id, String name, String parentId, Integer page, Integer size,
      String sort, String order);

  void updateById(LitemallCategory category);

  void deleteById(Integer id);

  void add(LitemallCategory category);


  List<LitemallCategory> queryChannel();
}
