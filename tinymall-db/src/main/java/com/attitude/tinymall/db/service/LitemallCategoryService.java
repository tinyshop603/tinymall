package com.attitude.tinymall.db.service;

import com.attitude.tinymall.db.dao.LitemallCategoryMapper;
import com.attitude.tinymall.db.domain.LitemallAdmin;
import com.attitude.tinymall.db.domain.LitemallCategory;
import com.attitude.tinymall.db.domain.LitemallCategoryExample;
import com.attitude.tinymall.db.domain.LitemallCategory.Column;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class LitemallCategoryService {

  @Resource
  private LitemallCategoryMapper categoryMapper;

  public List<LitemallCategory> queryIdByPid(int pid) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andParentIdEqualTo(pid).andLevelEqualTo("L2").andDeletedEqualTo(false);
    Column[] columns = new Column[]{Column.id};
    return categoryMapper.selectByExampleSelective(example, columns);
  }

  public List<LitemallCategory> queryL1WithoutRecommend(int offset, int limit) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return categoryMapper.selectByExample(example);
  }

  public List<LitemallCategory> queryL1(int offset, int limit) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return categoryMapper.selectByExample(example);
  }

  public List<LitemallCategory> queryL1() {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
    return categoryMapper.selectByExample(example);
  }

  public List<LitemallCategory> queryByPid(Integer pid) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andParentIdEqualTo(pid).andDeletedEqualTo(false);
    return categoryMapper.selectByExample(example);
  }

  public List<LitemallCategory> queryL2ByIds(List<Integer> ids) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false);
    return categoryMapper.selectByExample(example);
  }

  public LitemallCategory findById(Integer id) {
    return categoryMapper.selectByPrimaryKey(id);
  }

  public List<LitemallCategory> querySelective(String id, String name, String parentId,
      Integer page, Integer size, String sort, String order) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    LitemallCategoryExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(id)) {
      criteria.andIdEqualTo(Integer.valueOf(id));
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    if (!StringUtils.isEmpty(parentId)) {
      criteria.andParentIdEqualTo(Integer.valueOf(parentId));
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return categoryMapper.selectByExample(example);
  }

  public int countSelective(String id, String name, String parentId, Integer page, Integer size,
      String sort, String order) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    LitemallCategoryExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(id)) {
      criteria.andIdEqualTo(Integer.valueOf(id));
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    if (!StringUtils.isEmpty(parentId)) {
      criteria.andParentIdEqualTo(Integer.valueOf(parentId));
    }
    criteria.andDeletedEqualTo(false);

    return (int) categoryMapper.countByExample(example);
  }

  public void updateById(LitemallCategory category) {
    if (category.getParentId() > LitemallAdmin.USER_LIMIT) {
      throw new RuntimeException("非法的parentId");
    }
    categoryMapper.updateByPrimaryKeySelective(category);
  }

  public void deleteById(Integer id) {
    categoryMapper.logicalDeleteByPrimaryKey(id);
  }

  public void add(LitemallCategory category) {
    if (category.getParentId() > LitemallAdmin.USER_LIMIT) {
      throw new RuntimeException("非法的parentId");
    }
    categoryMapper.insertSelective(category);
  }

  private LitemallCategory.Column[] CHANNEL = {LitemallCategory.Column.id,
      LitemallCategory.Column.name, LitemallCategory.Column.iconUrl};

  public List<LitemallCategory> queryChannel() {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
    return categoryMapper.selectByExampleSelective(example, CHANNEL);
  }
}
