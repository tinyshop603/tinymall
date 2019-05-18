package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallUserMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallUser;
import com.attitude.tinymall.domain.LitemallUserExample;
import com.attitude.tinymall.service.BaiduFenceService;
import com.attitude.tinymall.service.LitemallUserService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class LitemallUserServiceImpl implements LitemallUserService {

  @Autowired
  private LitemallUserMapper userMapper;
  @Autowired
  private BaiduFenceService baiduFenceService;
  @Autowired
  private LitemallAdminServiceImpl adminService;

  @Override
  public LitemallUser findById(Integer userId) {
    return userMapper.selectByPrimaryKey(userId);
  }
  @Override
  public LitemallUser queryByOid(String openId) {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
    return userMapper.selectOneByExample(example);
  }
  @Override
  public void add(LitemallUser user) {
    tryToMontionPerson(user);
    userMapper.insertSelective(user);
  }
  @Override
  public void tryToMontionPerson(LitemallUser user) {
    if (user.getAdminId() != null) {
      // 挂接, 并监控
      LitemallAdmin litemallAdmin = adminService.findAllColunmById(user.getAdminId());
      // 由于商店地址是必填项, 则此时的fence的Id肯定是已经创建好了的
      boolean isSuccessMotioned = baiduFenceService
          .addMonitorPersonToFence(user.getId().toString(), litemallAdmin.getShopFenceId());
      log.info("user: {} has been add to the fence: {}, {}?", user.getId(),
          litemallAdmin.getShopFenceId(), isSuccessMotioned);

    }
  }
  @Override
  public void update(LitemallUser user) {
    // 针对先前的历史数据, 进行挂接, 该接口是幂等的
    tryToMontionPerson(user);
    userMapper.updateByPrimaryKeySelective(user);
  }

  @Override
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
  @Override
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

  @Override
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
  @Override
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
  @Override
  public int count() {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andDeletedEqualTo(false);

    return (int) userMapper.countByExample(example);
  }
  @Override
  public int countByAdminId(Integer adminId) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);

    return (int) userMapper.countByExample(example);
  }
  @Override
  public List<LitemallUser> queryByUsername(String username) {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
    return userMapper.selectByExample(example);
  }
  @Override
  public List<LitemallUser> queryByMobile(String mobile) {
    LitemallUserExample example = new LitemallUserExample();
    example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
    return userMapper.selectByExample(example);
  }
  @Override
  public List<LitemallUser> queryByAdminId(Integer adminId) {
    LitemallUserExample example = new LitemallUserExample();
    LitemallUserExample.Criteria criteria = example.createCriteria();
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    criteria.andDeletedEqualTo(false);
    return userMapper.selectByExample(example);
  }

  @Override
  public void deleteById(Integer id) {
    userMapper.logicalDeleteByPrimaryKey(id);
  }
}
