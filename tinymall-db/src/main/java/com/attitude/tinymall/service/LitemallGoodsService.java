package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallGoodsExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.domain.LitemallGoods;
import com.attitude.tinymall.domain.LitemallGoods.Column;
import com.attitude.tinymall.dao.LitemallGoodsMapper;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public interface LitemallGoodsService {

   List<LitemallGoods> queryByHot(int offset, int limit) ;

   List<LitemallGoods> queryByNew(int offset, int limit) ;

   List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit);

   int countByCategory(List<Integer> catList, int offset, int limit) ;

   List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit);

   int countByCategory(Integer catId, Integer page, Integer size) ;

  //wz-增加categoryIds字段以适应一次获取多区域商品
   List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keyword,
      Integer isHot, Integer isNew, Integer offset, Integer limit, String sort,
      List<Integer> categoryIds) ;

   int countSelective(Integer catId, Integer brandId, String keyword, Integer isHot,
      Integer isNew, Integer offset, Integer limit, String sort) ;

   LitemallGoods findById(Integer id) ;

    int countSelective(String goodsSn, String name, String categoryId);

   List<LitemallGoods> queryByIds(List<Integer> relatedGoodsIds);

   Integer queryOnSale(List<Integer> categoryId) ;

   List<LitemallGoods> listGoodsByAdminId(Integer adminId, String goodsSn, String name,
      String categoryId, Integer page, Integer size, String sort, String order) ;

   int countGoodsByAdminId(Integer adminId, String goodsSn, String name, String categoryId) ;

   List<LitemallGoods> querySelective(String goodsSn, String name, String categoryId,
      Integer page, Integer size, String sort, String order) ;

   void updateById(LitemallGoods goods);

   void deleteById(Integer id);

   int add(LitemallGoods goods) ;

   int count() ;

   int countByAdminId(Integer adminId) ;

   List<Integer> getCatIds(Integer brandId, String keyword, Integer isHot, Integer isNew) ;

   List<Integer> getAdminGoodsIds(Integer adminId) ;
}
