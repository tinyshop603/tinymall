package com.attitude.tinymall.util;

public class OrderHandleOption {
    private boolean cancel = false;      // 取消操作
    private boolean delete = false;      // 删除操作
    private boolean pay = false;         // 支付操作
    private boolean comment = false;    // 评论操作
    private boolean confirm = false;    // 确认收货操作
    private boolean refund = false;     // 取消订单并退款操作
    private boolean seller_refund = false;     // 商家进行退款操作
    private boolean rebuy = false;        // 再次购买

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public void setRefund(boolean refund) {
        this.refund = refund;
    }

    public void setSeller_refund(boolean seller_refund) {
        this.seller_refund = seller_refund;
    }

    public void setRebuy(boolean rebuy) {
        this.rebuy = rebuy;
    }

    public boolean isCancel() {
        return cancel;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isPay() {
        return pay;
    }

    public boolean isComment() {
        return comment;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public boolean isRefund() {
        return refund;
    }

    public boolean isSeller_refund() {
        return seller_refund;
    }

    public boolean isRebuy() {
        return rebuy;
    }

}
