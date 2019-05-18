package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallGoodsSpecificationMapper;
import com.attitude.tinymall.domain.LitemallGoodsSpecification;
import com.attitude.tinymall.domain.LitemallGoodsSpecificationExample;
import com.attitude.tinymall.service.LitemallGoodsSpecificationService;
import com.github.pagehelper.PageHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LitemallGoodsSpecificationServiceImpl implements LitemallGoodsSpecificationService {

  @Autowired
  private LitemallGoodsSpecificationMapper goodsSpecificationMapper;
  @Autowired
  private LitemallGoodsServiceImpl goodsService;

 @Override
  public List<LitemallGoodsSpecification> queryByGid(Integer id) {
    LitemallGoodsSpecificationExample example = new LitemallGoodsSpecificationExample();
    example.or().andGoodsIdEqualTo(id).andDeletedEqualTo(false);
    return goodsSpecificationMapper.selectByExample(example);
  }
  @Override
  public LitemallGoodsSpecification findById(Integer id) {
    return goodsSpecificationMapper.selectByPrimaryKey(id);
  }

  @Override
  public List<LitemallGoodsSpecification> querySelective(Integer goodsId, Integer adminId,
      Integer page, Integer size, String sort, String order) {

    LitemallGoodsSpecificationExample example = new LitemallGoodsSpecificationExample();
    LitemallGoodsSpecificationExample.Criteria criteria = example.createCriteria();

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    List<Integer> litemallGoodsIdsOfAdmin = goodsService.getAdminGoodsIds(adminId);
    if (litemallGoodsIdsOfAdmin.size() > 0) {
      criteria.andGoodsIdIn(litemallGoodsIdsOfAdmin);
    }

    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return goodsSpecificationMapper.selectByExample(example);
  }
  @Override
  public int countSelective(Integer goodsId, Integer adminId, Integer page, Integer size,
      String sort,
      String order) {
    LitemallGoodsSpecificationExample example = new LitemallGoodsSpecificationExample();
    LitemallGoodsSpecificationExample.Criteria criteria = example.createCriteria();

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }

    List<Integer> litemallGoodsIdsOfAdmin = goodsService.getAdminGoodsIds(adminId);
    if (litemallGoodsIdsOfAdmin.size() > 0) {
      criteria.andGoodsIdIn(litemallGoodsIdsOfAdmin);
    }
    criteria.andDeletedEqualTo(false);

    return (int) goodsSpecificationMapper.countByExample(example);
  }
  @Override
  public void updateById(LitemallGoodsSpecification goodsSpecification) {
    goodsSpecificationMapper.updateByPrimaryKeySelective(goodsSpecification);
  }
  @Override
  public void deleteById(Integer id) {
    goodsSpecificationMapper.logicalDeleteByPrimaryKey(id);
  }
  @Override
  public void add(LitemallGoodsSpecification goodsSpecification) {
    goodsSpecificationMapper.insertSelective(goodsSpecification);
  }
  @Override
  public Integer[] queryIdsByGid(Integer goodsId) {
    List<LitemallGoodsSpecification> goodsSpecificationList = queryByGid(goodsId);
    Integer[] ids = new Integer[goodsSpecificationList.size()];
    for (int i = 0; i < ids.length; i++) {
      ids[i] = goodsSpecificationList.get(i).getId();
    }
    return ids;
  }

  private class VO {

    private String name;
    private List<LitemallGoodsSpecification> valueList;

    public void setName(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public List<LitemallGoodsSpecification> getValueList() {
      return valueList;
    }

    public void setValueList(List<LitemallGoodsSpecification> valueList) {
      this.valueList = valueList;
    }
  }

  /**
   * [ { name: '', valueList: [ {}, {}] }, { name: '', valueList: [ {}, {}] } ]
   */
  @Override
  public Object getSpecificationVoList(Integer id) {
    List<LitemallGoodsSpecification> goodsSpecificationList = queryByGid(id);

    Map<String, VO> map = new HashMap<>();
    List<VO> specificationVoList = new ArrayList<>();

    for (LitemallGoodsSpecification goodsSpecification : goodsSpecificationList) {
      String specification = goodsSpecification.getSpecification();
      VO goodsSpecificationVo = map.get(specification);
      if (goodsSpecificationVo == null) {
        goodsSpecificationVo = new VO();
        goodsSpecificationVo.setName(specification);
        List<LitemallGoodsSpecification> valueList = new ArrayList<>();
        valueList.add(goodsSpecification);
        goodsSpecificationVo.setValueList(valueList);
        map.put(specification, goodsSpecificationVo);
        specificationVoList.add(goodsSpecificationVo);
      } else {
        List<LitemallGoodsSpecification> valueList = goodsSpecificationVo.getValueList();
        valueList.add(goodsSpecification);
      }
    }

    return specificationVoList;
  }

}
