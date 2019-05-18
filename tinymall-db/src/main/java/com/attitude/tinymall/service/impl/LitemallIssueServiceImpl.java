package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallIssueMapper;
import com.attitude.tinymall.domain.LitemallIssue;
import com.attitude.tinymall.domain.LitemallIssueExample;
import com.attitude.tinymall.service.LitemallIssueService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallIssueServiceImpl implements LitemallIssueService {
    @Autowired
    private LitemallIssueMapper issueMapper;
   @Override
    public List<LitemallIssue> query() {
        LitemallIssueExample example = new LitemallIssueExample();
        example.or().andDeletedEqualTo(false);
        return issueMapper.selectByExample(example);
    }
    @Override
    public void deleteById(Integer id) {
        issueMapper.logicalDeleteByPrimaryKey(id);
    }
    @Override
    public void add(LitemallIssue issue) {
        issueMapper.insertSelective(issue);
    }
    @Override
    public List<LitemallIssue> querySelective(String question, Integer page, Integer size, String sort, String order) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(question)){
            criteria.andQuestionLike("%" + question + "%" );
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return issueMapper.selectByExample(example);
    }
    @Override
    public int countSelective(String question, Integer page, Integer size, String sort, String order) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(question)){
            criteria.andQuestionLike("%" + question + "%" );
        }
        criteria.andDeletedEqualTo(false);

        return (int)issueMapper.countByExample(example);
    }
    @Override
    public void updateById(LitemallIssue issue) {
        issueMapper.updateByPrimaryKeySelective(issue);
    }
    @Override
    public LitemallIssue findById(Integer id) {
        return issueMapper.selectByPrimaryKey(id);
    }
}
