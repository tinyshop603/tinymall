package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallAdMapper;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.domain.LitemallAd;
import com.attitude.tinymall.domain.LitemallAdExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallAdService {

  List<LitemallAd> queryByApid(Integer i);

  List<LitemallAd> querySelective(String name, String content, Integer page, Integer limit,
      String sort, String order);

  int countSelective(String name, String content, Integer page, Integer size, String sort,
      String order);

  void updateById(LitemallAd ad);

  void deleteById(Integer id);

  void add(LitemallAd ad);

  LitemallAd findById(Integer id);
}
