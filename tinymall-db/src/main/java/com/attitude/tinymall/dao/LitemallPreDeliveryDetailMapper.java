package com.attitude.tinymall.dao;

import com.attitude.tinymall.domain.LitemallPreDeliveryDetail;
import com.attitude.tinymall.domain.LitemallPreDeliveryDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LitemallPreDeliveryDetailMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    long countByExample(LitemallPreDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallPreDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String deliveryId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int insert(LitemallPreDeliveryDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int insertSelective(LitemallPreDeliveryDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallPreDeliveryDetail selectOneByExample(LitemallPreDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallPreDeliveryDetail selectOneByExampleSelective(@Param("example") LitemallPreDeliveryDetailExample example, @Param("selective") LitemallPreDeliveryDetail.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<LitemallPreDeliveryDetail> selectByExampleSelective(@Param("example") LitemallPreDeliveryDetailExample example, @Param("selective") LitemallPreDeliveryDetail.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    List<LitemallPreDeliveryDetail> selectByExample(LitemallPreDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallPreDeliveryDetail selectByPrimaryKeySelective(@Param("deliveryId") String deliveryId, @Param("selective") LitemallPreDeliveryDetail.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    LitemallPreDeliveryDetail selectByPrimaryKey(String deliveryId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallPreDeliveryDetail record, @Param("example") LitemallPreDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallPreDeliveryDetail record, @Param("example") LitemallPreDeliveryDetailExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallPreDeliveryDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_pre_delivery_detail
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallPreDeliveryDetail record);
}