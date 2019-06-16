package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallRegionMapper;
import com.attitude.tinymall.domain.LitemallRegion;
import com.attitude.tinymall.domain.LitemallRegionExample;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface LitemallRegionService {

     List<LitemallRegion> queryByPid(Integer parentId) ;

     LitemallRegion findById(Integer id);

     List<LitemallRegion> querySelective(String name, Integer code, Integer page, Integer size, String sort, String order) ;

     LitemallRegion queryByCode(Integer code);

     int countSelective(String name, Integer code, Integer page, Integer size, String sort, String order) ;
}
