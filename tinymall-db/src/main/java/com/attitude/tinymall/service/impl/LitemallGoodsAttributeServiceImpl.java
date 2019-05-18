package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallGoodsAttributeMapper;
import com.attitude.tinymall.domain.LitemallGoodsAttribute;
import com.attitude.tinymall.domain.LitemallGoodsAttributeExample;
import com.attitude.tinymall.service.LitemallGoodsAttributeService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LitemallGoodsAttributeServiceImpl implements LitemallGoodsAttributeService {

  @Autowired
  private LitemallGoodsAttributeMapper goodsAttributeMapper;
  @Autowired
  private LitemallGoodsServiceImpl goodsService;
  @Override
  public List<LitemallGoodsAttribute> queryByGid(Integer goodsId) {
    LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
    example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
    return goodsAttributeMapper.selectByExample(example);
  }
  @Override
  public List<LitemallGoodsAttribute> querySelective(Integer goodsId, Integer adminId, Integer page,
      Integer size, String sort, String order) {
    LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
    LitemallGoodsAttributeExample.Criteria criteria = example.createCriteria();

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    List<Integer> litemallGoodsIdsOfAdmin = goodsService.getAdminGoodsIds(adminId);
    if (litemallGoodsIdsOfAdmin.size() > 0) {
      criteria.andGoodsIdIn(litemallGoodsIdsOfAdmin);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return goodsAttributeMapper.selectByExample(example);
  }
  @Override
  public int countSelective(Integer goodsId, Integer adminId, Integer page, Integer size,
      String sort, String order) {
    LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
    LitemallGoodsAttributeExample.Criteria criteria = example.createCriteria();

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    List<Integer> litemallGoodsIdsOfAdmin = goodsService.getAdminGoodsIds(adminId);
    if (litemallGoodsIdsOfAdmin.size() > 0) {
      criteria.andGoodsIdIn(litemallGoodsIdsOfAdmin);
    }
    criteria.andDeletedEqualTo(false);

    return (int) goodsAttributeMapper.countByExample(example);
  }
  @Override
  public void updateById(LitemallGoodsAttribute goodsAttribute) {
    goodsAttributeMapper.updateByPrimaryKeySelective(goodsAttribute);
  }
  @Override
  public void deleteById(Integer id) {
    goodsAttributeMapper.logicalDeleteByPrimaryKey(id);
  }
  @Override
  public void add(LitemallGoodsAttribute goodsAttribute) {
    goodsAttributeMapper.insertSelective(goodsAttribute);
  }
  @Override
  public LitemallGoodsAttribute findById(Integer id) {
    return goodsAttributeMapper.selectByPrimaryKey(id);
  }
}
