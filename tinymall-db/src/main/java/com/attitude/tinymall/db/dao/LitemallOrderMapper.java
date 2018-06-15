package com.attitude.tinymall.db.dao;

import com.attitude.tinymall.db.domain.LitemallOrder;
import com.attitude.tinymall.db.domain.LitemallOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LitemallOrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    long countByExample(LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int insert(LitemallOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int insertSelective(LitemallOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallOrder selectOneByExample(LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallOrder selectOneByExampleSelective(@Param("example") LitemallOrderExample example, @Param("selective") LitemallOrder.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<LitemallOrder> selectByExampleSelective(@Param("example") LitemallOrderExample example, @Param("selective") LitemallOrder.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    List<LitemallOrder> selectByExample(LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallOrder selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallOrder.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    LitemallOrder selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallOrder selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallOrder record, @Param("example") LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallOrder record, @Param("example") LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") LitemallOrderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_order
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}