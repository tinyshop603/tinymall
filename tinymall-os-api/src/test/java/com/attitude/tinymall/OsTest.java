package com.attitude.tinymall;

import com.attitude.tinymall.service.AliyunOssService;
import java.io.FileInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.attitude.tinymall.config.ObjectStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class OsTest {

  @Autowired
  private ObjectStorageConfig objectStorageConfig;

  @Autowired
  private AliyunOssService aliyunOssService;


  @Test
  public void test() {
    System.out.println(objectStorageConfig.getAddress() + ":" + objectStorageConfig.getPort());
  }

  @Test
  public void testAliyunUpload() {
    try {
      assert aliyunOssService.uploadFile("test-my-advtor", new FileInputStream(
          "/Users/zhaoguiyang/Wechat/tinymall-os-api/src/test/resources/file/test.png"));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Test
  public void testAliyunDownload() {
    String url = aliyunOssService.getFileUrl("post3.png");
    System.out.println(url);
    assert url.length() > 1;
  }


}
