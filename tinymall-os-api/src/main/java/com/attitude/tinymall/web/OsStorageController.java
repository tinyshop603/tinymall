package com.attitude.tinymall.web;

import com.attitude.tinymall.config.ObjectStorageConfig;
import com.attitude.tinymall.service.StorageService;
import com.attitude.tinymall.util.ResponseUtil;
import com.attitude.tinymall.service.LitemallStorageService;
import com.attitude.tinymall.util.CharUtil;
import com.attitude.tinymall.domain.LitemallStorage;
import com.attitude.tinymall.service.AliyunOssService;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/os/storage")
@Slf4j
public class OsStorageController {

  @Autowired
  private StorageService storageService;
  @Autowired
  private LitemallStorageService tinymallStorageService;
  @Autowired
  private AliyunOssService aliyunOssService;

  @Autowired
  private ObjectStorageConfig osConfig;

  private String generateUrl(String key) {
    return osConfig.getAddress() + "/os/storage/fetch/" + key;
  }

  private String generateKey(String originalFilename) {
    int index = originalFilename.lastIndexOf('.');
    String suffix = originalFilename.substring(index);

    String key = null;
    LitemallStorage storageInfo = null;

    do {
      key = CharUtil.getRandomString(20) + suffix;
      storageInfo = tinymallStorageService.findByKey(key);
    }
    while (storageInfo != null);

    return key;
  }

  @GetMapping("/list")
  public Object list(String key, String name,
      @RequestParam(value = "page", defaultValue = "1") Integer page,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      String sort, String order) {
    List<LitemallStorage> storageList = tinymallStorageService
        .querySelective(key, name, page, limit, sort, order);
    int total = tinymallStorageService.countSelective(key, name, page, limit, sort, order);
    Map<String, Object> data = new HashMap<>();
    data.put("total", total);
    data.put("items", storageList);

    return ResponseUtil.ok(data);
  }

  @GetMapping("/aliyun/{name}")
  public Object getAliyunImageUrlByName(@PathVariable String name, HttpServletRequest request){
    ByteArrayOutputStream outputStream = aliyunOssService.getOutputByQueryString(name, request.getQueryString());
    if (outputStream == null) {
      return ResponseUtil.fail404();
    } else {
      return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(outputStream.toByteArray());
    }
  }

  @GetMapping("/aliyun/{imageName}/url")
  public Object getAliYunImageStream(@PathVariable String imageName) {
    return ResponseUtil.ok(aliyunOssService.getFileUrl(imageName));
  }

  @PostMapping("/aliyun/{name}")
  public Object saveAliyunImage(MultipartFile file, @PathVariable String name) {
    try {
      aliyunOssService.uploadFile(name, file.getInputStream());
    } catch (IOException e) {
      log.error("cannot join this image to the aliyun oss, detail: " + e.getMessage());
      return ResponseUtil.fail();
    }
    return ResponseUtil.ok();
  }

  @PostMapping("/create")
  public Object create(@RequestParam("file") MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    InputStream inputStream = null;
    try {
      inputStream = file.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseUtil.badArgumentValue();
    }
    String key = generateKey(originalFilename);
    storageService.store(inputStream, key);

    String url = generateUrl(key);
    LitemallStorage storageInfo = new LitemallStorage();
    storageInfo.setName(originalFilename);
    storageInfo.setSize((int) file.getSize());
    storageInfo.setType(file.getContentType());
    storageInfo.setAddTime(LocalDateTime.now());
    storageInfo.setModified(LocalDateTime.now());
    storageInfo.setKey(key);
    storageInfo.setUrl(url);
    tinymallStorageService.add(storageInfo);
    return ResponseUtil.ok(storageInfo);
  }

  @PostMapping("/read")
  public Object read(Integer id) {
    if (id == null) {
      return ResponseUtil.badArgument();
    }
    LitemallStorage storageInfo = tinymallStorageService.findById(id);
    if (storageInfo == null) {
      return ResponseUtil.badArgumentValue();
    }
    return ResponseUtil.ok(storageInfo);
  }

  @PostMapping("/update")
  public Object update(@RequestBody LitemallStorage tinymallStorage) {

    tinymallStorageService.update(tinymallStorage);
    return ResponseUtil.ok(tinymallStorage);
  }

  @PostMapping("/delete")
  public Object delete(@RequestBody LitemallStorage tinymallStorage) {
    tinymallStorageService.deleteByKey(tinymallStorage.getKey());
    storageService.delete(tinymallStorage.getKey());
    return ResponseUtil.ok();
  }

  @GetMapping("/fetch/{key:.+}")
  public ResponseEntity<Resource> fetch(@PathVariable String key) {
    LitemallStorage tinymallStorage = tinymallStorageService.findByKey(key);
    if (key == null) {
      ResponseEntity.notFound();
    }
    String type = tinymallStorage.getType();
    MediaType mediaType = MediaType.parseMediaType(type);

    Resource file = storageService.loadAsResource(key);
    if (file == null) {
      ResponseEntity.notFound();
    }
    return ResponseEntity.ok().contentType(mediaType).body(file);
  }

  @GetMapping("/download/{key:.+}")
  public ResponseEntity<Resource> download(@PathVariable String key) {
    LitemallStorage tinymallStorage = tinymallStorageService.findByKey(key);
    if (key == null) {
      ResponseEntity.notFound();
    }
    String type = tinymallStorage.getType();
    MediaType mediaType = MediaType.parseMediaType(type);

    Resource file = storageService.loadAsResource(key);
    if (file == null) {
      ResponseEntity.notFound();
    }
    return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

}
