package com.attitude.tinymall;

import com.attitude.tinymall.domain.MessageInfo;
import com.attitude.tinymall.util.JacksonUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import java.math.BigDecimal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
public class WxConfigTest {
    @Autowired
    private WxPayService wxPayService;

    @Test
    public void test() {
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        System.out.println(wxPayConfig.getMchId() + " " + wxPayConfig.getMchKey());
    }

    @Test
    public void testJson(){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMsgType("-->");
        String s = JacksonUtil.stringifyObject(messageInfo);
        System.out.println(s);
        Object o = JacksonUtil.pareseObject(s);
        System.out.println(o.toString());

    }

    @Test
    public void testStr(){
      String a = "aaaa1" ;
       String[] b =  a.split("a");
        System.out.println(a.split("a").length);

    }
    @Test
     public void testVoidBigDecial(){
        BigDecimal b = new BigDecimal("1234.00004");
        BigDecimal c = new BigDecimal("1234.00004");
        System.out.println(b.equals(c));
     }

}
