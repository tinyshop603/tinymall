package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallCartMapper;
import com.attitude.tinymall.domain.LitemallCart;
import com.attitude.tinymall.domain.LitemallCartExample;
import com.attitude.tinymall.domain.LitemallUser;
import com.attitude.tinymall.service.LitemallCartService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LitemallCartServiceImpl implements LitemallCartService {

  @Autowired
  private LitemallCartMapper cartMapper;
  @Autowired
  private LitemallGoodsServiceImpl goodsService;
  @Autowired
  private LitemallUserServiceImpl userService;

  @Override
  public LitemallCart queryExist(Integer goodsId, Integer productId, Integer userId) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andGoodsIdEqualTo(goodsId).andProductIdEqualTo(productId).andUserIdEqualTo(userId)
        .andDeletedEqualTo(false);
    return cartMapper.selectOneByExample(example);
  }

  @Override
  public void add(LitemallCart cart) {
    cartMapper.insertSelective(cart);
  }

  @Override
  public void update(LitemallCart cart) {
    cartMapper.updateByPrimaryKey(cart);
  }

  @Override
  public List<LitemallCart> queryByUid(int userId) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    return cartMapper.selectByExample(example);
  }

  @Override
  public List<LitemallCart> queryByUidAndChecked(Integer userId) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true).andDeletedEqualTo(false);
    return cartMapper.selectByExample(example);
  }

  @Override
  public List<LitemallCart> queryByUidAndSid(int userId, String sessionId) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
    return cartMapper.selectByExample(example);
  }

  @Override
  public int delete(List<Integer> productIdList, int userId) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andUserIdEqualTo(userId).andProductIdIn(productIdList);
    return cartMapper.logicalDeleteByExample(example);
  }

  @Override
  public LitemallCart findById(Integer id) {
    return cartMapper.selectByPrimaryKey(id);
  }

  @Override
  public int updateCheck(Integer userId, List<Integer> idsList, Boolean checked) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andUserIdEqualTo(userId).andProductIdIn(idsList).andDeletedEqualTo(false);
    LitemallCart cart = new LitemallCart();
    cart.setChecked(checked);
    return cartMapper.updateByExampleSelective(cart, example);
  }

  @Override
  public void clearGoods(Integer userId) {
    LitemallCartExample example = new LitemallCartExample();
    example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true);
    LitemallCart cart = new LitemallCart();
    cart.setDeleted(true);
    cartMapper.updateByExampleSelective(cart, example);
  }

  @Override
  public List<LitemallCart> listAdminCartsByAdminId(Integer adminId, Integer userId,
      Integer goodsId,
      Integer page, Integer limit, String sort, String order) {
    LitemallCartExample example = new LitemallCartExample();
    LitemallCartExample.Criteria criteria = example.createCriteria();

    List<Integer> adminGoodsIds = goodsService.getAdminGoodsIds(adminId);

    if (adminGoodsIds.size() > 0) {
      criteria.andGoodsIdIn(adminGoodsIds);
    }

    List<Integer> adminUsers = userService.queryByAdminId(adminId).stream()
        .mapToInt(LitemallUser::getId).boxed().collect(
            Collectors.toList());

    if (adminUsers.size() > 0) {
      criteria.andUserIdIn(adminUsers);
    }

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, limit);
    return cartMapper.selectByExample(example);
  }

  @Override
  public int countAdminCartByAdminId(Integer adminId, Integer userId, Integer goodsId) {
    LitemallCartExample example = new LitemallCartExample();
    LitemallCartExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }

    List<Integer> adminUsers = userService.queryByAdminId(adminId).stream()
        .mapToInt(LitemallUser::getId).boxed().collect(
            Collectors.toList());

    if (adminUsers.size() > 0) {
      criteria.andUserIdIn(adminUsers);
    }

    List<Integer> adminGoodsIds = goodsService.getAdminGoodsIds(adminId);

    if (adminGoodsIds.size() > 0) {
      criteria.andGoodsIdIn(adminGoodsIds);
    }

    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    criteria.andDeletedEqualTo(false);

    return (int) cartMapper.countByExample(example);
  }

  @Override
  public List<LitemallCart> querySelective(Integer userId, Integer goodsId, Integer page,
      Integer limit, String sort, String order) {
    LitemallCartExample example = new LitemallCartExample();
    LitemallCartExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    criteria.andDeletedEqualTo(false);

    PageHelper.startPage(page, limit);
    return cartMapper.selectByExample(example);
  }

  @Override
  public int countSelective(Integer userId, Integer goodsId, Integer page, Integer limit,
      String sort, String order) {
    LitemallCartExample example = new LitemallCartExample();
    LitemallCartExample.Criteria criteria = example.createCriteria();

    if (userId != null) {
      criteria.andUserIdEqualTo(userId);
    }
    if (goodsId != null) {
      criteria.andGoodsIdEqualTo(goodsId);
    }
    criteria.andDeletedEqualTo(false);

    return (int) cartMapper.countByExample(example);
  }

  @Override
  public void deleteById(Integer id) {
    cartMapper.logicalDeleteByPrimaryKey(id);
  }
}
