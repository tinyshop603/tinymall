package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.service.LitemallAdminService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.domain.LitemallCategory;
import com.attitude.tinymall.service.LitemallCategoryService;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/category")
public class AdminCategoryController {
    private final Log logger = LogFactory.getLog(AdminCategoryController.class);

    @Autowired
    private LitemallCategoryService categoryService;

    @Autowired
    private LitemallAdminService adminService;

    @GetMapping
    public Object getHomeCategory(@LoginAdmin Integer adminId,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "limit", defaultValue = "10") Integer limit,
        String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        // 根据Parent Id 查询出 Id ,当做parentId进行塞入进去
        List<LitemallCategory> parentCategorys = categoryService.queryIdByPid(adminId,"L1");
        // 此时查询出来的必须是1或者是0条数据
        String parentId = null;
        if (parentCategorys != null && parentCategorys.size() > 0){
            parentId = parentCategorys.get(0).getId().toString();
        }

        List<LitemallCategory> collectList = categoryService.getAdminMallCategoryByAdminId(adminId,page, limit, sort, order);
        int total = categoryService.countAdminMallCategoryByAdminId(adminId,page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", collectList);

        return ResponseUtil.ok(data);
    }


    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String id,  String name,String parentId,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallCategory> collectList = categoryService.querySelective(id, name,parentId, page, limit, sort, order);
        int total = categoryService.countSelective(id, name,parentId, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", collectList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallCategory category){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        categoryService.add(category);
        return ResponseUtil.ok();
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallCategory category = categoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallCategory category){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        categoryService.updateById(category);
        return ResponseUtil.ok();
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallCategory category){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        categoryService.deleteById(category.getId());
        return ResponseUtil.ok();
    }

    @GetMapping("/l1")
    public Object catL1(@LoginAdmin Integer adminId) {
        if (adminId == null) {
            return ResponseUtil.unlogin();
        }

        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();
        HashMap<Integer, String> data = new HashMap<>(l1CatList.size());
        for(LitemallCategory category : l1CatList){
            data.put(category.getId(), category.getName());
        }
        return ResponseUtil.ok(data);
    }

}
