package com.attitude.tinymall.db.service;

import com.attitude.tinymall.core.domain.baidu.fence.ShopFenceResult;
import com.attitude.tinymall.core.service.BaiduFenceService;
import com.attitude.tinymall.db.domain.LitemallAdminExample;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.db.dao.LitemallAdminMapper;
import com.attitude.tinymall.db.domain.LitemallAdmin;
import com.attitude.tinymall.db.domain.LitemallAdmin.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class LitemallAdminService {

  @Autowired
  private LitemallAdminMapper adminMapper;

  @Autowired
  private BaiduFenceService baiduFenceService;

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

  public LitemallAdmin findAdminByOwnerId(String ownerId) {
    LitemallAdminExample example = new LitemallAdminExample();
    example.or().andOwnerIdEqualTo(ownerId).andDeletedEqualTo(false);
    return adminMapper.selectOneByExample(example);
  }

  private final Column[] result = new Column[]{Column.id, Column.username, Column.avatar};

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

  public void updateById(LitemallAdmin admin) {
    if (!StringUtils.isEmpty(admin.getShopAddress()) && admin.getShopFenceId() != null) {
      baiduFenceService
          .updateCreateCircleFence(admin.getShopFenceId(), admin.getShopAddress(), 3000);
    }
    adminMapper.updateByPrimaryKeySelective(admin);
  }

  public void deleteById(Integer id) {
    adminMapper.logicalDeleteByPrimaryKey(id);
  }

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

  public LitemallAdmin findById(Integer id) {
    return adminMapper.selectByPrimaryKeySelective(id, result);
  }
  public LitemallAdmin findAllById(Integer id) {
    return adminMapper.selectByPrimaryKey(id);
  }
}
