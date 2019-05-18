package com.attitude.tinymall.service;

import com.attitude.tinymall.dao.LitemallUserMapper;
import com.attitude.tinymall.domain.LitemallAdmin;
import com.attitude.tinymall.domain.LitemallUser;
import com.github.pagehelper.PageHelper;
import com.attitude.tinymall.domain.LitemallUserExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;


public interface LitemallUserService {


   LitemallUser findById(Integer userId);

   LitemallUser queryByOid(String openId) ;

   void add(LitemallUser user) ;

   void tryToMontionPerson(LitemallUser user);

   void update(LitemallUser user) ;


   List<LitemallUser> listUsersByAdminId(Integer adminId, String username, String mobile,
      Integer page, Integer size, String sort, String order) ;

   int countUsersByAdminId(Integer adminId, String username, String mobile) ;


   List<LitemallUser> querySelective(String username, String mobile, Integer page,
      Integer size, String sort, String order) ;

   int countSeletive(String username, String mobile, Integer page, Integer size, String sort,
      String order) ;

   int count() ;

   int countByAdminId(Integer adminId) ;

   List<LitemallUser> queryByUsername(String username) ;

   List<LitemallUser> queryByMobile(String mobile) ;

   List<LitemallUser> queryByAdminId(Integer adminId) ;


   void deleteById(Integer id) ;
}
