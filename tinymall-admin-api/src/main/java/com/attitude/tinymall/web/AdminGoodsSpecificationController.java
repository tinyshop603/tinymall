package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.domain.LitemallGoodsSpecification;
import com.attitude.tinymall.service.LitemallGoodsSpecificationService;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/goods-specification")
public class AdminGoodsSpecificationController {

  private final Log logger = LogFactory.getLog(AdminGoodsSpecificationController.class);

  @Autowired
  private LitemallGoodsSpecificationService goodsSpecificationService;

  @GetMapping("/list")
  public Object list(@LoginAdmin Integer adminId,
      Integer goodsId,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      String sort, String order) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    // 此处已经处理了相关的商品属性信息
    List<LitemallGoodsSpecification> goodsSpecificationList = goodsSpecificationService
        .querySelective(goodsId, adminId, page, limit, sort, order);
    int total = goodsSpecificationService
        .countSelective(goodsId, adminId, page, limit, sort, order);
    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("items", goodsSpecificationList);

    return ResponseUtil.ok(data);
  }

  @PostMapping("/create")
  public Object create(@LoginAdmin Integer adminId,
      @RequestBody LitemallGoodsSpecification goodsSpecification) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    goodsSpecificationService.add(goodsSpecification);
    return ResponseUtil.ok(goodsSpecification);
  }

  @GetMapping("/read")
  public Object read(@LoginAdmin Integer adminId, Integer id) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    if (id == null) {
      return ResponseUtil.badArgument();
    }

    LitemallGoodsSpecification goodsSpecification = goodsSpecificationService.findById(id);
    return ResponseUtil.ok(goodsSpecification);
  }

  @PostMapping("/update")
  public Object update(@LoginAdmin Integer adminId,
      @RequestBody LitemallGoodsSpecification goodsSpecification) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    goodsSpecificationService.updateById(goodsSpecification);
    return ResponseUtil.ok(goodsSpecification);
  }

  @PostMapping("/delete")
  public Object delete(@LoginAdmin Integer adminId,
      @RequestBody LitemallGoodsSpecification goodsSpecification) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    goodsSpecificationService.deleteById(goodsSpecification.getId());
    return ResponseUtil.ok();
  }

  @GetMapping("/volist")
  public Object volist(@LoginAdmin Integer adminId, Integer id) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    if (id == null) {
      return ResponseUtil.badArgument();
    }

    Object goodsSpecificationVoList = goodsSpecificationService.getSpecificationVoList(id);
    return ResponseUtil.ok(goodsSpecificationVoList);
  }

}
