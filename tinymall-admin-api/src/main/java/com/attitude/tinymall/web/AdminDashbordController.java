package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.service.LitemallGoodsService;
import com.attitude.tinymall.service.LitemallOrderService;
import com.attitude.tinymall.service.LitemallProductService;
import com.attitude.tinymall.service.LitemallUserService;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/dashboard")
public class AdminDashbordController {

  private final Log logger = LogFactory.getLog(AdminDashbordController.class);

  @Autowired
  private LitemallUserService userService;
  @Autowired
  private LitemallGoodsService goodsService;
  @Autowired
  private LitemallProductService productService;
  @Autowired
  private LitemallOrderService orderService;

  @GetMapping("")
  public Object info(@LoginAdmin Integer adminId) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    int userTotal;
    int goodsTotal;
    int productTotal;
    int orderTotal;
    if (adminId == 0) {
      userTotal = userService.count();
      goodsTotal = goodsService.count();
      productTotal = productService.count();
      orderTotal = orderService.count();
    } else {
      userTotal = userService.countByAdminId(adminId);
      goodsTotal = goodsService.countByAdminId(adminId);
      productTotal = productService.countByAdminId(adminId);
      orderTotal = orderService.countByAdminId(adminId);
    }

    Map<String, Integer> data = new HashMap<>(4);
    data.put("userTotal", userTotal);
    data.put("goodsTotal", goodsTotal);
    data.put("productTotal", productTotal);
    data.put("orderTotal", orderTotal);

    return ResponseUtil.ok(data);
  }

}
