package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.domain.LitemallUser;
import com.attitude.tinymall.service.LitemallUserService;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/{userName}/user")
public class AdminUserController {

  private final Log logger = LogFactory.getLog(AdminUserController.class);

  @Autowired
  private LitemallUserService userService;

  @GetMapping("/list")
  public Object list(@LoginAdmin Integer adminId,
      String username, String mobile,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      String sort, String order) {
    if (adminId == null) {
      return ResponseUtil.fail401();
    }
    List<LitemallUser> userList = userService.listUsersByAdminId(adminId, username, mobile, page, limit, sort, order);
    int total = userService.countUsersByAdminId(adminId, username, mobile);
    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("items", userList);

    return ResponseUtil.ok(data);
  }

  @GetMapping("/username")
  public Object username(String username) {
    if (StringUtil.isEmpty(username)) {
      return ResponseUtil.fail402();
    }

    int total = userService.countSeletive(username, null, null, null, null, null);
    if (total == 0) {
      return ResponseUtil.ok("不存在");
    }
    return ResponseUtil.ok("已存在");
  }


  @PostMapping("/create")
  public Object create(@LoginAdmin Integer adminId, @RequestBody LitemallUser user) {
    logger.debug(user);

    userService.add(user);
    return ResponseUtil.ok(user);
  }

  @PostMapping("/update")
  public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallUser user) {
    logger.debug(user);

    userService.update(user);
    return ResponseUtil.ok(user);
  }
}
