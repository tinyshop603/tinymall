package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallFootprintMapper;
import com.attitude.tinymall.domain.LitemallFootprint;
import com.attitude.tinymall.domain.LitemallFootprintExample;
import com.attitude.tinymall.service.LitemallFootprintService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Deprecated
public class LitemallFootprintServiceImpl implements LitemallFootprintService {

  @Autowired
  private LitemallFootprintMapper footprintMapper;

  @Override
  public List<LitemallFootprint> queryByAddTime(Integer userId, Integer page, Integer size) {
    LitemallFootprintExample example = new LitemallFootprintExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    example.setOrderByClause(LitemallFootprint.Column.addTime.desc());
    PageHelper.startPage(page, size);
    return footprintMapper.selectByExample(example);
  }

  @Override
  public int countByAddTime(Integer userId, Integer page, Integer size) {
    LitemallFootprintExample example = new LitemallFootprintExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    return (int) footprintMapper.countByExample(example);
  }

  @Override
  public LitemallFootprint findById(Integer id) {
    return footprintMapper.selectByPrimaryKey(id);
  }

  @Override
  public void deleteById(Integer id) {
    footprintMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public void add(LitemallFootprint footprint) {
    footprintMapper.insertSelective(footprint);
  }

  @Override
  public List<LitemallFootprint> querySelective(String userId, String goodsId, Integer page,
      Integer size, String sort, String order) {
    LitemallFootprintExample example = new LitemallFootprintExample();
    LitemallFootprintExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(goodsId)) {
      criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return footprintMapper.selectByExample(example);
  }

  @Override
  public int countSelective(String userId, String goodsId, Integer page, Integer size, String sort,
      String order) {
    LitemallFootprintExample example = new LitemallFootprintExample();
    LitemallFootprintExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(goodsId)) {
      criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
    }
    criteria.andDeletedEqualTo(false);

    return (int) footprintMapper.countByExample(example);
  }

  @Override
  public void updateById(LitemallFootprint collect) {
    footprintMapper.updateByPrimaryKeySelective(collect);
  }

}
