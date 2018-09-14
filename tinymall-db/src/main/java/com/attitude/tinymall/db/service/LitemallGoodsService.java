package com.attitude.tinymall.db.service;

import com.attitude.tinymall.db.domain.LitemallGoodsExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.db.domain.LitemallGoods;
import com.attitude.tinymall.db.domain.LitemallGoods.Column;
import com.attitude.tinymall.db.dao.LitemallGoodsMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LitemallGoodsService {

  @Resource
  private LitemallGoodsMapper goodsMapper;
  @Resource
  private LitemallCategoryService categoryService;

  public List<LitemallGoods> queryByHot(int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIsHotEqualTo(true).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }

  public List<LitemallGoods> queryByNew(int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIsNewEqualTo(true).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }

  public List<LitemallGoods> queryByCategory(List<Integer> catList, int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdIn(catList).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }

  public int countByCategory(List<Integer> catList, int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdIn(catList).andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }

  public List<LitemallGoods> queryByCategory(Integer catId, int offset, int limit) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdEqualTo(catId).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return goodsMapper.selectByExample(example);
  }

  public int countByCategory(Integer catId, Integer page, Integer size) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andCategoryIdEqualTo(catId).andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }

  //wz-增加categoryIds字段以适应一次获取多区域商品
  public List<LitemallGoods> querySelective(Integer catId, Integer brandId, String keyword,
      Integer isHot, Integer isNew, Integer offset, Integer limit, String sort,
      List<Integer> categoryIds) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    LitemallGoodsExample.Criteria criteria = example.createCriteria();

    if (categoryIds != null) {
      criteria.andCategoryIdIn(categoryIds);
    }
    if (catId != null && catId != 0) {
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

    if (sort != null) {
      example.setOrderByClause(sort);
    }
    if (limit != null && offset != null) {
      PageHelper.startPage(offset, limit);
    }

    Column[] columns = new Column[]{Column.id, Column.name, Column.listPicUrl, Column.retailPrice,
        Column.isOnSale};
    return goodsMapper.selectByExampleSelective(example, columns);
  }

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

  public LitemallGoods findById(Integer id) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIdEqualTo(id).andDeletedEqualTo(false);
    return goodsMapper.selectOneByExampleWithBLOBs(example);
  }


  public List<LitemallGoods> queryByIds(List<Integer> relatedGoodsIds) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIdIn(relatedGoodsIds).andDeletedEqualTo(false);
    return goodsMapper.selectByExample(example);
  }

  public Integer queryOnSale(List<Integer> categoryId) {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andIsOnSaleEqualTo(true).andCategoryIdIn(categoryId).andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }

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

  public void updateById(LitemallGoods goods) {
    goodsMapper.updateByPrimaryKeySelective(goods);
  }

  public void deleteById(Integer id) {
    goodsMapper.logicalDeleteByPrimaryKey(id);
  }

  public void add(LitemallGoods goods) {
    goodsMapper.insertSelective(goods);
  }

  public int count() {
    LitemallGoodsExample example = new LitemallGoodsExample();
    example.or().andDeletedEqualTo(false);
    return (int) goodsMapper.countByExample(example);
  }

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
}
