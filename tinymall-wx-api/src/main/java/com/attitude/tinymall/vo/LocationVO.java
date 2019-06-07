package com.attitude.tinymall.vo;


import com.attitude.tinymall.domain.baidu.geocode.GeoDecodingAddress;
import lombok.Data;

@Data
public class LocationVO extends GeoDecodingAddress {

    /**
     * in/out
     * 是否在地理围栏范围内
     */
    private String distributionStatus;
}
