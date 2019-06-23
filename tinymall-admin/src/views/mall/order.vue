<template>
  <div class="app-container calendar-list-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"
                v-model="listQuery.userId"></el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入订单编号"
                v-model="listQuery.orderSn"></el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-download" @click="handleDownload"
                 :loading="downloadLoading">导出订单信息
      </el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-refresh" @click="handleCurrentChange">全部刷新</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit
              highlight-current-row>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table size="small" :data="props.row.goods" v-loading="listLoading" element-loading-text="正在查询中。。。" border
                    fit highlight-current-row>
            <el-table-column align="center" min-width="90px" label="商品条形码" prop="goodsSn"></el-table-column>
            <el-table-column align="center" min-width="90px" label="商品名称" prop="goodsName"></el-table-column>
            <el-table-column align="center" min-width="90px" label="商品数量" prop="number"></el-table-column>
            <el-table-column align="center" min-width="90px" label="商品规格"
                             prop="goodsSpecificationValues"></el-table-column>
            <el-table-column align="center" min-width="90px" label="商品单价" prop="retailPrice"></el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column v-if="false" align="center" width="120px" label="订单编号" prop="order.orderSn"></el-table-column>
      <el-table-column align="center" width="85px" label="下单时间" prop="order.addTime" sortable></el-table-column>
      <el-table-column align="center" min-width="80px" label="订单状态" prop="order.orderStatus">
        <template slot-scope="scope">
          <template v-for="tag in order_tags">
            <el-tag v-if="tag.status==scope.row.order.orderStatus" :type="tag.type">
              {{tag.name}}
            </el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="80px" label="配送状态" prop="order.tpdStatus">
        <template slot-scope="scope">
          <el-tag>
            {{scope.row.order.tpdStatusMsg?scope.row.order.tpdStatusMsg:'--/--'}}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="80px" label="用户昵称" prop="order.consignee"></el-table-column>
      <el-table-column align="center" min-width="80px" label="用户地址" prop="order.address"></el-table-column>
      <el-table-column align="center" width="100px" label="用户电话" prop="order.mobile"></el-table-column>
      <el-table-column align="center" width="80px" label="订单费用" prop="order.orderPrice"></el-table-column>
      <el-table-column align="center" v-if="false" min-width="80px" label="支付方式" prop="order.paymentWay">
        <template slot-scope="scope">
          <el-tag :type="textMap.orderPayWay[scope.row.order.paymentWay].type">{{textMap.orderPayWay[scope.row.order.paymentWay].text}}
          </el-tag>
        </template>
      </el-table-column>
      <!-- TODO 订单详情页的修改, 点击弹出订单的详情-->

      <el-table-column align="center" min-width="80px" label="配送信息" prop="order.remark">
        <template slot-scope="scope">
          <el-link @click="viewOrderDetail(scope.row)">查看详情<i class="el-icon-view el-icon--right"></i> </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作"  class-name="small-padding fixed-width" prop="type">
        <template slot-scope="scope">
          <el-dropdown hide-timeout="0" show-timeout="0" trigger="click" @command="handleOrderOptionCommand">
            <el-button type="primary">
              订单操作<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command='{type:"acceptOrder", itemData:scope.row.order}'>接受订单</el-dropdown-item>
              <el-dropdown-item :command='{type:"callRider", itemData:scope.row.order}'>呼叫骑手</el-dropdown-item>
              <el-dropdown-item :command='{type:"confirmCancelAndRefund", itemData:scope.row.order}'>取消订单</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <!--          <el-button :type="scope.row.sendBtnStatus.type" :disabled="scope.row.sendBtnStatus.disabled" size="small" @click="handleSend(scope.row.order)">发货</el-button>-->
          <!--          <el-button :type="scope.row.cancelBtnStatus.type" :disabled="scope.row.cancelBtnStatus.disabled" size="small" @click="handleCancelOrder(scope.row.order)">取消订单</el-button>-->
          <!--          <el-button :type="scope.row.confirmBtnStatus.type" :disabled="scope.row.confirmBtnStatus.disabled" size="small" @click="handleConfirmOrder(scope.row.order)">确认完成</el-button>-->
          <!--          <el-button :type="scope.row.refundBtnStatus.type" :disabled="scope.row.refundBtnStatus.disabled" size="small" @click="handleRefundOrder(scope.row.order)">申请退货</el-button>-->
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="80px" label="订单备注" prop="order.remark"></el-table-column>
    </el-table>
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                     :current-page="listQuery.page" :page-sizes="[10,20,30,50]" :page-size="listQuery.limit"
                     layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
    </div>
    <!-- 发货对话框 -->
    <el-dialog title="接受订单准备发货" :visible.sync="sendDialogFormVisible">
      确认发货后，您必须在30分钟内为客户送货到门，是否继续发货？
      <div slot="footer" class="dialog-footer">
        <el-button @click="sendDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="merchantAccept">确定</el-button>
      </div>
    </el-dialog>
    <!-- 确认退款对话框 -->
    <el-dialog title="取消订单" :visible.sync="refundAndCancleDialogFormVisible">
      取消订单前，请确保已跟客户进行电话沟通，与客户协商后才可进行</br>
      取消，否则会影响您的信誉，并且，金钱将直接退回给用户</br>
      确定退款后不可再对该订单做任何操作,继续操作?</br>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundAndCancleDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="merchantConfirmCancelAndRefund">确定</el-button>
      </div>
    </el-dialog>

    <!--   展示当前订单配送详情对话框-->
    <el-dialog title="配送详情" :visible.sync="showDeliveryDetail">
      <template>
        <el-table
          :data="deliveryDetailsDialogData"
          style="width: 100%">
          <el-table-column
            prop="key"
            width="150">
          </el-table-column>
          <el-table-column
            prop="value">
          </el-table-column>
        </el-table>
      </template>


      <div slot="footer" class="dialog-footer">
        <el-button @click="showDeliveryDetail = false">关闭</el-button>
      </div>
    </el-dialog>
    <audio id="bgMusic">
      <source src="../../media/come.mp3" type="audio/mp3">
    </audio>
  </div>
</template>
<style>
  .demo-table-expand {
    font-size: 0;
  }

  .demo-table-expand label {
    width: 200px;
    color: #99a9bf;
  }

  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }

</style>
<script>
import {
  listOrder,
  updateOrderCode,
  callDadaRider,
  STATUS,
  refundOrder,
  formatDate,
  readOrder,
  getOrderDetail,
  formatDate2
} from '@/api/order'
import waves from '@/directive/waves' // 水波纹指令
export default {
  name:'Order',
  directives:{
    waves
  },
  data() {
    return {
      deliveryDetailsDialogData:[],
      isShowDeleteColumn:false, // 是否显示订单表格删除状态的列
      list:undefined,
      player:undefined, // 后台音频播放器
      total:undefined,
      listLoading:true,
      listQuery:{
        page:1,
        limit:20,
        id:undefined,
        name:undefined,
        sort:'+id'
      },
      dataForm:{
        id:undefined,
        shipChannel:undefined,
        shipSn:undefined,
        shipStartTime:undefined,
        shipEndTime:undefined,
        orderStatus:undefined
      },
      sendDialogFormVisible:false,
      recvDialogFormVisible:false,
      refundAndCancleDialogFormVisible:false,
      showDeliveryDetail:false,
      downloadLoading:false,
      order_tags:[
        { name:'待付款', type:'normal', status:'PENDING_PAYMENT' },
        { name:'系统取消', type:'warning', status:'SYSTEM_AUTO_CANCEL' },
        { name:'系统完成', type:'success', status:'SYSTEM_AUTO_COMPLETE' },
        { name:'商家取消', type:'warning', status:'MERCHANT_CANCEL' },
        { name:'用户取消', type:'warning', status:'CUSTOMER_CANCEL' },
        { name:'订单进行中', type:'success', status:'ONGOING' },
        { name:'商家确认收单', type:'info', status:'MERCHANT_ACCEPT' },
        { name:'商家发货', type:'info', status:'MERCHANT_SHIP' },
        { name:'申请退款', type:'normal', status:'MERCHANT_REFUNDING' },
        { name:'订单完成', type:'success', status:'COMPLETE' },
        { name:'用户已付款', type:'success', status:'CUSTOMER_PAIED' },
        { name:'退款完成', type:'danger', status:'REFUND_COMPLETE' }
      ],
      textMap:{
        orderPayWay:{
          'ONLINE_WECHAT_PAY':{ text:'微信支付', type:'success' },
          'ONLINE_ALI_PAY':{ text:'支付宝支付', type:'normal' },
          'CASH_ON_DELIVERY':{ text:'货到付款', type:'warning' }

        }
      }
    }
  },
  created() {
    this.getList()
  },
  computed:{
    submitOrderEvent() {
      return this.$store.state.order.submitOrder
    },
    cancelOrderEvent() {
      return this.$store.state.order.cancelOrder
    },
    refundOrderEvent() {
      return this.$store.state.order.refundOrder
    },
    confirmOrderEvent() {
      return this.$store.state.order.confirmOrder
    }
  },
  watch:{
    submitOrderEvent:function(ov, nv) {
      const newOrder = this.$store.state.order.submitOrder
      // socket传数据对时间格式的处理-wz
      const date = new Date(newOrder.order.addTime)
      newOrder.order.addTime = formatDate(date, 'yyyy-MM-dd hh:mm:ss')
      if (newOrder) {
        this.list.unshift(newOrder)
      }
    },
    cancelOrderEvent:function(ov, nv) {
      const cancelOrder = this.$store.state.order.cancelOrder
      // TODO 临时写法，需找到时间格式变化原因 socket传数据对时间格式的处理-wz
      cancelOrder.addTime = formatDate2(cancelOrder.addTime)
      if (cancelOrder) {
        this.updateOrderItemStatus(cancelOrder)
      }
    },
    refundOrderEvent:function(ov, nv) {
      const refundOrder = this.$store.state.order.refundOrder
      // TODO 临时写法，需找到时间格式变化原因 socket传数据对时间格式的处理-wz
      refundOrder.addTime = formatDate2(refundOrder.addTime)
      if (refundOrder) {
        this.updateOrderItemStatus(refundOrder)
      }
    },
    confirmOrderEvent:function(ov, nv) {
      const confirmOrder = this.$store.state.order.confirmOrder
      // TODO 临时写法，需找到时间格式变化原因 socket传数据对时间格式的处理-wz
      confirmOrder.addTime = formatDate2(confirmOrder.addTime)
      if (confirmOrder) {
        this.updateOrderItemStatus(confirmOrder)
      }
    }
  },
  mounted:function() {
  },
  methods:{
    handleOrderOptionCommand(command) {
      const orderData = command.itemData
      this.dataForm.id = orderData.id
      switch (command.type) {
        case 'acceptOrder':
          this.sendDialogFormVisible = true
          break
        case 'callRider':
          callDadaRider({ 'orderId':parseInt(orderData.id) }).then(response => {
            const responseData = response.data
            if (responseData.errno === 0) {
              // 修改订单的状态
              orderData.orderStatus = 'ONGOING'
              // 修改配送状态为待接单
              orderData.tpdStatusMsg = '待接单'
              this.$notify({
                title:'成功',
                message:responseData.errmsg,
                type:'success'
              })
            } else {
              this.$notify({
                title:'失败',
                message:responseData.errmsg,
                type:'error'
              })
            }
          })
          break
        case 'confirmCancelAndRefund':
          this.refundAndCancleDialogFormVisible = true
          break
      }
    },
    getList() {
      this.listLoading = true
      listOrder(this.listQuery).then(response => {
        this.list = response.data.data.items
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    resetForm(row) {
      this.dataForm.id = row.id
      this.dataForm.shipChannel = row.shipChannel
      this.dataForm.shipSn = row.shipSn
      this.dataForm.shipStartTime = row.shipStartTime
      this.dataForm.shipEndTime = row.shipEndTime
      this.dataForm.orderStatus = row.orderStatus
    },
    merchantAccept() {
      // 更改当前的订单的状态为发货状态
      updateOrderCode({ orderId:this.dataForm.id, orderStatus:STATUS.MERCHANT_ACCEPT }).then(response => {
        const responseData = response.data
        this.sendDialogFormVisible = false
        if (responseData.errno !== 0) {
          this.$notify({
            title:'失败',
            message:responseData.errmsg,
            type:'error'
          })
          return
        }
        this.updateOrderItemStatus(responseData.data)
        this.$notify({
          title:'成功',
          message:response.data.errmsg,
          type:'success'
        })
      })
    },
    merchantConfirmCancelAndRefund() {
      this.refundAndCancleDialogFormVisible = false
      const loading = this.$loading({
        lock:true,
        text:'正在努力退款中',
        spinner:'el-icon-loading',
        background:'rgba(0, 0, 0, 0.7)'
      })
      // 更改当前的订单的状态为完成状态
      refundOrder(this.dataForm).then(response => {
        loading.close()
        const responseData = response.data
        if (responseData.errno !== 0) {
          this.$notify({
            title:'失败',
            message:responseData.errmsg,
            type:'error'
          })
          return
        }
        const updatedOrder = responseData.data
        this.$notify({
          title:'成功',
          message:'取消成功, 并将钱已退回客户钱包',
          type:'success'
        })
        this.updateOrderItemStatus(updatedOrder)
      })
    },
    handleDownload() {
      this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = ['订单ID', '订单编号', '用户ID', '订单状态', '是否删除', '收货人', '收货联系电话', '收货地址']
          const filterVal = ['id', 'orderSn', 'userId', 'orderStatis', 'isDelete', 'consignee', 'mobile', 'address']
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '订单信息')
          this.downloadLoading = false
        })
    },
    updateOrderItemStatus(updatedOrder) {
      for (const v of this.list) {
        if (v.order.id === updatedOrder.id) {
          const index = this.list.indexOf(v)
          const newObj = {
            goods:v.goods,
            order:updatedOrder,
            sendBtnStatus:updatedOrder.sendBtnStatus,
            cancelBtnStatus:updatedOrder.cancelBtnStatus,
            confirmBtnStatus:updatedOrder.confirmBtnStatus,
            refundBtnStatus:updatedOrder.refundBtnStatus
          }
          this.list.splice(index, 1, newObj)
          break
        }
      }
    },
    viewOrderDetail(currentData) {
      // 获取订单的详细信息
      // 重置配送信息
      this.deliveryDetailsDialogData = []

      if (currentData.order) {
        getOrderDetail({ 'id':currentData.order.id }).then(response => {
          const data = response.data.data
          this.updateOrderItemStatus(data.order)
          this.showDeliveryDetail = true
          this.deliveryDetailsDialogData = [
            { 'key':'配送编号', 'value':data.deliveryDetail.dmId },
            { 'key':'配送员', 'value':data.deliveryDetail.dmName },
            { 'key':'配送员手机号', 'value':data.deliveryDetail.dmMobile },
            { 'key':'配送费', 'value':data.deliveryDetail.deliverFee },
            { 'key':'配送状态', 'value':data.order.tpdStatusMsg },
            { 'key':'取消来源', 'value':data.deliveryDetail.cancelFrom },
            { 'key':'取消原因', 'value':data.deliveryDetail.cancelReason }
          ]
        })
      }
    }
  }
}

</script>
