package com.attitude.tinymall.wx.web;

import com.attitude.tinymall.core.util.ResponseUtil;
import com.attitude.tinymall.db.domain.LitemallAdmin;
import com.attitude.tinymall.db.domain.LitemallCategory;
import com.attitude.tinymall.db.domain.LitemallGoods;
import com.attitude.tinymall.db.service.LitemallAdminService;
import com.attitude.tinymall.db.service.LitemallCategoryService;
import com.attitude.tinymall.db.service.LitemallGoodsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx/{storeId}/mall")
public class WxMallController {

  @Autowired
  private LitemallCategoryService categoryService;

  @Autowired
  private LitemallAdminService litemallAdminService;

  @Autowired
  private LitemallGoodsService goodsService;

  @GetMapping
  public Object getMallDetail(@PathVariable("storeId") String appid) {
    LitemallAdmin litemallAdmin = litemallAdminService.findAdminByOwnerId(appid);

    // 此处查询出的数据必定size为1或者0
    List<LitemallCategory> litemallCategories = categoryService
        .queryByPid(litemallAdmin.getId());
    Integer id = null;
    if (litemallCategories != null && litemallCategories.size() == 1) {
      id = litemallCategories.get(0).getId();
    }
    if (id == null) {
      return ResponseUtil.badArgument();
    }
    LitemallCategory cur = categoryService.findById(id);
    if (cur.getParentId() <= LitemallAdmin.USER_LIMIT) {
      return ResponseUtil.ok(cur);
    } else {
      return ResponseUtil.fail();
    }

  }

  @GetMapping("/category")
  public Object getMallCategoryDetail(@PathVariable("storeId") String appid) {
    final String replacePic = "?imageMogr/thumbnail/!120x120r/gravity/Center/crop/120x120/";
    LitemallAdmin litemallAdmin = litemallAdminService.findAdminByOwnerId(appid);
    // 此处查询出的数据必定size为1或者0
    List<LitemallCategory> litemallCategories = categoryService
        .queryByPid(litemallAdmin.getId());
    Integer storeId = null;
    if (litemallCategories != null && litemallCategories.size() == 1) {
      storeId = litemallCategories.get(0).getId();
    }

    if (storeId == null) {
      return ResponseUtil.badArgument();
    }
    // 当前分类
    List<LitemallCategory> categoryList = categoryService.queryByPid(storeId);
    Map<String, Object> data = new HashMap();
    int categoryId = 0;
    if (categoryList.size() > 0) {
      LitemallCategory currentCategory = categoryList.get(0);
      categoryId = currentCategory.getId();
      data.put("currentCategory", currentCategory);
    }
    if (categoryId != 0) {
      //商品列表
      List<LitemallGoods> goodsList = goodsService
          .querySelective(categoryId, null, null, null, null, 0, Integer.MAX_VALUE, null, null);
      //截取图片格式
      if (goodsList.size() > 0) {
        for (int i = 0; i < goodsList.size(); i++) {
          String oldPicUrl = goodsList.get(i).getListPicUrl();
          String newPicUrl = oldPicUrl.substring(0, oldPicUrl.indexOf("?")) + replacePic;
          goodsList.get(i).setListPicUrl(newPicUrl);
        }
      }
      data.put("currentSubCategoryList", goodsList);
    }
    data.put("categoryList", categoryList);
    return ResponseUtil.ok(data);


  }

}