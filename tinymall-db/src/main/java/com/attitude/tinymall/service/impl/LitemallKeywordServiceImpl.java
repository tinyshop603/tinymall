package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallKeywordMapper;
import com.attitude.tinymall.domain.LitemallKeyword;
import com.attitude.tinymall.domain.LitemallKeywordExample;
import com.attitude.tinymall.service.LitemallKeywordService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallKeywordServiceImpl implements LitemallKeywordService {
    @Autowired
    private LitemallKeywordMapper keywordsMapper;

    @Override
    public List<LitemallKeyword> queryDefaults() {

        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return keywordsMapper.selectByExample(example);
    }
    @Override
    public LitemallKeyword queryDefault() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return keywordsMapper.selectOneByExample(example);
    }
    @Override
    public List<LitemallKeyword> queryHots() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsHotEqualTo(true).andDeletedEqualTo(false);
        return keywordsMapper.selectByExample(example);
    }
    @Override
    public List<LitemallKeyword> queryByKeyword(String keyword, Integer page, Integer size) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.setDistinct(true);
        example.or().andKeywordLike("%" + keyword + "%").andDeletedEqualTo(false);
        PageHelper.startPage(page, size);
        return keywordsMapper.selectByExampleSelective(example, LitemallKeyword.Column.keyword);
    }
    @Override
    public List<LitemallKeyword> querySelective(String keyword, String url, Integer page, Integer limit, String sort, String order) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        LitemallKeywordExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%" + keyword + "%");
        }
        if (!StringUtils.isEmpty(url)) {
            criteria.andUrlLike("%" + url + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return keywordsMapper.selectByExample(example);
    }
    @Override
    public int countSelective(String keyword, String url, Integer page, Integer limit, String sort, String order) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        LitemallKeywordExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordLike("%" + keyword + "%");
        }
        if (!StringUtils.isEmpty(url)) {
            criteria.andUrlLike("%" + url + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return (int)keywordsMapper.countByExample(example);
    }
    @Override
    public void add(LitemallKeyword keywords) {
        keywordsMapper.insertSelective(keywords);
    }
    @Override
    public LitemallKeyword findById(Integer id) {
        return keywordsMapper.selectByPrimaryKey(id);
    }
    @Override
    public void updateById(LitemallKeyword keywords) {
        keywordsMapper.updateByPrimaryKeySelective(keywords);
    }
    @Override
    public void deleteById(Integer id) {
        keywordsMapper.logicalDeleteByPrimaryKey(id);
    }
}
