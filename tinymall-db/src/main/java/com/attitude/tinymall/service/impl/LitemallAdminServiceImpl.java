package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallAdminMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallAdmin.Column;
import com.attitude.tinymall.domain.LitemallAdminExample;
import com.attitude.tinymall.domain.LitemallCategory;
import com.attitude.tinymall.domain.baidu.fence.ShopFenceResult;
import com.attitude.tinymall.service.BaiduFenceService;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.service.LitemallCategoryService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@Slf4j
public class LitemallAdminServiceImpl implements LitemallAdminService {

  @Autowired
  private LitemallAdminMapper adminMapper;

  @Autowired
  private BaiduFenceService baiduFenceService;

  @Autowired
  private LitemallCategoryService categoryService;

  @Override
  public LitemallAdmin findAdmin(String username) {
    LitemallAdminExample example = new LitemallAdminExample();
    example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
    List<LitemallAdmin> litemallAdmins = adminMapper.selectByExample(example);
    // 经过若干主键的限制,限制了该数据的list最大为1
    LitemallAdmin res = null;
    if (litemallAdmins != null && litemallAdmins.size() > 0) {
      res = litemallAdmins.get(0);
    }
    return res;
  }

  @Override
  public LitemallAdmin findAdminByOwnerId(String ownerId) {
    LitemallAdminExample example = new LitemallAdminExample();
    example.or().andOwnerIdEqualTo(ownerId).andDeletedEqualTo(false);
    return adminMapper.selectOneByExample(example);
  }

  private final Column[] result = new Column[]{Column.id, Column.username, Column.avatar,Column.tpdShopNo};

  @Override
  public List<LitemallAdmin> querySelective(String username, Integer page, Integer limit,
      String sort, String order) {
    LitemallAdminExample example = new LitemallAdminExample();
    LitemallAdminExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(username)) {
      criteria.andUsernameLike("%" + username + "%");
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, limit);
    return adminMapper.selectByExampleSelective(example, result);
  }

  @Override
  public int countSelective(String username, Integer page, Integer size, String sort,
      String order) {
    LitemallAdminExample example = new LitemallAdminExample();
    LitemallAdminExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(username)) {
      criteria.andUsernameLike("%" + username + "%");
    }
    criteria.andDeletedEqualTo(false);

    return (int) adminMapper.countByExample(example);
  }

  @Override
  public void updateById(LitemallAdmin admin) {
    if (!StringUtils.isEmpty(admin.getShopAddress()) && admin.getShopFenceId() != null) {
      boolean isSuccess = baiduFenceService
          .updateCreateCircleFence(admin.getShopFenceId(), admin.getShopAddress(), 3000);
      if (isSuccess) {
        log.info("根据位置信息更新地理围栏成功: fenceId: {}, address: {} ", admin.getShopFenceId(),
            admin.getShopAddress());
      }
    }
    adminMapper.updateByPrimaryKeySelective(admin);
  }

  @Override
  public void deleteById(Integer id) {
    adminMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public void add(LitemallAdmin admin) {
    if (!StringUtils.isEmpty(admin.getShopAddress())) {
      ShopFenceResult circleFence = baiduFenceService
          .createCircleFence(admin.getOwnerId(), admin.getShopAddress(), 3000);
      if (circleFence != null && circleFence.getFenceId() > 0) {
        admin.setShopFenceId(circleFence.getFenceId());
      }
    }
    adminMapper.insertSelective(admin);
  }

  @Override
  public LitemallAdmin findById(Integer id) {
    return adminMapper.selectByPrimaryKeySelective(id, result);
  }

  @Override
  public LitemallAdmin findAllColunmById(Integer id) {
    return adminMapper.selectByPrimaryKey(id);
  }

  @Override
  public LitemallAdmin findByShopId(Integer shopId) {
    LitemallCategory category = categoryService.findById(shopId);
    return findById(category.getParentId());
  }

}
