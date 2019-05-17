package com.attitude.tinymall.db.util;

import com.attitude.tinymall.db.domain.LitemallOrder;

import java.util.ArrayList;
import java.util.List;

/*
 * 订单流程：下单成功－》支付订单－》发货－》收货
 * 订单状态：
 * 101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，用户申请退款；203，管理员执行退款操作，确认退款成功；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收获后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消或者付款
 * 当201支付完成而商家未发货时，此时用户可以退款
 * 当301商家已发货时，此时用户可以有确认收货
 * 当401用户确认收货以后，此时用户可以进行的操作是退货、删除、去评价或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除、去评价、或者再次购买
 *
 * 新增状态：货到付款状态码：wz-2018-9-20
 * 001,货到付款，生成订单但商家未发货；002，货到付款，用户取消订单；
 * 003，货到付款，店家发货，用户未确认收货；004，货到付款，用户主动确认收货；
 * 005，货到付款，系统自动确认收货；
 *
 * 当001商家未确定时，此时用户可以进行的操作是取消订单
 * 当002，取消订单时，此时用户可以进行的操作是删除订单
 * 当003，店家发货时，此时无可进行操作
 * 当004，005 确认收货时，此时用户可进行的操作是删除订单或者再次购买
 */
public class OrderUtil {

    public static final Short STATUS_CREATE = 101;
    public static final Short STATUS_PAY = 201;
    public static final Short STATUS_SHIP = 301;
    public static final Short STATUS_CONFIRM = 401;
    public static final Short STATUS_CANCEL = 102;
    public static final Short STATUS_AUTO_CANCEL = 103;
    public static final Short STATUS_REFUND = 202;
    public static final Short STATUS_REFUND_CONFIRM = 203;
    public static final Short STATUS_AUTO_CONFIRM = 402;
    public static final Short STATUS_AFTER_PAY = 001;
    public static final Short STATUS_AFTER_CANCEL = 002;
    public static final Short STATUS_AFTER_SHIP = 003;
    public static final Short STATUS_AFTER_CONFIRM = 004;
    public static final Short STATUS_AFTER_AUTO_CONFIRM = 005;


    public static String orderStatusText(LitemallOrder order) {
        int status = 0;

        if (status == 101) {
            return "提示：等待买家付款";
        }

        if (status == 102) {
//            return "已取消";
            return "提示：经过商家确认，该订单已取消。如有疑问，请直接联系店家";
        }

        if (status == 103) {
            return "提示：该订单已取消(系统)";
        }

        if (status == 201) {
//            return "已付款";
            return "提示：店家正在协调配送，请稍等片刻";
        }

        if (status == 202) {
            return "提示：退款中，等待商家确认";
        }

        if (status == 203) {
            return "提示：已退款";
        }

        if (status == 301) {
//            return "已发货";
            return "提示：店家正在快马加鞭向您赶来";
        }

        if (status == 401) {
//            return "已收货";
            return "提示：该订单已完成";
        }

        if (status == 402) {
            return "提示：该订单已完成（系统）";
        }

        if (status == 001) {
            return "提示：店家正在协调配送，请稍等片刻";
        }

        if (status == 002) {
            return "提示：该订单已取消，如有疑问，请直接联系店家";
        }

        if (status == 003) {
            return "提示：店家正在快马加鞭向您赶来";
        }

        if (status == 004) {
            return "提示：该订单已完成";
        }

        if (status == 005) {
            return "提示：该订单已完成";
        }

        throw new IllegalStateException("orderStatus不支持");
    }

}
