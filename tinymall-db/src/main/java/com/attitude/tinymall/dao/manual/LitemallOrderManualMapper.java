package com.attitude.tinymall.dao.manual;

import com.attitude.tinymall.domain.LitemallOrderWithGoods;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
public interface LitemallOrderManualMapper {

  List<LitemallOrderWithGoods> selectOdersWithGoodsByAdminId(@Param("adminId") Integer adminId);

}
