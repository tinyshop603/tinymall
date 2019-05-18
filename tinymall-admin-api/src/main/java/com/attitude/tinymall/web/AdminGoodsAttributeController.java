package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.domain.LitemallGoodsAttribute;
import com.attitude.tinymall.service.LitemallGoodsAttributeService;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/goods-attribute")
public class AdminGoodsAttributeController {

  private final Log logger = LogFactory.getLog(AdminGoodsAttributeController.class);

  @Autowired
  private LitemallGoodsAttributeService goodsAttributeService;

  @GetMapping("/list")
  public Object list(@LoginAdmin Integer adminId,
      Integer goodsId,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      String sort, String order) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    List<LitemallGoodsAttribute> goodsAttributeList = goodsAttributeService
        .querySelective(goodsId, adminId, page, limit, sort, order);
    int total = goodsAttributeService.countSelective(goodsId, adminId, page, limit, sort, order);
    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("items", goodsAttributeList);

    return ResponseUtil.ok(data);
  }

  @PostMapping("/create")
  public Object create(@LoginAdmin Integer adminId,
      @RequestBody LitemallGoodsAttribute goodsAttribute) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    goodsAttributeService.add(goodsAttribute);
    return ResponseUtil.ok(goodsAttribute);
  }

  @GetMapping("/read")
  public Object read(@LoginAdmin Integer adminId, Integer id) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }

    if (id == null) {
      return ResponseUtil.badArgument();
    }

    LitemallGoodsAttribute goodsAttribute = goodsAttributeService.findById(id);
    return ResponseUtil.ok(goodsAttribute);
  }

  @PostMapping("/update")
  public Object update(@LoginAdmin Integer adminId,
      @RequestBody LitemallGoodsAttribute goodsAttribute) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    goodsAttributeService.updateById(goodsAttribute);
    return ResponseUtil.ok(goodsAttribute);
  }

  @PostMapping("/delete")
  public Object delete(@LoginAdmin Integer adminId,
      @RequestBody LitemallGoodsAttribute goodsAttribute) {
    if (adminId == null) {
      return ResponseUtil.unlogin();
    }
    goodsAttributeService.deleteById(goodsAttribute.getId());
    return ResponseUtil.ok();
  }

}
