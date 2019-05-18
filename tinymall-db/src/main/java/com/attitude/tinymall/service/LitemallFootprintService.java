package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallFootprintMapper;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.domain.LitemallFootprint;
import com.attitude.tinymall.domain.LitemallFootprintExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Deprecated
public interface LitemallFootprintService {

   List<LitemallFootprint> queryByAddTime(Integer userId, Integer page, Integer size);

   int countByAddTime(Integer userId, Integer page, Integer size);

   LitemallFootprint findById(Integer id);

   void deleteById(Integer id);

   void add(LitemallFootprint footprint);

   List<LitemallFootprint> querySelective(String userId, String goodsId, Integer page,
      Integer size, String sort, String order);

   int countSelective(String userId, String goodsId, Integer page, Integer size, String sort,
      String order);

   void updateById(LitemallFootprint collect);

}
