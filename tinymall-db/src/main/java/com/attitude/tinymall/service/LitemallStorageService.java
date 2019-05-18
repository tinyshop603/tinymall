package com.attitude.tinymall.service;

import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.dao.LitemallStorageMapper;
import com.attitude.tinymall.domain.LitemallStorage;
import com.attitude.tinymall.domain.LitemallStorageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

public interface LitemallStorageService {

     void deleteByKey(String key) ;

     void add(LitemallStorage storageInfo);

     LitemallStorage findByName(String filename) ;

     LitemallStorage findByKey(String key) ;

     void update(LitemallStorage storageInfo);


     LitemallStorage findById(Integer id);

     List<LitemallStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order) ;

     int countSelective(String key, String name, Integer page, Integer size, String sort, String order) ;
}
