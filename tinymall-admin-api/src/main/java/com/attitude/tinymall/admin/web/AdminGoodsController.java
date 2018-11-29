package com.attitude.tinymall.admin.web;


import com.attitude.tinymall.db.domain.LitemallGoodsSpecification;
import com.attitude.tinymall.db.domain.LitemallProduct;
import com.attitude.tinymall.db.service.LitemallGoodsSpecificationService;
import com.attitude.tinymall.db.service.LitemallProductService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private LitemallProductService productService;

    @Autowired
    private LitemallGoodsSpecificationService goodsSpecificationService;

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
        goods.setKeywords(goods.getName());
        goods.setCounterPrice(goods.getRetailPrice());
        goods.setListPicUrl(goods.getPrimaryPicUrl());
      //  goods.setCategoryId(Integer.parseInt(goods.getcategoryName()));
        goodsService.add(goods);
        if (goods.getId() == null) {
            return ResponseUtil.badArgument();
        }
        syncGoodsSpecification(goods.getId());
        syncProduct(goods.getId());
        return ResponseUtil.ok(goods);
    }

    /**
     * wz-2018-11-20
     * 添加商品同时添加规格
     * */
    private void syncGoodsSpecification(Integer goodsId){
        LitemallGoodsSpecification goodsSpecification = new LitemallGoodsSpecification();
        LitemallGoods goods = goodsService.findById(goodsId);
        goodsSpecification.setGoodsId(goods.getId());
        goodsSpecification.setValue("标准");
        goodsSpecification.setPicUrl(goods.getListPicUrl());
        goodsSpecification.setSpecification("规格");
        goodsSpecification.setAddTime(LocalDateTime.now());
        goodsSpecificationService.add(goodsSpecification);
    }

    /**
     * wz-2018-11-20
     * 添加商品同时添加货品
     * */
    private void syncProduct(Integer goodsId){
        LitemallGoods goods = goodsService.findById(goodsId);

        List<LitemallProduct> productList = productService.queryByGid(goodsId);

        Integer[] goodsSpecificationIds = goodsSpecificationService.queryIdsByGid(goodsId);

        LitemallProduct product = new LitemallProduct();
        product.setGoodsId(goodsId);
        product.setGoodsNumber(100);
        product.setRetailPrice(goods.getRetailPrice());
        product.setGoodsSpecificationIds(goodsSpecificationIds);
        product.setUrl(goods.getListPicUrl());
        product.setAddTime(LocalDateTime.now());
        productService.add(product);

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
