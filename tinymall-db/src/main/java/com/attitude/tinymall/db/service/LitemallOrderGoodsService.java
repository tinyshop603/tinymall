package com.attitude.tinymall.db.service;

import com.attitude.tinymall.db.dao.LitemallOrderGoodsMapper;
import com.attitude.tinymall.db.domain.LitemallOrder;
import com.attitude.tinymall.db.domain.LitemallOrderGoods;
import com.attitude.tinymall.db.domain.LitemallOrderGoodsExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LitemallOrderGoodsService {
    @Resource
    private LitemallOrderGoodsMapper orderGoodsMapper;

    public int add(LitemallOrderGoods orderGoods) {
        return orderGoodsMapper.insertSelective(orderGoods);
    }

    public List<LitemallOrderGoods> queryByOid(Integer orderId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }
    public List<LitemallOrderGoods> findByOidAndGid(Integer orderId, Integer goodsId) {
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdEqualTo(orderId).andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }

    public List<LitemallOrderGoods> listOrderWithGoodsByOrder(List<LitemallOrder> orderList) {
        List<Integer> orderIds = new ArrayList<Integer>();
        int num = 0;
        for(LitemallOrder order : orderList){
            orderIds.add(order.getId());
        }
        LitemallOrderGoodsExample example = new LitemallOrderGoodsExample();
        example.or().andOrderIdIn(orderIds).andDeletedEqualTo(false);
        return orderGoodsMapper.selectByExample(example);
    }

}
