package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallBrand;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.domain.LitemallBrandExample;
import com.attitude.tinymall.dao.LitemallBrandMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallBrandService {

  List<LitemallBrand> queryWithNew(int offset, int limit);

  List<LitemallBrand> query(int offset, int limit);

  int queryTotalCount();

  LitemallBrand findById(Integer id);

  List<LitemallBrand> querySelective(String id, String name, Integer page, Integer size,
      String sort, String order);

  int countSelective(String id, String name, Integer page, Integer size, String sort,
      String order);

  void updateById(LitemallBrand brand);

  void deleteById(Integer id);

  void add(LitemallBrand brand);

}
