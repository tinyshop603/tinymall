package com.attitude.tinymall.service.impl;

import com.attitude.tinymall.dao.LitemallStorageMapper;
import com.attitude.tinymall.domain.LitemallStorage;
import com.attitude.tinymall.domain.LitemallStorageExample;
import com.attitude.tinymall.service.LitemallStorageService;
import com.github.pagehelper.PageHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LitemallStorageServiceImpl implements LitemallStorageService {
    @Autowired
    private LitemallStorageMapper storageMapper;
    @Override
    public void deleteByKey(String key) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andKeyEqualTo(key);
        storageMapper.logicalDeleteByExample(example);
    }
    @Override
    public void add(LitemallStorage storageInfo) {
        storageMapper.insertSelective(storageInfo);
    }
    @Override
    public LitemallStorage findByName(String filename) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andNameEqualTo(filename).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }
    @Override
    public LitemallStorage findByKey(String key) {
        LitemallStorageExample example = new LitemallStorageExample();
        example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }
    @Override
    public void update(LitemallStorage storageInfo) {
        storageMapper.updateByPrimaryKeySelective(storageInfo);
    }

    @Override
    public LitemallStorage findById(Integer id) {
        return storageMapper.selectByPrimaryKey(id);
    }
    @Override
    public List<LitemallStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order) {
        LitemallStorageExample example = new LitemallStorageExample();
        LitemallStorageExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(key)){
            criteria.andKeyEqualTo(key);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        PageHelper.startPage(page, limit);
        return storageMapper.selectByExample(example);
    }
    @Override
    public int countSelective(String key, String name, Integer page, Integer size, String sort, String order) {
        LitemallStorageExample example = new LitemallStorageExample();
        LitemallStorageExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isEmpty(key)){
            criteria.andKeyEqualTo(key);
        }
        if(!StringUtils.isEmpty(name)){
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        return (int)storageMapper.countByExample(example);
    }
}
