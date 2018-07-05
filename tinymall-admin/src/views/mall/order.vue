<template>
  <div class="app-container calendar-list-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID" v-model="listQuery.userId"></el-input>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入订单编号" v-model="listQuery.orderSn"></el-input>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" v-waves icon="el-icon-download" @click="handleDownload" :loading="downloadLoading">导出订单信息</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table size="small" :data="list" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table size="small" :data="props.row.goods" v-loading="listLoading" element-loading-text="正在查询中。。。" border fit highlight-current-row>
          <el-table-column align="center" min-width="90px" label="商品条形码" prop="goodsSn"></el-table-column>
          <el-table-column align="center" min-width="90px" label="商品名称" prop="goodsName"></el-table-column>
          <el-table-column align="center" min-width="90px" label="商品数量" prop="number"></el-table-column>
          <el-table-column align="center" min-width="90px" label="商品规格" prop="goodsSpecificationValues"></el-table-column>
          <el-table-column align="center" min-width="90px" label="商品单价" prop="retailPrice"></el-table-column>
          </el-table>                    
        </template>
      </el-table-column>
      <el-table-column align="center" width="100px" label="下单时间" prop="order.addTime" sortable></el-table-column>
      <el-table-column align="center" min-width="100px" label="用户昵称" prop="order.consignee"></el-table-column>
      <el-table-column align="center" min-width="100px" label="用户地址" prop="order.address"></el-table-column>
      <el-table-column align="center" min-width="100px" label="用户电话" prop="order.orderStatus"></el-table-column>
      <el-table-column align="center" v-if="isShowDeleteColumn" min-width="100px" label="是否删除" prop="order.isDelete">
        <template slot-scope="scope">
          <el-tag :type="scope.row.order.isDelete ? 'success' : 'error' "> { { scope.row.order.isDelete ? '未删除': '已删除' } }
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" min-width="100px" label="订单费用" prop="order.orderPrice"></el-table-column>
      <el-table-column align="center" min-width="100px" label="实际费用" prop="order.actualPrice"></el-table-column>
      <el-table-column align="center" label="操作" width="280" class-name="small-padding fixed-width" prop="type">
        <template slot-scope="scope">
          <el-button :type="scope.row.sendBtnStatus.type" :disabled="scope.row.sendBtnStatus.disabled" size="small" @click="handleSend(scope.row.order)">发货</el-button>
          <el-button :type="scope.row.cancelBtnStatus.type" :disabled="scope.row.cancelBtnStatus.disabled" size="small" @click="handleCancelOrder(scope.row.order)">取消订单</el-button>
          <el-button :type="scope.row.confirmBtnStatus.type" :disabled="scope.row.confirmBtnStatus.disabled" size="small" @click="handleConfirmOrder(scope.row.order)">确认订单</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="listQuery.page" :page-sizes="[10,20,30,50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
    </div>
    <!-- 发货对话框 -->
    <el-dialog title="发货" :visible.sync="sendDialogFormVisible">
      确认发货后，您必须在30分钟内为客户送货到门，是否继续发货？
      <div slot="footer" class="dialog-footer">
        <el-button @click="sendDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="sendData">确定</el-button>
      </div>
    </el-dialog>
    <!-- 取消订单对话框 -->
    <el-dialog title="取消订单" :visible.sync="cancelSendDialogFormVisible">
      取消订单前，请确保已跟客户进行电话沟通，与客户协商后才可进行</br>
      取消，否则会影响您的信誉，并且，订单取消后将无法对该订单做任</br>
      何操作，确认继续取消？
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelSendDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="cancelData">确定</el-button>
      </div>
    </el-dialog>
    <!-- 收货对话框 -->
    <el-dialog title="确定完成订单" :visible.sync="recvDialogFormVisible">
      确定订单完成前,请确认已经完成送货到门,并且已完成线下收付款,</br>
      订单确认完成后不可再对该订单做任何操作,继续确认完成订单?</br>
      <div slot="footer" class="dialog-footer">
        <el-button @click="recvDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="recvData">确定</el-button>
      </div>
    </el-dialog>
    <audio id="bgMusic">
      <source src="../../media/eleme.mp3" type="audio/mp3">
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
import { listOrder, getBtnStateByCode, updateOrderCode, STATUS } from '@/api/order'
import waves from '@/directive/waves' // 水波纹指令
export default {
  name: 'Order',
  directives: {
    waves
  },
  data() {
    return {
      isShowDeleteColumn: false, // 是否显示订单表格删除状态的列
      list: undefined,
      player: undefined, // 后台音频播放器
      total: undefined,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        sort: '+id'
      },
      dataForm: {
        id: undefined,
        shipChannel: undefined,
        shipSn: undefined,
        shipStartTime: undefined,
        shipEndTime: undefined,
        orderStatus: undefined
      },
      sendDialogFormVisible: false,
      recvDialogFormVisible: false,
      cancelSendDialogFormVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  sockets: {
    connect: function() {
      console.log('socket connected')
    },
    submitOrderEvent: function(jsonData) {
      let newOrder = JSON.parse(jsonData)
      newOrder = Object.assign(newOrder, getBtnStateByCode(newOrder.orderStatus))
      this.list.unshift(newOrder)
      this.player.play()
    },
    cancelOrderEvent: function(jsonData) {
      console.log('----->订单取消' + jsonData)
    }
  },
  mounted: function() {
    const _this = this
    this.$nextTick(function() {
      _this.player = document.getElementById('bgMusic')
    })
  },
  methods: {
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
    handleSend(row) {
      this.resetForm(row)
      this.sendDialogFormVisible = true
      // this.$nextTick(() => {
      //   this.$refs['dataForm'].clearValidate()
      // })
    },
    sendData() {
      /**
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateOrder(this.dataForm).then(response => {
            const updatedOrder = response.data.data
            for (const v of this.list) {
              if (v.id === updatedOrder.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, updatedOrder)
                break
              }
            }
            this.sendDialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
      **/
      // 更改当前的订单的状态为发货状态
      this.dataForm.orderStatus = STATUS.SHIP
      updateOrderCode(this.dataForm).then(response => {
        const updatedOrder = response.data.data
        this.updateOrderItemStatus(updatedOrder)
        this.sendDialogFormVisible = false
        this.$notify({
          title: '成功',
          message: '发货成功,请尽快送货',
          type: 'success',
          duration: 2000
        })
      })
    },
    handleRecv(row) {
      this.resetForm(row)
      this.recvDialogFormVisible = true
      // this.$nextTick(() => {
      //   this.$refs['dataForm'].clearValidate()
      // })
    },
    handleCancelOrder(row) {
      this.resetForm(row)
      this.cancelSendDialogFormVisible = true
      // this.$nextTick(() => {
      //   this.$refs['dataForm'].clearValidate()
      // })
    },
    handleConfirmOrder(row) {
      this.resetForm(row)
      this.recvDialogFormVisible = true
      // this.$nextTick(() => {
      //   this.$refs['dataForm'].clearValidate()
      // })
    },
    recvData() {
      // 更改当前的订单的状态为完成状态
      this.dataForm.orderStatus = STATUS.RECEIVE_COMPLETE
      updateOrderCode(this.dataForm).then(response => {
        const updatedOrder = response.data.data
        this.updateOrderItemStatus(updatedOrder)
        this.recvDialogFormVisible = false
        this.$notify({
          title: '成功',
          message: '订单已完成',
          type: 'success',
          duration: 2000
        })
      })
    },
    cancelData() {
      // 更改当前的订单的状态为取消状态
      this.dataForm.orderStatus = STATUS.CANCEL
      updateOrderCode(this.dataForm).then(response => {
        const updatedOrder = response.data.data
        this.updateOrderItemStatus(updatedOrder)
        this.cancelSendDialogFormVisible = false
        this.$notify({
          title: '成功',
          message: '取消成功',
          type: 'success',
          duration: 2000
        })
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
            goods: v.goods,
            order: updatedOrder,
            sendBtnStatus: updatedOrder.sendBtnStatus,
            cancelBtnStatus: updatedOrder.cancelBtnStatus,
            confirmBtnStatus: updatedOrder.confirmBtnStatus
          }
          this.list.splice(index, 1, newObj)
          break
        }
      }
    }
  }
}

</script>
