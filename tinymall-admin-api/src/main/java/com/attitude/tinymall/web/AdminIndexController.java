package com.attitude.tinymall.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.attitude.tinymall.util.ResponseUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/{userName}/index")
@Deprecated
public class AdminIndexController {
    private final Log logger = LogFactory.getLog(AdminIndexController.class);

    @RequestMapping("/index")
    public Object index(){
        return ResponseUtil.ok("hello world, this is admin service");
    }


}
