package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallOrderGoodsMapper;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderGoods;
import com.attitude.tinymall.domain.LitemallOrderGoodsExample;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.attitude.tinymall.service.LitemallOrderGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LitemallOrderGoodsServiceImpl implements LitemallOrderGoodsService {
    @Autowired
    private LitemallOrderGoodsMapper orderGoodsMapper;
    @Override
    public int add(LitemallOrderGoods orderGoods) {
        return orderGoodsMapper.insertSelective(orderGoods);
    }
    @Override
    public List<LitemallOrderGoods> queryByOid(Integer orderId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }
    @Override
    public List<LitemallOrderGoods> findByOidAndGid(Integer orderId, Integer goodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }
    @Override
    public List<LitemallOrderGoods> listOrderWithGoodsByOrder(List<LitemallOrder> orderList) {
        if (orderList == null || orderList.size() == 0) {
            return new ArrayList<>();
        }
        List<Integer> orderIds = new ArrayList<Integer>();
        int num = 0;
        for (LitemallOrder order : orderList) {
            orderIds.add(order.getId());
        }
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdIn(orderIds).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }

}
