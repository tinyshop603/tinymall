package com.attitude.tinymall.db.dao.manual;

import com.attitude.tinymall.db.domain.LitemallOrderWithGoods;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author zhaoguiyang on 2019/5/15.
 * @project Wechat
 */
public interface LitemallOrderManualMapper {

  List<LitemallOrderWithGoods> selectOdersWithGoodsByAdminId(@Param("adminId") Integer adminId);

}
