package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.domain.LitemallAddress;
import com.attitude.tinymall.domain.LitemallOrder;
import com.attitude.tinymall.domain.dada.ResponseEntity;
import com.attitude.tinymall.domain.dada.order.AddOrderParams;
import com.attitude.tinymall.domain.dada.shop.ShopDetailResult;
import com.attitude.tinymall.domain.dada.testorder.AcceptOrderParams;
import com.attitude.tinymall.service.LitemallDeliveryDetailService;
import com.attitude.tinymall.util.RegexUtil;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 */

@RestController
@RequestMapping("/dada/order")
public class AdminDadaController {
    @Autowired
    private LitemallDeliveryDetailService litemallDeliveryDetailService;

   //申请配送
    @RequestMapping("/addOrder")
    public Object dadaAddOrder(Integer orderId){
        String status = (String)litemallDeliveryDetailService.dadaAddOrder(orderId);
        if("0".equals(status)){
            return ResponseUtil.ok();
        }else{
            return  ResponseUtil.fail();
        }
    }

    //接受订单
    @RequestMapping("/callback/status")
    public Object dadaCallback(ResponseEntity orderResult ){
        return orderResult.getStatus();
    }
}
