package com.attitude.tinymall.web;

import com.attitude.tinymall.common.Constants;
import com.attitude.tinymall.util.ResponseUtil;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallCategory;
import com.attitude.tinymall.domain.LitemallGoods;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.service.LitemallCategoryService;
import com.attitude.tinymall.service.LitemallGoodsService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
  private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
      .setNameFormat("category_and_goods-request-pool-%d").build();
  /**
   * 当前百度返回的默认的最多天数
   */
  private final int CORE_SIZE = 5;
  private ExecutorService executorService = new ThreadPoolExecutor(CORE_SIZE, CORE_SIZE,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(), namedThreadFactory);


  private static String replacePic = "?x-oss-process=image/resize,m_fixed,h_120,w_120";

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
    if (cur.getParentId() <= Constants.ADMIN_USER_LIMIT) {
      return ResponseUtil.ok(cur);
    } else {
      return ResponseUtil.fail();
    }

  }

  // TODO 分类改造首页 返回全部商品 采用图片懒加载及锚点技术实现
  @GetMapping("/new/category")
  public Object getAllDetail(@PathVariable("storeId") String appid) throws InterruptedException {
    System.out.println(
        "category/开始:" + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
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
    // 当前所有L2分类
    List<LitemallCategory> categoryList = categoryService.queryByPid(storeId);
    Map<Integer, List<LitemallGoods>> allGoodsMap = new ConcurrentHashMap<>(16);
    final CountDownLatch countDownLatch = new CountDownLatch(categoryList.size());

    if (categoryList.size() > 0) {
      for (int i = 0; i < categoryList.size(); i++) {
        final int categoryId = categoryList.get(i).getId();
        executorService.execute(() -> {
          try {
            List<LitemallGoods> goodsList = goodsService
                .querySelective(categoryId, null, null,
                    null, null, 0,
                    Integer.MAX_VALUE, null, null);
            allGoodsMap.put(categoryId, goodsList);
          } finally {
            countDownLatch.countDown();
          }
        });
      }
    }
    countDownLatch.await();
    List<List<LitemallGoods>> allGoodsList = Collections.synchronizedList(new ArrayList<>());
    categoryList.forEach((value) -> {
      Integer categoryId = value.getId();
      allGoodsList.add(allGoodsMap.get(categoryId));
    });
    Map<String, Object> data = new HashMap();
    data.put("categoryList", categoryList);
    data.put("allGoodsList", allGoodsList);
//    int categoryId = 0;
//    if (categoryList.size() > 0) {
//      LitemallCategory currentCategory = categoryList.get(0);
//      categoryId = currentCategory.getId();
//      data.put("currentCategory", currentCategory);
//    }
//    if (categoryId != 0) {
//      //商品列表
//      List<LitemallGoods> goodsList = goodsService
//              .querySelective(categoryId, null, null, null, null, 0, Integer.MAX_VALUE, null, null);
//      //截取图片格式
//      if (goodsList.size() > 0) {
//        for (int i = 0; i < goodsList.size(); i++) {
//          String oldPicUrl = goodsList.get(i).getListPicUrl();
//          String newPicUrl = new String();
//          if(oldPicUrl.indexOf("fuss10")!= -1){
//            newPicUrl =  oldPicUrl.substring(0, oldPicUrl.indexOf("?")) + replacePic;
//          }else{
//            newPicUrl = oldPicUrl;
//          }
//
//          goodsList.get(i).setListPicUrl(newPicUrl);
//        }
//      }
//      data.put("currentSubCategoryList", goodsList);
//    }
//    data.put("categoryList", categoryList);
    return ResponseUtil.ok(data);


  }

  @GetMapping("/location")
  public Object getConsumerLocation(double latitude, double longitude) {
    Map<String, Object> data = new HashMap();
    data.put("locationName", "北店嘉园南里");
    data.put("canDistribution", false);
    return ResponseUtil.ok(data);
  }

  @GetMapping("/category")
  public Object getMallCategoryDetail(@PathVariable("storeId") String appid) {
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
          String newPicUrl = goodsList.get(i).getListPicUrl() + replacePic;
          goodsList.get(i).setListPicUrl(newPicUrl);
        }
      }
      data.put("currentSubCategoryList", goodsList);
    }
    data.put("categoryList", categoryList);
    return ResponseUtil.ok(data);


  }

}
