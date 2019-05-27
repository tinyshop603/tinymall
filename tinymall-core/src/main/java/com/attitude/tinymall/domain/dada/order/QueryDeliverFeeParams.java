package com.attitude.tinymall.domain.dada.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangsong 20190511
 */
@Builder
@Data
public class QueryDeliverFeeParams {
    @JSONField(name = "shop_no")//门店编号，门店创建后可在门店列表和单页查看
    private String shopNo;

    @JSONField(name = "origin_id")
    private String originId;

    @JSONField(name = "city_code")//订单所在城市的code
    private String cityCode;

    @JSONField(name = "cargo_price")//订单金额
    private BigDecimal cargoPrice;

    @JSONField(name = "is_prepay")//是否需要垫付 1:是 0:否 (垫付订单金额，非运费)
    private Integer isPrepay;

    @JSONField(name = "receiver_name")//收货人姓名
    private String receiverName;

    @JSONField(name = "receiver_address")//收货人地址
    private String receiverAddress;

    @JSONField(name = "receiver_lat")//收货人地址维度（高德坐标系）
    private float receiverLat;

    @JSONField(name = "receiver_lng")//收货人地址经度（高德坐标系）
    private float receiverLng;

    private String callback;//回调URL

    @JSONField(name = "receiver_phone")
    private String receiverPhone;//收货人手机号
}
