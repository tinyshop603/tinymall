package com.attitude.tinymall.service;

import com.attitude.tinymall.domain.LitemallIssue;
import com.attitude.tinymall.domain.LitemallIssueExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallIssueMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallIssueService {

     List<LitemallIssue> query() ;

     void deleteById(Integer id);
    
     void add(LitemallIssue issue);

     List<LitemallIssue> querySelective(String question, Integer page, Integer size, String sort, String order) ;

     int countSelective(String question, Integer page, Integer size, String sort, String order) ;

     void updateById(LitemallIssue issue);

     LitemallIssue findById(Integer id);
}
