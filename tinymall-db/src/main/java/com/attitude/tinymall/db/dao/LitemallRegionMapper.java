package com.attitude.tinymall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.attitude.tinymall.db.domain.LitemallRegion;
import com.attitude.tinymall.db.domain.LitemallRegionExample;

public interface LitemallRegionMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    long countByExample(LitemallRegionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallRegionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int insert(LitemallRegion record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int insertSelective(LitemallRegion record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallRegion selectOneByExample(LitemallRegionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallRegion selectOneByExampleSelective(@Param("example") LitemallRegionExample example, @Param("selective") LitemallRegion.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<LitemallRegion> selectByExampleSelective(@Param("example") LitemallRegionExample example, @Param("selective") LitemallRegion.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    List<LitemallRegion> selectByExample(LitemallRegionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    LitemallRegion selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallRegion.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    LitemallRegion selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallRegion record, @Param("example") LitemallRegionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallRegion record, @Param("example") LitemallRegionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallRegion record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_region
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallRegion record);
}