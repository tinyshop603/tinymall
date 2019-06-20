package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallGoodsMapper;
import com.attitude.tinymall.domain.LitemallGoods;
import com.attitude.tinymall.domain.LitemallGoods.Column;
import com.attitude.tinymall.domain.LitemallGoodsExample;
import com.attitude.tinymall.service.LitemallGoodsService;
import com.github.pagehelper.PageHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallGoodsServiceImpl implements LitemallGoodsService {

  @Autowired
  private LitemallGoodsMapper goodsMapper;
  @Autowired
  private LitemallCategoryServiceImpl categoryService;
  @Override
  public List<LitemallGoods> queryByHot(int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIsHotEqualTo(true).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public List<LitemallGoods> queryByNew(int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIsNewEqualTo(true).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdIn(catList).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public int countByCategory(List<Integer> catList, int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdIn(catList).andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }
  @Override
  public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdEqualTo(catId).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public int countByCategory(Integer catId, Integer page, Integer size) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdEqualTo(catId).andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }

  //wz-增加categoryIds字段以适应一次获取多区域商品
  @Override
  public List<LitemallGoods> querySelective(Integer categoryId, Integer brandId, String keyword,
      Integer isHot, Integer isNew, Integer offset, Integer limit, String sort,
      List<Integer> categoryIds) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    if (categoryIds != null) {
      criteria.andCategoryIdIn(categoryIds);
    }
    if (categoryId != null && categoryId != 0) {
      criteria.andCategoryIdEqualTo(categoryId);
    }
    if (brandId != null) {
      criteria.andBrandIdEqualTo(brandId);
    }
    if (isNew != null) {
      criteria.andIsNewEqualTo(isNew.intValue() == 1);
    }
    if (isHot != null) {
      criteria.andIsHotEqualTo(isHot.intValue() == 1);
    }
    if (keyword != null) {
      criteria.andKeywordsLike("%" + keyword + "%");
    }
    criteria.andDeletedEqualTo(false);

    if (sort != null) {
      example.setOrderByClause(sort);
    }
    if (limit != null && offset != null) {
      PageHelper.startPage(offset, limit);
    }

    Column[] columns = new Column[]{Column.id, Column.name, Column.listPicUrl, Column.retailPrice,
        Column.isOnSale,Column.counterPrice};
    return goodsMapper.selectByExampleSelective(example, columns);
  }
  @Override
  public int countSelective(Integer catId, Integer brandId, String keyword, Integer isHot,
      Integer isNew, Integer offset, Integer limit, String sort) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    if (catId != null) {
      criteria.andCategoryIdEqualTo(catId);
    }
    if (brandId != null) {
      criteria.andBrandIdEqualTo(brandId);
    }
    if (isNew != null) {
      criteria.andIsNewEqualTo(isNew.intValue() == 1);
    }
    if (isHot != null) {
      criteria.andIsHotEqualTo(isHot.intValue() == 1);
    }
    if (keyword != null) {
      criteria.andKeywordsLike("%" + keyword + "%");
    }
    criteria.andDeletedEqualTo(false);

    return (int) goodsMapper.countByExample(example);
  }
  @Override
  public LitemallGoods findById(Integer id) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIdEqualTo(id).andDeletedEqualTo(false);
    return goodsMapper.selectOneByExampleWithBLOBs(example);
  }

  @Override
  public List<LitemallGoods> queryByIds(List<Integer> relatedGoodsIds) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIdIn(relatedGoodsIds).andDeletedEqualTo(false);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public Integer queryOnSale(List<Integer> categoryId) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIsOnSaleEqualTo(true).andCategoryIdIn(categoryId).andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }
  @Override
  public List<LitemallGoods> listGoodsByAdminId(Integer adminId, String goodsSn, String name,
      String categoryId, Integer page, Integer size, String sort, String order) {

    // TODO 抽出适当的方法进行构造查询条件
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    List<Integer> categoryIds = categoryService
        .getAdminMallCategoryIdsByAdminId(adminId);

    if (categoryIds != null) {
      criteria.andCategoryIdIn(categoryIds);
    }

    if (!StringUtils.isEmpty(goodsSn)) {
      criteria.andGoodsSnEqualTo(goodsSn);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    if (!StringUtils.isEmpty(categoryId)) {
      criteria.andCategoryIdEqualTo(Integer.valueOf(categoryId));
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public int countGoodsByAdminId(Integer adminId, String goodsSn, String name, String categoryId) {

    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();
    List<Integer> categoryIds = categoryService
        .getAdminMallCategoryIdsByAdminId(adminId);

    if (categoryIds != null) {
      criteria.andCategoryIdIn(categoryIds);
    }

    if (!StringUtils.isEmpty(goodsSn)) {
      criteria.andGoodsSnEqualTo(goodsSn);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    if (!StringUtils.isEmpty(categoryId)) {
      criteria.andCategoryIdEqualTo(Integer.valueOf(categoryId));
    }
    criteria.andDeletedEqualTo(false);

    return (int) goodsMapper.countByExample(example);
  }
  @Override
  public List<LitemallGoods> querySelective(String goodsSn, String name, String categoryId,
      Integer page, Integer size, String sort, String order) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(goodsSn)) {
      criteria.andGoodsSnEqualTo(goodsSn);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    if (!StringUtils.isEmpty(categoryId)) {
      criteria.andCategoryIdEqualTo(Integer.valueOf(categoryId));
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return goodsMapper.selectByExample(example);
  }
  @Override
  public int countSelective(String goodsSn, String name, String categoryId) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(goodsSn)) {
      criteria.andGoodsSnEqualTo(goodsSn);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    if (!StringUtils.isEmpty(categoryId)) {
      criteria.andCategoryIdEqualTo(Integer.valueOf(categoryId));
    }
    criteria.andDeletedEqualTo(false);

    return (int) goodsMapper.countByExample(example);
  }
  @Override
  public void updateById(LitemallGoods goods) {
    goods.setListPicUrl(goods.getPrimaryPicUrl());
    goodsMapper.updateByPrimaryKeySelective(goods);
  }

  public void deleteById(Integer id) {
    goodsMapper.logicalDeleteByPrimaryKey(id);
  }

  public int add(LitemallGoods goods) {

    return goodsMapper.insertSelective(goods);
  }
  @Override
  public int count() {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }
  @Override
  public int countByAdminId(Integer adminId) {
    return this.getAdminGoodsIds(adminId).size();
  }
  @Override
  public List<Integer> getCatIds(Integer brandId, String keyword, Integer isHot, Integer isNew) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    if (brandId != null) {
      criteria.andBrandIdEqualTo(brandId);
    }
    if (isNew != null) {
      criteria.andIsNewEqualTo(isNew.intValue() == 1);
    }
    if (isHot != null) {
      criteria.andIsHotEqualTo(isHot.intValue() == 1);
    }
    if (keyword != null) {
      criteria.andKeywordsLike("%" + keyword + "%");
    }
    criteria.andDeletedEqualTo(false);

    List<LitemallGoods> goodsList = goodsMapper
        .selectByExampleSelective(example, Column.categoryId);
    List<Integer> cats = new ArrayList<Integer>();
    for (LitemallGoods goods : goodsList) {
      cats.add(goods.getCategoryId());
    }
    return cats;
  }
  @Override
  public List<Integer> getAdminGoodsIds(Integer adminId) {
    // 获取该店铺的所有的商品,此处不必考虑量的问题,原因是一步向前端扔,二数据量不会过大,,若产生效率问题:考虑表加上冗余外键
    List<LitemallGoods> litemallGoodsOfAdmin = this
        .listGoodsByAdminId(adminId, null, null, null, 0, Integer.MAX_VALUE, null, null);
    return litemallGoodsOfAdmin
        .stream()
        .mapToInt(LitemallGoods::getId)
        .boxed()
        .collect(Collectors.toList());
  }
}
