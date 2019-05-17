package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.domain.LitemallCart;
import com.attitude.tinymall.service.LitemallCartService;
import com.attitude.tinymall.service.LitemallGoodsService;
import com.attitude.tinymall.service.LitemallProductService;
import com.attitude.tinymall.service.LitemallUserService;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/cart")
public class AdminCartController {
    private final Log logger = LogFactory.getLog(AdminCartController.class);

    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallProductService productService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       Integer userId, Integer goodsId,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.fail401();
        }
        List<LitemallCart> cartList = cartService.listAdminCartsByAdminId(adminId,userId, goodsId, page, limit, sort, order);
        int total = cartService.countAdminCartByAdminId(adminId,userId, goodsId);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", cartList);

        return ResponseUtil.ok(data);
    }

    /*
     * 目前的逻辑不支持管理员创建
     */
    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallCart cart){
        if(adminId == null){
            return ResponseUtil.fail401();
        }

        return ResponseUtil.fail501();
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.fail401();
        }

        LitemallCart cart = cartService.findById(id);
        return ResponseUtil.ok(cart);
    }

    /*
     * 目前的逻辑不支持管理员创建
     */
    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallCart cart){
        if(adminId == null){
            return ResponseUtil.fail401();
        }
        return ResponseUtil.fail501();
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallCart cart){
        if(adminId == null){
            return ResponseUtil.fail401();
        }
        cartService.deleteById(cart.getId());
        return ResponseUtil.ok();
    }

}
