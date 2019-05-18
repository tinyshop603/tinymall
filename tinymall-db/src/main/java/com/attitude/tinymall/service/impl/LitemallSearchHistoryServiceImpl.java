package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallSearchHistoryMapper;
import com.attitude.tinymall.domain.LitemallSearchHistory;
import com.attitude.tinymall.domain.LitemallSearchHistoryExample;
import com.attitude.tinymall.service.LitemallSearchHistoryService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallSearchHistoryServiceImpl implements LitemallSearchHistoryService {
    @Autowired
    private LitemallSearchHistoryMapper searchHistoryMapper;
    @Override
    public void save(LitemallSearchHistory searchHistory) {
        searchHistoryMapper.insertSelective(searchHistory);
    }
    @Override
    public List<LitemallSearchHistory> queryByUid(int uid) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
        example.setDistinct(true);
        return searchHistoryMapper.selectByExampleSelective(example, LitemallSearchHistory.Column.keyword);
    }
    @Override
    public void deleteByUid(int uid) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        example.or().andUserIdEqualTo(uid);
        searchHistoryMapper.logicalDeleteByExample(example);
    }
    @Override
    public void deleteById(Integer id) {
        LitemallSearchHistory searchHistory = searchHistoryMapper.selectByPrimaryKey(id);
        if(searchHistory == null){
            return;
        }
        searchHistory.setDeleted(true);
        searchHistoryMapper.logicalDeleteByPrimaryKey(id);
    }
    @Override
    public void add(LitemallSearchHistory searchHistory) {
        searchHistoryMapper.insertSelective(searchHistory);
    }
    @Override
    public List<LitemallSearchHistory> querySelective(String userId, String keyword, Integer page, Integer size, String sort, String order) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        LitemallSearchHistoryExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(keyword)){
            criteria.andKeywordLike("%" + keyword + "%" );
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return searchHistoryMapper.selectByExample(example);
    }
    @Override
    public int countSelective(String userId, String keyword, Integer page, Integer size, String sort, String order) {
        LitemallSearchHistoryExample example = new LitemallSearchHistoryExample();
        LitemallSearchHistoryExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(keyword)){
            criteria.andKeywordLike("%" + keyword + "%" );
        }
        criteria.andDeletedEqualTo(false);

        return (int)searchHistoryMapper.countByExample(example);
    }
    @Override
    public void updateById(LitemallSearchHistory collect) {
        searchHistoryMapper.updateByPrimaryKeySelective(collect);
    }
    @Override
    public LitemallSearchHistory findById(Integer id) {
        return searchHistoryMapper.selectByPrimaryKey(id);
    }
}
