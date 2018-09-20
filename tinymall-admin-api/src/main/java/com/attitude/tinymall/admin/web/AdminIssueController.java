package com.attitude.tinymall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.admin.annotation.LoginAdmin;
import com.attitude.tinymall.db.domain.LitemallIssue;
import com.attitude.tinymall.db.service.LitemallIssueService;
import com.attitude.tinymall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/issue")
@Deprecated
public class AdminIssueController {
    private final Log logger = LogFactory.getLog(AdminIssueController.class);

    @Autowired
    private LitemallIssueService issueService;

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String question,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        List<LitemallIssue> issueList = issueService.querySelective(question, page, limit, sort, order);
        int total = issueService.countSelective(question, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", issueList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallIssue brand){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        issueService.add(brand);
        return ResponseUtil.ok(brand);
    }

    @GetMapping("/read")
    public Object read(@LoginAdmin Integer adminId, Integer id){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        if(id == null){
            return ResponseUtil.badArgument();
        }

        LitemallIssue brand = issueService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallIssue brand){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        issueService.updateById(brand);
        return ResponseUtil.ok(brand);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallIssue brand){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        issueService.deleteById(brand.getId());
        return ResponseUtil.ok();
    }

}
