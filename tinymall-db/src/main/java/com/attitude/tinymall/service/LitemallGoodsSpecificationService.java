package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallGoodsSpecification;
import com.attitude.tinymall.domain.LitemallGoodsSpecificationExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallGoodsSpecificationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LitemallGoodsSpecificationService {



   List<LitemallGoodsSpecification> queryByGid(Integer id) ;

   LitemallGoodsSpecification findById(Integer id);

   List<LitemallGoodsSpecification> querySelective(Integer goodsId, Integer adminId,
      Integer page, Integer size, String sort, String order) ;

   int countSelective(Integer goodsId, Integer adminId, Integer page, Integer size,
      String sort,
      String order);

   void updateById(LitemallGoodsSpecification goodsSpecification);

   void deleteById(Integer id);

   void add(LitemallGoodsSpecification goodsSpecification) ;

   Integer[] queryIdsByGid(Integer goodsId) ;
 
   Object getSpecificationVoList(Integer id) ;

}
