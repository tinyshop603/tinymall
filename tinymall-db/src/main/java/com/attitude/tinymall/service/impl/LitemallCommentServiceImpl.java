package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallCommentMapper;
import com.attitude.tinymall.domain.LitemallComment;
import com.attitude.tinymall.domain.LitemallCommentExample;
import com.attitude.tinymall.service.LitemallCommentService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class LitemallCommentServiceImpl implements LitemallCommentService {

  @Autowired
  private LitemallCommentMapper commentMapper;
  @Autowired
  private LitemallGoodsServiceImpl goodsService;

  @Override
  public List<LitemallComment> queryGoodsByGid(Integer id, int offset, int limit) {
    LitemallCommentExample example = new LitemallCommentExample();
    example.setOrderByClause(LitemallComment.Column.addTime.desc());
    example.or().andValueIdEqualTo(id).andTypeIdEqualTo((byte) 0).andDeletedEqualTo(false);
    PageHelper.startPage(offset, limit);
    return commentMapper.selectByExample(example);
  }

  @Override
  public int countGoodsByGid(Integer id, int offset, int limit) {
    LitemallCommentExample example = new LitemallCommentExample();
    example.or().andValueIdEqualTo(id).andTypeIdEqualTo((byte) 0).andDeletedEqualTo(false);
    return (int) commentMapper.countByExample(example);
  }

  @Override
  public List<LitemallComment> query(Byte typeId, Integer valueId, Integer showType, Integer offset,
      Integer limit) {
    LitemallCommentExample example = new LitemallCommentExample();
    example.setOrderByClause(LitemallComment.Column.addTime.desc());
    if (showType == 0) {
      example.or().andValueIdEqualTo(valueId).andTypeIdEqualTo(typeId).andDeletedEqualTo(false);
    } else if (showType == 1) {
      example.or().andValueIdEqualTo(valueId).andTypeIdEqualTo(typeId).andHasPictureEqualTo(true)
          .andDeletedEqualTo(false);
    } else {
      Assert.state(false, "showType不支持");
    }
    PageHelper.startPage(offset, limit);
    return commentMapper.selectByExample(example);
  }

  @Override
  public int count(Byte typeId, Integer valueId, Integer showType, Integer offset, Integer size) {
    LitemallCommentExample example = new LitemallCommentExample();
    if (showType == 0) {
      example.or().andValueIdEqualTo(valueId).andTypeIdEqualTo(typeId).andDeletedEqualTo(false);
    } else if (showType == 1) {
      example.or().andValueIdEqualTo(valueId).andTypeIdEqualTo(typeId).andHasPictureEqualTo(true)
          .andDeletedEqualTo(false);
    } else {
      Assert.state(false, "");
    }
    return (int) commentMapper.countByExample(example);
  }

  @Override
  public Integer save(LitemallComment comment) {
    return commentMapper.insertSelective(comment);
  }

  @Override
  public void update(LitemallComment comment) {
    commentMapper.updateByPrimaryKeySelective(comment);
  }

  @Override
  public List<LitemallComment> listAdminCommentsByAdminId(Integer adminId, String userId,
      String valueId, Integer page, Integer size, String sort, String order) {
    LitemallCommentExample example = new LitemallCommentExample();
    example.setOrderByClause(LitemallComment.Column.addTime.desc());
    LitemallCommentExample.Criteria criteria = example.createCriteria();

    List<Integer> adminGoodsIds = goodsService.getAdminGoodsIds(adminId);
    if (adminGoodsIds.size() > 0) {
      criteria.andValueIdIn(adminGoodsIds);
    }

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(valueId)) {
      criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeIdEqualTo((byte) 0);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return commentMapper.selectByExample(example);
  }

  @Override
  public int countAdminCommentsByAdminId(Integer adminId, String userId, String valueId) {
    LitemallCommentExample example = new LitemallCommentExample();
    LitemallCommentExample.Criteria criteria = example.createCriteria();
    List<Integer> adminGoodsIds = goodsService.getAdminGoodsIds(adminId);
    if (adminGoodsIds.size() > 0) {
      criteria.andValueIdIn(adminGoodsIds);
    }
    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(valueId)) {
      criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeIdEqualTo((byte) 0);
    }
    criteria.andDeletedEqualTo(false);

    return (int) commentMapper.countByExample(example);
  }

  @Override
  public List<LitemallComment> querySelective(String userId, String valueId, Integer page,
      Integer size, String sort, String order) {
    LitemallCommentExample example = new LitemallCommentExample();
    example.setOrderByClause(LitemallComment.Column.addTime.desc());
    LitemallCommentExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(valueId)) {
      criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeIdEqualTo((byte) 0);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, size);
    return commentMapper.selectByExample(example);
  }

  @Override
  public int countSelective(String userId, String valueId, Integer page, Integer size, String sort,
      String order) {
    LitemallCommentExample example = new LitemallCommentExample();
    LitemallCommentExample.Criteria criteria = example.createCriteria();

    if (!StringUtils.isEmpty(userId)) {
      criteria.andUserIdEqualTo(Integer.valueOf(userId));
    }
    if (!StringUtils.isEmpty(valueId)) {
      criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeIdEqualTo((byte) 0);
    }
    criteria.andDeletedEqualTo(false);

    return (int) commentMapper.countByExample(example);
  }

  @Override
  public void updateById(LitemallComment comment) {
    commentMapper.updateByPrimaryKeySelective(comment);
  }

  @Override
  public void deleteById(Integer id) {
    commentMapper.logicalDeleteByPrimaryKey(id);
  }

  @Override
  public void add(LitemallComment comment) {
    commentMapper.insertSelective(comment);
  }

  @Override
  public LitemallComment findById(Integer id) {
    return commentMapper.selectByPrimaryKey(id);
  }
}
