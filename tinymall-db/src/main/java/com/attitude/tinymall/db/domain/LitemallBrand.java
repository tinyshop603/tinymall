package com.attitude.tinymall.db.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LitemallBrand {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public static final Boolean NOT_DELETED = false;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    @Deprecated
    public static final Boolean DEL_FLAG_ON = false;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public static final Boolean IS_DELETED = true;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    @Deprecated
    public static final Boolean DEL_FLAG_OFF = true;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.list_pic_url
     *
     * @mbg.generated
     */
    private String listPicUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.simple_desc
     *
     * @mbg.generated
     */
    private String simpleDesc;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.pic_url
     *
     * @mbg.generated
     */
    private String picUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.sort_order
     *
     * @mbg.generated
     */
    private Byte sortOrder;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.is_show
     *
     * @mbg.generated
     */
    private Boolean isShow;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.floor_price
     *
     * @mbg.generated
     */
    private BigDecimal floorPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.app_list_pic_url
     *
     * @mbg.generated
     */
    private String appListPicUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.is_new
     *
     * @mbg.generated
     */
    private Boolean isNew;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.new_pic_url
     *
     * @mbg.generated
     */
    private String newPicUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.new_sort_order
     *
     * @mbg.generated
     */
    private Byte newSortOrder;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.add_time
     *
     * @mbg.generated
     */
    private LocalDateTime addTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tinymall_brand.deleted
     *
     * @mbg.generated
     */
    private Boolean deleted;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.id
     *
     * @return the value of tinymall_brand.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.id
     *
     * @param id the value for tinymall_brand.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.name
     *
     * @return the value of tinymall_brand.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.name
     *
     * @param name the value for tinymall_brand.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.list_pic_url
     *
     * @return the value of tinymall_brand.list_pic_url
     *
     * @mbg.generated
     */
    public String getListPicUrl() {
        return listPicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.list_pic_url
     *
     * @param listPicUrl the value for tinymall_brand.list_pic_url
     *
     * @mbg.generated
     */
    public void setListPicUrl(String listPicUrl) {
        this.listPicUrl = listPicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.simple_desc
     *
     * @return the value of tinymall_brand.simple_desc
     *
     * @mbg.generated
     */
    public String getSimpleDesc() {
        return simpleDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.simple_desc
     *
     * @param simpleDesc the value for tinymall_brand.simple_desc
     *
     * @mbg.generated
     */
    public void setSimpleDesc(String simpleDesc) {
        this.simpleDesc = simpleDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.pic_url
     *
     * @return the value of tinymall_brand.pic_url
     *
     * @mbg.generated
     */
    public String getPicUrl() {
        return picUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.pic_url
     *
     * @param picUrl the value for tinymall_brand.pic_url
     *
     * @mbg.generated
     */
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.sort_order
     *
     * @return the value of tinymall_brand.sort_order
     *
     * @mbg.generated
     */
    public Byte getSortOrder() {
        return sortOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.sort_order
     *
     * @param sortOrder the value for tinymall_brand.sort_order
     *
     * @mbg.generated
     */
    public void setSortOrder(Byte sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.is_show
     *
     * @return the value of tinymall_brand.is_show
     *
     * @mbg.generated
     */
    public Boolean getIsShow() {
        return isShow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.is_show
     *
     * @param isShow the value for tinymall_brand.is_show
     *
     * @mbg.generated
     */
    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.floor_price
     *
     * @return the value of tinymall_brand.floor_price
     *
     * @mbg.generated
     */
    public BigDecimal getFloorPrice() {
        return floorPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.floor_price
     *
     * @param floorPrice the value for tinymall_brand.floor_price
     *
     * @mbg.generated
     */
    public void setFloorPrice(BigDecimal floorPrice) {
        this.floorPrice = floorPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.app_list_pic_url
     *
     * @return the value of tinymall_brand.app_list_pic_url
     *
     * @mbg.generated
     */
    public String getAppListPicUrl() {
        return appListPicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.app_list_pic_url
     *
     * @param appListPicUrl the value for tinymall_brand.app_list_pic_url
     *
     * @mbg.generated
     */
    public void setAppListPicUrl(String appListPicUrl) {
        this.appListPicUrl = appListPicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.is_new
     *
     * @return the value of tinymall_brand.is_new
     *
     * @mbg.generated
     */
    public Boolean getIsNew() {
        return isNew;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.is_new
     *
     * @param isNew the value for tinymall_brand.is_new
     *
     * @mbg.generated
     */
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.new_pic_url
     *
     * @return the value of tinymall_brand.new_pic_url
     *
     * @mbg.generated
     */
    public String getNewPicUrl() {
        return newPicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.new_pic_url
     *
     * @param newPicUrl the value for tinymall_brand.new_pic_url
     *
     * @mbg.generated
     */
    public void setNewPicUrl(String newPicUrl) {
        this.newPicUrl = newPicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.new_sort_order
     *
     * @return the value of tinymall_brand.new_sort_order
     *
     * @mbg.generated
     */
    public Byte getNewSortOrder() {
        return newSortOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.new_sort_order
     *
     * @param newSortOrder the value for tinymall_brand.new_sort_order
     *
     * @mbg.generated
     */
    public void setNewSortOrder(Byte newSortOrder) {
        this.newSortOrder = newSortOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.add_time
     *
     * @return the value of tinymall_brand.add_time
     *
     * @mbg.generated
     */
    public LocalDateTime getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.add_time
     *
     * @param addTime the value for tinymall_brand.add_time
     *
     * @mbg.generated
     */
    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tinymall_brand.deleted
     *
     * @return the value of tinymall_brand.deleted
     *
     * @mbg.generated
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tinymall_brand.deleted
     *
     * @param deleted the value for tinymall_brand.deleted
     *
     * @mbg.generated
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", listPicUrl=").append(listPicUrl);
        sb.append(", simpleDesc=").append(simpleDesc);
        sb.append(", picUrl=").append(picUrl);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", isShow=").append(isShow);
        sb.append(", floorPrice=").append(floorPrice);
        sb.append(", appListPicUrl=").append(appListPicUrl);
        sb.append(", isNew=").append(isNew);
        sb.append(", newPicUrl=").append(newPicUrl);
        sb.append(", newSortOrder=").append(newSortOrder);
        sb.append(", addTime=").append(addTime);
        sb.append(", deleted=").append(deleted);
        sb.append("]");
        return sb.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LitemallBrand other = (LitemallBrand) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getListPicUrl() == null ? other.getListPicUrl() == null : this.getListPicUrl().equals(other.getListPicUrl()))
            && (this.getSimpleDesc() == null ? other.getSimpleDesc() == null : this.getSimpleDesc().equals(other.getSimpleDesc()))
            && (this.getPicUrl() == null ? other.getPicUrl() == null : this.getPicUrl().equals(other.getPicUrl()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getIsShow() == null ? other.getIsShow() == null : this.getIsShow().equals(other.getIsShow()))
            && (this.getFloorPrice() == null ? other.getFloorPrice() == null : this.getFloorPrice().equals(other.getFloorPrice()))
            && (this.getAppListPicUrl() == null ? other.getAppListPicUrl() == null : this.getAppListPicUrl().equals(other.getAppListPicUrl()))
            && (this.getIsNew() == null ? other.getIsNew() == null : this.getIsNew().equals(other.getIsNew()))
            && (this.getNewPicUrl() == null ? other.getNewPicUrl() == null : this.getNewPicUrl().equals(other.getNewPicUrl()))
            && (this.getNewSortOrder() == null ? other.getNewSortOrder() == null : this.getNewSortOrder().equals(other.getNewSortOrder()))
            && (this.getAddTime() == null ? other.getAddTime() == null : this.getAddTime().equals(other.getAddTime()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getListPicUrl() == null) ? 0 : getListPicUrl().hashCode());
        result = prime * result + ((getSimpleDesc() == null) ? 0 : getSimpleDesc().hashCode());
        result = prime * result + ((getPicUrl() == null) ? 0 : getPicUrl().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getIsShow() == null) ? 0 : getIsShow().hashCode());
        result = prime * result + ((getFloorPrice() == null) ? 0 : getFloorPrice().hashCode());
        result = prime * result + ((getAppListPicUrl() == null) ? 0 : getAppListPicUrl().hashCode());
        result = prime * result + ((getIsNew() == null) ? 0 : getIsNew().hashCode());
        result = prime * result + ((getNewPicUrl() == null) ? 0 : getNewPicUrl().hashCode());
        result = prime * result + ((getNewSortOrder() == null) ? 0 : getNewSortOrder().hashCode());
        result = prime * result + ((getAddTime() == null) ? 0 : getAddTime().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public void andLogicalDeleted(boolean deleted) {
        setDeleted(deleted ? IS_DELETED : NOT_DELETED);
    }

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table tinymall_brand
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    public enum Column {
        id("id", "id", "INTEGER"),
        name("name", "name", "VARCHAR"),
        listPicUrl("list_pic_url", "listPicUrl", "VARCHAR"),
        simpleDesc("simple_desc", "simpleDesc", "VARCHAR"),
        picUrl("pic_url", "picUrl", "VARCHAR"),
        sortOrder("sort_order", "sortOrder", "TINYINT"),
        isShow("is_show", "isShow", "BIT"),
        floorPrice("floor_price", "floorPrice", "DECIMAL"),
        appListPicUrl("app_list_pic_url", "appListPicUrl", "VARCHAR"),
        isNew("is_new", "isNew", "BIT"),
        newPicUrl("new_pic_url", "newPicUrl", "VARCHAR"),
        newSortOrder("new_sort_order", "newSortOrder", "TINYINT"),
        addTime("add_time", "addTime", "TIMESTAMP"),
        deleted("deleted", "deleted", "BIT");

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String column;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String javaProperty;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        private final String jdbcType;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String value() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getValue() {
            return this.column;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getJavaProperty() {
            return this.javaProperty;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String getJdbcType() {
            return this.jdbcType;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        Column(String column, String javaProperty, String jdbcType) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String desc() {
            return this.column + " DESC";
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table tinymall_brand
         *
         * @mbg.generated
         * @project https://github.com/itfsw/mybatis-generator-plugin
         */
        public String asc() {
            return this.column + " ASC";
        }
    }
}