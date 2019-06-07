package com.attitude.tinymall.domain.baidu.address;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PoiAddress {

    /**
     * area : 石景山区
     * uid : 2a7a25ecf83f12636c3e1bdd
     * address : 北京市石景山区石景山路22号
     * province : 北京市
     * city : 北京市
     * name : 万商大厦
     * location : {"lng":116.21983431473,"lat":39.906550730072}
     * telephone : (010)68666399
     * detail : 1
     * street_id : 2a7a25ecf83f12636c3e1bdd
     */
    private String area;
    private String uid;
    private String address;
    private String province;
    private String city;
    private String name;
    private Location location;
    private String telephone;
    private int detail;
    @JSONField(name = "street_id")
    private String streetId;


}
