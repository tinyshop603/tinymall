package com.attitude.tinymall.os.web;

import com.attitude.tinymall.core.util.ResponseUtil;
import com.attitude.tinymall.db.service.LitemallStorageService;
import com.attitude.tinymall.core.util.CharUtil;
import com.attitude.tinymall.db.domain.LitemallStorage;
import com.attitude.tinymall.os.service.StorageService;
import com.attitude.tinymall.os.config.ObjectStorageConfig;
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
public class OsStorageController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private LitemallStorageService tinymallStorageService;

    @Autowired
    private ObjectStorageConfig osConfig;

    private String generateUrl(String key){
        return "http://" + osConfig.getAddress() + ":" + osConfig.getPort() + "/os/storage/fetch/" + key;
    }

    private String generateKey(String originalFilename){
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        LitemallStorage storageInfo = null;

        do{
            key = CharUtil.getRandomString(20) + suffix;
            storageInfo = tinymallStorageService.findByKey(key);
        }
        while(storageInfo != null);

        return key;
    }

    @GetMapping("/list")
    public Object list(String key, String name,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        List<LitemallStorage> storageList = tinymallStorageService.querySelective(key, name, page, limit, sort, order);
        int total = tinymallStorageService.countSelective(key, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", storageList);

        return ResponseUtil.ok(data);
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
        storageInfo.setSize((int)file.getSize());
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
        if(id == null){
            return ResponseUtil.badArgument();
        }
        LitemallStorage storageInfo = tinymallStorageService.findById(id);
        if(storageInfo == null){
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
        if(key == null){
            ResponseEntity.notFound();
        }
        String type = tinymallStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if(file == null) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }

    @GetMapping("/download/{key:.+}")
    public ResponseEntity<Resource> download(@PathVariable String key) {
        LitemallStorage tinymallStorage = tinymallStorageService.findByKey(key);
        if(key == null){
            ResponseEntity.notFound();
        }
        String type = tinymallStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if(file == null) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
