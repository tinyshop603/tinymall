package com.attitude.tinymall.admin.web;

import com.attitude.tinymall.db.domain.LitemallCategory;
import com.attitude.tinymall.db.service.LitemallCategoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.admin.annotation.LoginAdmin;
import com.attitude.tinymall.db.domain.LitemallGoods;
import com.attitude.tinymall.db.service.LitemallGoodsService;
import com.attitude.tinymall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/goods")
public class AdminGoodsController {
    private final Log logger = LogFactory.getLog(AdminGoodsController.class);

    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallCategoryService categoryService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String goodsSn, String name,String categoryId,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        // 将该用户下的所有的category全部抓取出来,查询所有的商品是该用户的
        List<LitemallGoods> goodsList = goodsService.listGoodsByAdminId(adminId,goodsSn, name,categoryId, page, limit, sort, order);
        List<LitemallCategory> categoryList = categoryService.getAdminMallCategoryByAdminId(adminId,0, 0, sort, order);
        String categoryName = null;
        Integer categoryId2 = 0;
        Map map = new HashMap();
        for(int i =0;i<categoryList.size();i++){
            categoryName = categoryList.get(i).getName();
            categoryId2 = categoryList.get(i).getId();
            if(!map.containsKey(categoryId)){
                map.put(categoryId2,categoryName);
            }
        }
         for(int j = 0;j<goodsList.size();j++){
            goodsList.get(j).setcategoryName(((String) map.get(goodsList.get(j).getCategoryId())));
        }
        int total = goodsService.countGoodsByAdminId(adminId,goodsSn, name, categoryId);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);
        data.put("categoryMap", map);
        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
      //  goods.setCategoryId(Integer.parseInt(goods.getcategoryName()));
        goodsService.add(goods);
        return ResponseUtil.ok(goods);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallGoods goods = goodsService.findById(id);
        return ResponseUtil.ok(goods);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
      //  goods.setCategoryId(Integer.parseInt(goods.getcategoryName()));
        goodsService.updateById(goods);
        return ResponseUtil.ok(goods);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallGoods goods){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        goodsService.deleteById(goods.getId());
        return ResponseUtil.ok();
    }

}
