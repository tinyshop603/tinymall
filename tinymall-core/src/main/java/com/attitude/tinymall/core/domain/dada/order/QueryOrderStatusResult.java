package com.attitude.tinymall.core.domain.dada.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangsong 20190511
 */
@Data
public class QueryOrderStatusResult {
    private String orderId;//第三方订单编号

    private Integer statusCode;//订单状态(待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 妥投异常之物品返回中=9 妥投异常之物品返回完成=10 系统故障订单发布失败=1000 可参考文末的状态说明）

    private String statusMsg; //订单状态

    private String transporterName; //骑手姓名

    private String transporterPhone;//骑手电话

    private String transporterLng;//骑手经度

    private String transporterLat;//骑手纬度

    private BigDecimal deliveryFee;//	配送费,单位为元

    private BigDecimal tips; //小费,单位为元

    private BigDecimal couponFee; //优惠券费用,单位为元

    private BigDecimal insuranceFee; //保价费,单位为元

    private BigDecimal actualFee; //实际支付费用,单位为元

    private Double createTime; //发单时间

    private String acceptTime; //接单时间,若未接单,则为空

    private String fetchTime;//取货时间,若未接单,则为空

    private String finishTime;//送达时间,若未接单,则为空

    private String cancelTime;//取消时间,若未接单,则为空

    private String orderFinishCode;//	收货码

    private BigDecimal deductFee;//违约金
}
