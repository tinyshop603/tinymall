package com.attitude.tinymall.db.service;

import com.attitude.tinymall.core.service.BaiduFenceService;
import com.attitude.tinymall.db.dao.LitemallUserMapper;
import com.attitude.tinymall.db.domain.LitemallAdmin;
import com.attitude.tinymall.db.domain.LitemallUser;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.db.domain.LitemallUserExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class LitemallUserService {

  @Autowired
  private LitemallUserMapper userMapper;
  @Autowired
  private BaiduFenceService baiduFenceService;
  @Autowired
  private LitemallAdminService adminService;

  public LitemallUser findById(Integer userId) {
    return userMapper.selectByPrimaryKey(userId);
  }

  public LitemallUser queryByOid(String openId) {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
    return userMapper.selectOneByExample(example);
  }

  public void add(LitemallUser user) {
    if (user.getAdminId() != null) {
      // 挂接, 并监控
      boolean isHunged = baiduFenceService.hangUpPerson(user.getId().toString());
      if (isHunged) {
        LitemallAdmin litemallAdmin = adminService.findById(user.getAdminId());
        // 由于商店地址是必填项, 则此时的fence的Id肯定是已经创建好了的
        boolean isSuccessMotioned = baiduFenceService
            .addMonitorPersonToFence(user.getId().toString(), litemallAdmin.getShopFenceId());
        log.info("user: {} has been add to the fence: {}, {}?", user.getId(),
            litemallAdmin.getShopFenceId(), isSuccessMotioned);
      }
    }
    userMapper.insertSelective(user);
  }

  public void update(LitemallUser user) {
    userMapper.updateByPrimaryKeySelective(user);
  }


  public List<LitemallUser> listUsersByAdminId(Integer adminId, String username, String mobile,
      Integer page, Integer size, String sort, String order) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }

    if (!StringUtils.isEmpty(username)) {
      criteria.andUsernameLike("%" + username + "%");
    }
    if (!StringUtils.isEmpty(mobile)) {
      criteria.andMobileEqualTo(mobile);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return userMapper.selectByExample(example);
  }

  public int countUsersByAdminId(Integer adminId, String username, String mobile) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    if (!StringUtils.isEmpty(username)) {
      criteria.andUsernameLike("%" + username + "%");
    }
    if (!StringUtils.isEmpty(mobile)) {
      criteria.andMobileEqualTo(mobile);
    }
    criteria.andDeletedEqualTo(false);

    return (int) userMapper.countByExample(example);
  }


  public List<LitemallUser> querySelective(String username, String mobile, Integer page,
      Integer size, String sort, String order) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(username)) {
      criteria.andUsernameLike("%" + username + "%");
    }
    if (!StringUtils.isEmpty(mobile)) {
      criteria.andMobileEqualTo(mobile);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return userMapper.selectByExample(example);
  }

  public int countSeletive(String username, String mobile, Integer page, Integer size, String sort,
      String order) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(username)) {
      criteria.andUsernameLike("%" + username + "%");
    }
    if (!StringUtils.isEmpty(mobile)) {
      criteria.andMobileEqualTo(mobile);
    }
    criteria.andDeletedEqualTo(false);

    return (int) userMapper.countByExample(example);
  }

  public int count() {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andDeletedEqualTo(false);

    return (int) userMapper.countByExample(example);
  }

  public int countByAdminId(Integer adminId) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);

    return (int) userMapper.countByExample(example);
  }

  public List<LitemallUser> queryByUsername(String username) {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
    return userMapper.selectByExample(example);
  }

  public List<LitemallUser> queryByMobile(String mobile) {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
    return userMapper.selectByExample(example);
  }

  public List<LitemallUser> queryByAdminId(Integer adminId) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    return userMapper.selectByExample(example);
  }


  public void deleteById(Integer id) {
    userMapper.logicalDeleteByPrimaryKey(id);
  }
}
