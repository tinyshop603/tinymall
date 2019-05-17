package com.attitude.tinymall.web;

import com.attitude.tinymall.annotation.LoginAdmin;
import com.attitude.tinymall.dao.AdminToken;
import com.attitude.tinymall.service.AdminTokenManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.util.bcrypt.BCryptPasswordEncoder;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.service.LitemallAdminService;
import com.attitude.tinymall.util.JacksonUtil;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/login")
public class AdminAuthController {
    private final Log logger = LogFactory.getLog(AdminAuthController.class);

    @Autowired
    private LitemallAdminService adminService;

    /*
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public Object login(@RequestBody String body){
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return ResponseUtil.badArgument();
        }

        LitemallAdmin admin = adminService.findAdmin(username);
        if(admin == null){
            return ResponseUtil.badArgumentValue();
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(password, admin.getPassword())){
            return ResponseUtil.fail(403, "账号密码不对");
        }

        Integer adminId = admin.getId();
        // token
        AdminToken adminToken = AdminTokenManager.generateToken(adminId);

        return ResponseUtil.ok(adminToken.getToken());
    }

    @PostMapping("/logout")
    public Object login(@LoginAdmin Integer adminId){
        if(adminId == null){
            return ResponseUtil.unlogin();
        }

        return ResponseUtil.ok();
    }
}
