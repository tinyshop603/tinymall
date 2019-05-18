package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallKeywordMapper;
import com.attitude.tinymall.domain.LitemallKeyword;
import com.attitude.tinymall.domain.LitemallKeywordExample;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallKeywordServiceImpl {
    @Resource
    private LitemallKeywordMapper keywordsMapper;

    public List<LitemallKeyword> queryDefaults() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return keywordsMapper.selectByExample(example);
    }

    public LitemallKeyword queryDefault() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsDefaultEqualTo(true).andDeletedEqualTo(false);
        return keywordsMapper.selectOneByExample(example);
    }

    public List<LitemallKeyword> queryHots() {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.or().andIsHotEqualTo(true).andDeletedEqualTo(false);
        return keywordsMapper.selectByExample(example);
    }

    public List<LitemallKeyword> queryByKeyword(String keyword, Integer page, Integer size) {
        LitemallKeywordExample example = new LitemallKeywordExample();
        example.setDistinct(true);
        example.or().andKeywordLike("%" + keyword + "%").andDeletedEqualTo(false);
        PageHelper.startPage(page, size);
        return keywordsMapper.selectByExampleSelective(example, LitemallKeyword.Column.keyword);
    }

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

    public void add(LitemallKeyword keywords) {
        keywordsMapper.insertSelective(keywords);
    }

    public LitemallKeyword findById(Integer id) {
        return keywordsMapper.selectByPrimaryKey(id);
    }

    public void updateById(LitemallKeyword keywords) {
        keywordsMapper.updateByPrimaryKeySelective(keywords);
    }

    public void deleteById(Integer id) {
        keywordsMapper.logicalDeleteByPrimaryKey(id);
    }
}
