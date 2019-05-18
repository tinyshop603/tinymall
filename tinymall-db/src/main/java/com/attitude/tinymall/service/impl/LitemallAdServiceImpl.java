package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallAdMapper;
import com.attitude.tinymall.domain.LitemallAd;
import com.attitude.tinymall.domain.LitemallAdExample;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallAdServiceImpl {
    @Resource
    private LitemallAdMapper adMapper;

    public List<LitemallAd> queryByApid(Integer i) {
        LitemallAdExample example = new LitemallAdExample();
        example.or().andPositionEqualTo(i).andDeletedEqualTo(false);
        return adMapper.selectByExample(example);
    }

    public List<LitemallAd> querySelective(String name, String content, Integer page, Integer limit, String sort, String order) {
        LitemallAdExample example = new LitemallAdExample();
        LitemallAdExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        if(!StringUtils.isEmpty(content)){
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return adMapper.selectByExample(example);
    }

    public int countSelective(String name, String content, Integer page, Integer size, String sort, String order) {
        LitemallAdExample example = new LitemallAdExample();
        LitemallAdExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        if(!StringUtils.isEmpty(content)){
            criteria.andContentLike("%" + content + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)adMapper.countByExample(example);
    }

    public void updateById(LitemallAd ad) {
        adMapper.updateByPrimaryKeySelective(ad);
    }

    public void deleteById(Integer id) {
        adMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallAd ad) {
        adMapper.insertSelective(ad);
    }

    public LitemallAd findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }
}
