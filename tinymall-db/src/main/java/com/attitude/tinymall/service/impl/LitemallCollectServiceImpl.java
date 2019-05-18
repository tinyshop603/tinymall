package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallCollectMapper;
import com.attitude.tinymall.domain.LitemallCollect;
import com.attitude.tinymall.domain.LitemallCollectExample;
import com.attitude.tinymall.service.LitemallCollectService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallCollectServiceImpl implements LitemallCollectService {

  @Autowired
  private LitemallCollectMapper collectMapper;

  @Override
  public int count(int uid, Integer gid) {
    LitemallCollectExample example = new LitemallCollectExample();
    example.or().andUserIdEqualTo(uid).andValueIdEqualTo(gid).andDeletedEqualTo(false);
    return (int) collectMapper.countByExample(example);
  }

  @Override
  public List<LitemallCollect> queryByType(Integer userId, Integer typeId, Integer page,
      Integer size) {
    LitemallCollectExample example = new LitemallCollectExample();
    example.or().andUserIdEqualTo(userId).andTypeIdEqualTo(typeId).andDeletedEqualTo(false);
    example.setOrderByClause(LitemallCollect.Column.addTime.desc());
    PageHelper.startPage(page, size);
    return collectMapper.selectByExample(example);
  }

  @Override
  public int countByType(Integer userId, Integer typeId) {
    LitemallCollectExample example = new LitemallCollectExample();
    example.or().andUserIdEqualTo(userId).andTypeIdEqualTo(typeId).andDeletedEqualTo(false);
    return (int) collectMapper.countByExample(example);
  }

  @Override
  public LitemallCollect queryByTypeAndValue(Integer userId, Integer typeId, Integer valueId) {
    LitemallCollectExample example = new LitemallCollectExample();
    example.or().andUserIdEqualTo(userId).andValueIdEqualTo(valueId).andTypeIdEqualTo(typeId)
        .andDeletedEqualTo(false);
    return collectMapper.selectOneByExample(example);
  }

  @Override
  public void deleteById(Integer id) {
    collectMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public int add(LitemallCollect collect) {
    return collectMapper.insertSelective(collect);
  }

  @Override
  public List<LitemallCollect> querySelective(String userId, String valueId, Integer page,
      Integer size, String sort, String order) {
    LitemallCollectExample example = new LitemallCollectExample();
    LitemallCollectExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(valueId)) {
      criteria.andValueIdEqualTo(Integer.valueOf(valueId));
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return collectMapper.selectByExample(example);
  }

  @Override
  public int countSelective(String userId, String valueId, Integer page, Integer size, String sort,
      String order) {
    LitemallCollectExample example = new LitemallCollectExample();
    LitemallCollectExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(valueId)) {
      criteria.andValueIdEqualTo(Integer.valueOf(valueId));
    }
    criteria.andDeletedEqualTo(false);

    return (int) collectMapper.countByExample(example);
  }

  @Override
  public void updateById(LitemallCollect collect) {
    collectMapper.updateByPrimaryKeySelective(collect);
  }

  @Override
  public LitemallCollect findById(Integer id) {
    return collectMapper.selectByPrimaryKey(id);
  }
}
