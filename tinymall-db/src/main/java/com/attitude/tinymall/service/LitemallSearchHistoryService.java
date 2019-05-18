package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallSearchHistoryMapper;
import com.attitude.tinymall.domain.LitemallSearchHistory;
import com.attitude.tinymall.domain.LitemallSearchHistoryExample;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallSearchHistoryService {

     void save(LitemallSearchHistory searchHistory);

     List<LitemallSearchHistory> queryByUid(int uid) ;

     void deleteByUid(int uid) ;

     void deleteById(Integer id) ;

     void add(LitemallSearchHistory searchHistory);

     List<LitemallSearchHistory> querySelective(String userId, String keyword, Integer page, Integer size, String sort, String order) ;

     int countSelective(String userId, String keyword, Integer page, Integer size, String sort, String order) ;

     void updateById(LitemallSearchHistory collect);

     LitemallSearchHistory findById(Integer id);
}
