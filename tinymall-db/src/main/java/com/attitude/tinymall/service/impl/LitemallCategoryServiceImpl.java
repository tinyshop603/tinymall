package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallCategoryMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallCategory;
import com.attitude.tinymall.domain.LitemallCategory.Column;
import com.attitude.tinymall.domain.LitemallCategoryExample;
import com.attitude.tinymall.service.LitemallCategoryService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class LitemallCategoryServiceImpl implements LitemallCategoryService {

  @Autowired
  private LitemallCategoryMapper categoryMapper;

  @Autowired
  private LitemallAdminServiceImpl litemallAdminServiceImpl;

  @Override
  public String getAdminCategoryIdByUserName(String userName) {
    if (StringUtils.isEmpty(userName)) {
      return null;
    } else {
      LitemallAdmin admin = litemallAdminServiceImpl.findAdmin(userName);
      return this.getAdminCategoryIdByAdminId(admin.getId());
    }

  }

  @Override
  public String getAdminCategoryIdByAdminId(Integer adminId) {
    // 最高权限的adminId
    if (adminId == null || adminId <= 0) {
      return null;
    }
    // 根据Parent Id 查询出 Id ,当做parentId进行塞入进去
    List<LitemallCategory> parentCategorys = this.queryIdByPid(adminId, "L1");
    // 此时查询出来的必须是1或者是0条数据
    String parentId = null;
    if (parentCategorys != null && parentCategorys.size() > 0) {
      parentId = parentCategorys.get(0).getId().toString();
    }
    return parentId;
  }

  /**
   * 获取店铺的所有大分类信息 此处进行了分页的行为
   */
  @Override
  public List<LitemallCategory> getAdminMallCategoryByAdminId(Integer adminId, Integer page,
      Integer limit, String sort, String order) {
    return this
        .querySelective(null, null, this.getAdminCategoryIdByAdminId(adminId), page, limit, sort,
            order);
  }

  /**
   * 获取店铺分类的总数
   */
  @Override
  public int countAdminMallCategoryByAdminId(Integer adminId, Integer page, Integer limit,
      String sort, String order) {
    return this
        .countSelective(null, null, this.getAdminCategoryIdByAdminId(adminId), page, limit, sort,
            order);
  }

  @Override
  public List<Integer> getAdminMallCategoryIdsByAdminId(Integer adminId) {
    return this.getAdminMallCategoryByAdminId(adminId, 0, Integer.MAX_VALUE, null, null)
        .stream()
        .mapToInt(LitemallCategory::getId)
        .boxed()
        .collect(Collectors.toList());
  }

  @Override
  public List<LitemallCategory> queryIdByPid(int pid) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andParentIdEqualTo(pid).andLevelEqualTo("L2").andDeletedEqualTo(false);
    Column[] columns = new Column[]{Column.id};
    return categoryMapper.selectByExampleSelective(example, columns);
  }

  @Override
  public List<LitemallCategory> queryIdByPid(int pid, String level) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andParentIdEqualTo(pid).andLevelEqualTo(level).andDeletedEqualTo(false);
    Column[] columns = new Column[]{Column.id};
    return categoryMapper.selectByExampleSelective(example, columns);
  }

  @Override
  public List<LitemallCategory> queryL1WithoutRecommend(int offset, int limit) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return categoryMapper.selectByExample(example);
  }

  @Override
  public List<LitemallCategory> queryL1(int offset, int limit) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return categoryMapper.selectByExample(example);
  }

  @Override
  public List<LitemallCategory> queryL1() {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
    return categoryMapper.selectByExample(example);
  }

  @Override
  public List<LitemallCategory> queryByPid(Integer pid) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andParentIdEqualTo(pid).andIsShowEqualTo(true).andDeletedEqualTo(false);
    return categoryMapper.selectByExample(example);
  }

  @Override
  public List<LitemallCategory> queryL2ByIds(List<Integer> ids) {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false);
    return categoryMapper.selectByExample(example);
  }

  @Override
  public LitemallCategory findById(Integer id) {
    return categoryMapper.selectByPrimaryKey(id);
  }

  @Override
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

  @Override
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

  @Override
  public void updateById(LitemallCategory category) {
   /* if (category.getParentId() > LitemallAdmin.USER_LIMIT) {
      throw new RuntimeException("非法的parentId");
    }*/
    categoryMapper.updateByPrimaryKeySelective(category);
  }

  @Override
  public void deleteById(Integer id) {
    categoryMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public void add(LitemallCategory category) {
 /*   if (category.getParentId() > LitemallAdmin.USER_LIMIT) {
      throw new RuntimeException("非法的parentId");
    }*/
    categoryMapper.insertSelective(category);
  }

  private Column[] CHANNEL = {Column.id,
      Column.name, Column.iconUrl};

  @Override
  public List<LitemallCategory> queryChannel() {
    LitemallCategoryExample example = new LitemallCategoryExample();
    example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
    return categoryMapper.selectByExampleSelective(example, CHANNEL);
  }
}
