package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallFootprintMapper;
import com.attitude.tinymall.domain.LitemallFootprint;
import com.attitude.tinymall.domain.LitemallFootprintExample;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Deprecated
public class LitemallFootprintServiceImpl {
    @Resource
    private LitemallFootprintMapper footprintMapper;

    public List<LitemallFootprint> queryByAddTime(Integer userId, Integer page, Integer size) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        example.setOrderByClause(LitemallFootprint.Column.addTime.desc());
        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }

    public int countByAddTime(Integer userId,Integer page, Integer size) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return (int)footprintMapper.countByExample(example);
    }

    public LitemallFootprint findById(Integer id) {
        return footprintMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id){
        footprintMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallFootprint footprint) {
        footprintMapper.insertSelective(footprint);
    }

    public List<LitemallFootprint> querySelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        LitemallFootprintExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(goodsId)){
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, size);
        return footprintMapper.selectByExample(example);
    }

    public int countSelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallFootprintExample example = new LitemallFootprintExample();
        LitemallFootprintExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if(!StringUtils.isEmpty(goodsId)){
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        return (int)footprintMapper.countByExample(example);
    }

    public void updateById(LitemallFootprint collect) {
        footprintMapper.updateByPrimaryKeySelective(collect);
    }

}
