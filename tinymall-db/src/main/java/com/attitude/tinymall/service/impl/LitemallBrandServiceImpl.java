package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallBrandMapper;
import com.attitude.tinymall.domain.LitemallBrand;
import com.attitude.tinymall.domain.LitemallBrandExample;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallBrandServiceImpl {
    @Resource
    private LitemallBrandMapper brandMapper;

    public List<LitemallBrand> queryWithNew(int offset, int limit) {
        LitemallBrandExample example = new LitemallBrandExample();
        example.or().andIsNewEqualTo(true).andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return brandMapper.selectByExample(example);
    }

    public List<LitemallBrand> query(int offset, int limit) {
        LitemallBrandExample example = new LitemallBrandExample();
        example.or().andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return brandMapper.selectByExample(example);
    }

    public int queryTotalCount() {
        LitemallBrandExample example = new LitemallBrandExample();
        example.or().andDeletedEqualTo(false);
        return (int)brandMapper.countByExample(example);
    }

    public LitemallBrand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    public List<LitemallBrand> querySelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(id)){
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return brandMapper.selectByExample(example);
    }

    public int countSelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallBrandExample example = new LitemallBrandExample();
        LitemallBrandExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(id)){
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)brandMapper.countByExample(example);
    }

    public void updateById(LitemallBrand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    public void deleteById(Integer id) {
        brandMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallBrand brand) {
        brandMapper.insertSelective(brand);
    }

}
