package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallAddressMapper;
import com.attitude.tinymall.dao.LitemallAdminMapper;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallAddressExample;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.service.LitemallAddressService;
import com.attitude.tinymall.service.LitemallRegionService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallAddressServiceImpl implements LitemallAddressService {

  @Autowired
  private LitemallAddressMapper addressMapper;

  @Autowired
  private LitemallAdminMapper adminMapper;

  @Autowired
  private LitemallAdminServiceImpl adminService;

  @Autowired
  private LitemallRegionService regionService;

  @Override
  public List<LitemallAddress> queryByUid(Integer uid) {
    LitemallAddressExample example = new LitemallAddressExample();
    example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
    return addressMapper.selectByExample(example);
  }

  @Override
  public LitemallAddress findById(Integer id) {
    return addressMapper.selectByPrimaryKey(id);
  }

  @Override
  public int add(LitemallAddress address, String appId) {
    LitemallAdmin litemallAdmin = adminService.findAdminByOwnerId(appId);
    if (null != litemallAdmin) {
      // 关联外键
      address.setAdminId(litemallAdmin.getId());
    }
    return addressMapper.insertSelective(address);
  }

  @Override
  public int update(LitemallAddress address) {
    return addressMapper.updateByPrimaryKeySelective(address);
  }

  @Override
  public void delete(Integer id) {
    addressMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public LitemallAddress findDefault(Integer userId) {
    LitemallAddressExample example = new LitemallAddressExample();
    example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
    return addressMapper.selectOneByExample(example);
  }

  @Override
  public void resetDefault(Integer userId) {
    LitemallAddress address = new LitemallAddress();
    address.setIsDefault(false);
    LitemallAddressExample example = new LitemallAddressExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    addressMapper.updateByExampleSelective(address, example);
  }

  @Override
  public List<LitemallAddress> listAdminAddressByAdminId(Integer adminId, Integer userId,
      String name, Integer page,
      Integer limit, String sort, String order) {
    LitemallAddressExample example = new LitemallAddressExample();
    LitemallAddressExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, limit);
    return addressMapper.selectByExample(example);
  }

  @Override
  public int countAdminAddressByAdminId(Integer adminId, Integer userId, String name) {

    LitemallAddressExample example = new LitemallAddressExample();
    LitemallAddressExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (adminId != 0) {
      criteria.andAdminIdEqualTo(adminId);
    }
    if (!StringUtils.isEmpty(name)) {
      criteria.andNameLike("%" + name + "%");
    }
    criteria.andDeletedEqualTo(false);

    return (int) addressMapper.countByExample(example);
  }

  @Override
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

  @Override
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

  @Override
  public void updateById(LitemallAddress address) {
    addressMapper.updateByPrimaryKeySelective(address);
  }

  @Override
  public String getFullDetailAddress(LitemallAddress tinymallAddress) {
    Integer provinceId = tinymallAddress.getProvinceId();
    Integer cityId = tinymallAddress.getCityId();
    Integer areaId = tinymallAddress.getAreaId();
    String provinceName = regionService.findById(provinceId).getName();
    String cityName = regionService.findById(cityId).getName();
    String areaName = regionService.findById(areaId).getName();
    String fullRegion = provinceName + " " + cityName + " " + areaName;
    return fullRegion + " " + tinymallAddress.getAddress()+tinymallAddress.getAddressDetail();
  }
}
