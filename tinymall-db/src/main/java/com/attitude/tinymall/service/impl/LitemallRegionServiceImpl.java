package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallRegionMapper;
import com.attitude.tinymall.domain.LitemallRegion;
import com.attitude.tinymall.domain.LitemallRegionExample;
import com.attitude.tinymall.service.LitemallRegionService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallRegionServiceImpl implements LitemallRegionService {
    @Autowired
    private LitemallRegionMapper regionMapper;
    @Override
    public List<LitemallRegion> queryByPid(Integer parentId) {
        LitemallRegionExample example = new LitemallRegionExample();
        example.or().andPidEqualTo(parentId);
        return regionMapper.selectByExample(example);
    }
    @Override
    public LitemallRegion findById(Integer id) {
        return regionMapper.selectByPrimaryKey(id);
    }
    @Override
    public List<LitemallRegion> querySelective(String name, Integer code, Integer page, Integer size, String sort, String order) {
        LitemallRegionExample example = new LitemallRegionExample();
        LitemallRegionExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        if(code != null){
            criteria.andCodeEqualTo(code);
        }
        PageHelper.startPage(page, size);
        return regionMapper.selectByExample(example);
    }
    @Override
    public LitemallRegion queryByCode(Integer code){
        LitemallRegionExample example = new LitemallRegionExample();

        example.or().andCodeEqualTo(code);
        return regionMapper.selectOneByExample(example);
    }

    @Override
    public LitemallRegion queryByName(String name){
        LitemallRegionExample example = new LitemallRegionExample();
        example.or().andNameEqualTo(name);
        return regionMapper.selectOneByExample(example);
    }

    @Override
    public int countSelective(String name, Integer code, Integer page, Integer size, String sort, String order) {
        LitemallRegionExample example = new LitemallRegionExample();
        LitemallRegionExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        if(code != null){
            criteria.andCodeEqualTo(code);
        }
        return (int)regionMapper.countByExample(example);
    }
}
