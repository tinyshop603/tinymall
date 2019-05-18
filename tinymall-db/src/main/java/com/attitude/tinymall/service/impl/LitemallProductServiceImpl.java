package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallProductMapper;
import com.attitude.tinymall.domain.LitemallProduct;
import com.attitude.tinymall.domain.LitemallProductExample;
import com.attitude.tinymall.service.LitemallProductService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LitemallProductServiceImpl implements LitemallProductService {

  @Autowired
  private LitemallProductMapper productMapper;
  @Autowired
  private LitemallGoodsServiceImpl goodsService;
  @Override
  public List<LitemallProduct> queryByGid(Integer gid) {
    LitemallProductExample example = new LitemallProductExample();
    example.or().andGoodsIdEqualTo(gid).andDeletedEqualTo(false);
    return productMapper.selectByExample(example);
  }
  @Override
  public LitemallProduct findById(Integer id) {
    return productMapper.selectByPrimaryKey(id);
  }
  @Override
  public List<LitemallProduct> querySelective(Integer goodsId, Integer page, Integer size,
      String sort, String order) {
    LitemallProductExample example = new LitemallProductExample();
    LitemallProductExample.Criteria criteria = example.createCriteria();

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return productMapper.selectByExample(example);
  }
  @Override
  public int countSelective(Integer goodsId, Integer page, Integer size, String sort,
      String order) {
    LitemallProductExample example = new LitemallProductExample();
    LitemallProductExample.Criteria criteria = example.createCriteria();

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    criteria.andDeletedEqualTo(false);

    return (int) productMapper.countByExample(example);
  }
  @Override
  public void updateById(LitemallProduct product) {
    productMapper.updateByPrimaryKeySelective(product);
  }
  @Override
  public void deleteById(Integer id) {
    productMapper.logicalDeleteByPrimaryKey(id);
  }
  @Override
  public void add(LitemallProduct product) {
    productMapper.insertSelective(product);
  }
  @Override
  public int count() {
    LitemallProductExample example = new LitemallProductExample();
    example.or().andDeletedEqualTo(false);

    return (int) productMapper.countByExample(example);
  }
  @Override
  public int countByAdminId(Integer adminId) {
    LitemallProductExample example = new LitemallProductExample();
    LitemallProductExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andGoodsIdIn(goodsService.getAdminGoodsIds(adminId));
    }
    criteria.andDeletedEqualTo(false);
    return (int) productMapper.countByExample(example);
  }

}
