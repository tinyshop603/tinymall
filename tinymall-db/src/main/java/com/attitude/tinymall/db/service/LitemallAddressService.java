package com.attitude.tinymall.db.service;

import com.attitude.tinymall.db.dao.LitemallAdMapper;
import com.attitude.tinymall.db.dao.LitemallAdminMapper;
import com.attitude.tinymall.db.domain.LitemallAdmin;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.db.dao.LitemallAddressMapper;
import com.attitude.tinymall.db.domain.LitemallAddress;
import com.attitude.tinymall.db.domain.LitemallAddressExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallAddressService {

  @Resource
  private LitemallAddressMapper addressMapper;

  @Resource
  private LitemallAdminMapper adminMapper;


  public List<LitemallAddress> queryByUid(Integer uid) {
    LitemallAddressExample example = new LitemallAddressExample();
    example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
    return addressMapper.selectByExample(example);
  }

  public LitemallAddress findById(Integer id) {
    return addressMapper.selectByPrimaryKey(id);
  }

  public int add(LitemallAddress address, String appId) {
    LitemallAdmin litemallAdmin = adminMapper.findAdminByOwnerId(appId);
    if (null != litemallAdmin) {
      // 关联外键
      address.setAdminId(litemallAdmin.getId());
    }
    return addressMapper.insertSelective(address);
  }

  public int update(LitemallAddress address) {
    return addressMapper.updateByPrimaryKeySelective(address);
  }

  public void delete(Integer id) {
    addressMapper.logicalDeleteByPrimaryKey(id);
  }

  public LitemallAddress findDefault(Integer userId) {
    LitemallAddressExample example = new LitemallAddressExample();
    example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
    return addressMapper.selectOneByExample(example);
  }

  public void resetDefault(Integer userId) {
    LitemallAddress address = new LitemallAddress();
    address.setIsDefault(false);
    LitemallAddressExample example = new LitemallAddressExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    addressMapper.updateByExampleSelective(address, example);
  }

  public List<LitemallAddress> querySelective(Integer userId, String name, Integer page,
      Integer limit, String sort, String order) {
    LitemallAddressExample example = new LitemallAddressExample();
    LitemallAddressExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, limit);
    return addressMapper.selectByExample(example);
  }

  public int countSelective(Integer userId, String name, Integer page, Integer limit, String sort,
      String order) {
    LitemallAddressExample example = new LitemallAddressExample();
    LitemallAddressExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    criteria.andDeletedEqualTo(false);

    return (int) addressMapper.countByExample(example);
  }

  public void updateById(LitemallAddress address) {
    addressMapper.updateByPrimaryKeySelective(address);
  }
}
