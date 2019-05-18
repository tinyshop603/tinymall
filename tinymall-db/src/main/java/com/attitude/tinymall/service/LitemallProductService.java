package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallProductMapper;
import com.attitude.tinymall.domain.LitemallProduct;
import com.attitude.tinymall.domain.LitemallProductExample;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallProductService {


   List<LitemallProduct> queryByGid(Integer gid) ;

   LitemallProduct findById(Integer id);

   List<LitemallProduct> querySelective(Integer goodsId, Integer page, Integer size,
      String sort, String order) ;

   int countSelective(Integer goodsId, Integer page, Integer size, String sort,
      String order) ;

   void updateById(LitemallProduct product);

   void deleteById(Integer id);

   void add(LitemallProduct product);

   int count() ;

   int countByAdminId(Integer adminId) ;

}
