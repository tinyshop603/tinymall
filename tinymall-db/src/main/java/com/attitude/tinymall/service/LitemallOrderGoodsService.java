package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallOrderGoodsMapper;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.LitemallOrderGoods;
import com.attitude.tinymall.domain.LitemallOrderGoodsExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public interface LitemallOrderGoodsService {

     int add(LitemallOrderGoods orderGoods);

     List<LitemallOrderGoods> queryByOid(Integer orderId);
    
     List<LitemallOrderGoods> findByOidAndGid(Integer orderId, Integer goodsId) ;

     List<LitemallOrderGoods> listOrderWithGoodsByOrder(List<LitemallOrder> orderList) ;

}
