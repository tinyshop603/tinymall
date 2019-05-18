package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallKeyword;
import com.attitude.tinymall.domain.LitemallKeywordExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallKeywordMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallKeywordService {

     List<LitemallKeyword> queryDefaults() ;

     LitemallKeyword queryDefault() ;

     List<LitemallKeyword> queryHots() ;

     List<LitemallKeyword> queryByKeyword(String keyword, Integer page, Integer size) ;

     List<LitemallKeyword> querySelective(String keyword, String url, Integer page, Integer limit, String sort, String order) ;

     int countSelective(String keyword, String url, Integer page, Integer limit, String sort, String order) ;

     void add(LitemallKeyword keywords);

     LitemallKeyword findById(Integer id);

     void updateById(LitemallKeyword keywords);

     void deleteById(Integer id);
}
