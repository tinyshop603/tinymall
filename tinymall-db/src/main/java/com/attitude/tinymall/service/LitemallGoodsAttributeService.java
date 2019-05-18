package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallGoodsAttributeMapper;
import com.attitude.tinymall.domain.LitemallGoodsAttribute;
import com.attitude.tinymall.domain.LitemallGoodsAttributeExample;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallGoodsAttributeService {


   List<LitemallGoodsAttribute> queryByGid(Integer goodsId) ;

   List<LitemallGoodsAttribute> querySelective(Integer goodsId, Integer adminId, Integer page,
      Integer size, String sort, String order) ;

   int countSelective(Integer goodsId, Integer adminId, Integer page, Integer size,
      String sort, String order) ;

   void updateById(LitemallGoodsAttribute goodsAttribute) ;

   void deleteById(Integer id);
   void add(LitemallGoodsAttribute goodsAttribute);

   LitemallGoodsAttribute findById(Integer id);
}
