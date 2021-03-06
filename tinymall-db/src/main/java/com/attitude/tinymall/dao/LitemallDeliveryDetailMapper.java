package com.attitude.tinymall.dao;

import com.attitude.tinymall.domain.LitemallDeliveryDetail;
import com.attitude.tinymall.domain.LitemallDeliveryDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LitemallDeliveryDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    long countByExample(LitemallDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String deliveryId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int insert(LitemallDeliveryDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int insertSelective(LitemallDeliveryDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallDeliveryDetail selectOneByExample(LitemallDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallDeliveryDetail selectOneByExampleSelective(@Param("example") LitemallDeliveryDetailExample example, @Param("selective") LitemallDeliveryDetail.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<LitemallDeliveryDetail> selectByExampleSelective(@Param("example") LitemallDeliveryDetailExample example, @Param("selective") LitemallDeliveryDetail.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    List<LitemallDeliveryDetail> selectByExample(LitemallDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallDeliveryDetail selectByPrimaryKeySelective(@Param("deliveryId") String deliveryId, @Param("selective") LitemallDeliveryDetail.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    LitemallDeliveryDetail selectByPrimaryKey(String deliveryId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallDeliveryDetail record, @Param("example") LitemallDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallDeliveryDetail record, @Param("example") LitemallDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallDeliveryDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_delivery_detail
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallDeliveryDetail record);
}