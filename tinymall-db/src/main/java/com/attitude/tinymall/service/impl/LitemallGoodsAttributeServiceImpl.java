package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallGoodsAttributeMapper;
import com.attitude.tinymall.domain.LitemallGoodsAttribute;
import com.attitude.tinymall.domain.LitemallGoodsAttributeExample;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LitemallGoodsAttributeServiceImpl {

  @Resource
  private LitemallGoodsAttributeMapper goodsAttributeMapper;
  @Resource
  private LitemallGoodsServiceImpl goodsService;

  public List<LitemallGoodsAttribute> queryByGid(Integer goodsId) {
    LitemallGoodsAttributeExample example = new LitemallGoodsAttributeExample();
    example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
    return goodsAttributeMapper.selectByExample(example);
  }

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

  public void updateById(LitemallGoodsAttribute goodsAttribute) {
    goodsAttributeMapper.updateByPrimaryKeySelective(goodsAttribute);
  }

  public void deleteById(Integer id) {
    goodsAttributeMapper.logicalDeleteByPrimaryKey(id);
  }

  public void add(LitemallGoodsAttribute goodsAttribute) {
    goodsAttributeMapper.insertSelective(goodsAttribute);
  }

  public LitemallGoodsAttribute findById(Integer id) {
    return goodsAttributeMapper.selectByPrimaryKey(id);
  }
}
