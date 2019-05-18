package com.attitude.tinymall.service;

import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallTopicMapper;
import com.attitude.tinymall.domain.LitemallTopic;
import com.attitude.tinymall.domain.LitemallTopicExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallTopicService {

     List<LitemallTopic> queryList(int offset, int limit) ;

     int queryTotal() ;

     LitemallTopic findById(Integer id) ;

     List<LitemallTopic> queryRelatedList(Integer id, int offset, int limit) ;

     List<LitemallTopic> querySelective(String title, String subtitle, Integer page, Integer limit, String sort, String order) ;

     int countSelective(String title, String subtitle, Integer page, Integer size, String sort, String order) ;

     void updateById(LitemallTopic topic) ;

     void deleteById(Integer id) ;

     void add(LitemallTopic topic) ;


}
