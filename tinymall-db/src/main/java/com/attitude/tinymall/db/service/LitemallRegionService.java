package com.attitude.tinymall.db.service;

import com.attitude.tinymall.db.dao.LitemallRegionMapper;
import com.attitude.tinymall.db.domain.LitemallRegion;
import com.attitude.tinymall.db.domain.LitemallRegionExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.db.dao.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallRegionService {
    @Resource
    private LitemallRegionMapper regionMapper;

    public List<LitemallRegion> queryByPid(Integer parentId) {
        LitemallRegionExample example = new LitemallRegionExample();
        example.or().andPidEqualTo(parentId);
        return regionMapper.selectByExample(example);
    }

    public LitemallRegion findById(Integer id) {
        return regionMapper.selectByPrimaryKey(id);
    }

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
