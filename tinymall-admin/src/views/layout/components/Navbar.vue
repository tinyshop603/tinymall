<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger class="hamburger-container" :toggleClick="toggleSideBar" :isActive="sidebar.opened"></hamburger>

    <breadcrumb class="breadcrumb-container"></breadcrumb>

    <div class="right-menu">

      <el-tooltip effect="dark" content="全屏" placement="bottom">
        <screenfull class="screenfull right-menu-item"></screenfull>
      </el-tooltip>

      <el-dropdown class="avatar-container right-menu-item" trigger="click">
        <div class="avatar-wrapper">
          <img class="user-avatar" :src="avatar+'?imageView2/1/w/80/h/80'">
          <i class="el-icon-caret-bottom"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>
            <router-link to="/">
              主页
            </router-link>
          </el-dropdown-item>
          <el-dropdown-item divided>
            <span @click="logout" style="display:block;">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <audio id="bgMusic">
        <source src="../../../media/eleme.mp3" type="audio/mp3">
      </audio>
    </div>
  </el-menu>
</template>
<script>
import { mapGetters } from 'vuex'
import store from '@/store'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import {
  getLodop,
  printCredential
} from '@/api/printer'

export default {
  components:{
    Breadcrumb,
    Hamburger,
    Screenfull
  },
  computed:{
    ...mapGetters([
      'sidebar',
      'name',
      'avatar'
    ])
  },
  mounted:function() {
    const _this = this
    this.$nextTick(function() {
      _this.player = document.getElementById('bgMusic')
    })
  },
  sockets:{
    connect:function() {
      console.log('socket connected')
    },
    submitOrderEvent:function(jsonData) {
      const socData = JSON.parse(jsonData)
      console.log(socData)
      if (socData.storeUserName == store.getters.name) {
        const newOrder = socData.orderData
        this.player.play()
        const credentialData = {
          'shopName':'789便利店',
          'shopQRurl':'https://www.bjguangchi.top/static/789shop-b.png',
          'payStyle':'在线支付(已支付)',
          'orderNO':`订单编号:${newOrder.order.orderSn}`,
          'orderTime':`下单时间:${new Date(newOrder.order.addTime).toLocaleString('chinese', { hour12:false })}`,
          'buyGoods':newOrder.goods.map(good => ({
            'name':good.goodsName,
            'number':good.number,
            'price':good.retailPrice
          })),
          'others':[{
            'name':'配送费',
            'value':'0元'
          }],
          'orignTotalPrice':newOrder.order.orderPrice,
          'trueTotalPrice':newOrder.order.actualPrice,
          'customerInfo':{
            'name':newOrder.order.consignee,
            'phoneNumber':newOrder.order.mobile,
            'address':newOrder.order.address
          }
        }
        setTimeout(() =>
          printCredential(getLodop(), store.getters.printerIndex, credentialData),
        this.player.duration * 1000 + 500)
      }
    },
    cancelOrderEvent:function(jsonData) {
      console.log('----->订单取消' + jsonData)
    }
  },
  methods:{
    toggleSideBar() {
      this.$store.dispatch('toggleSideBar')
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload() // In order to re-instantiate the vue-router object to avoid bugs
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;
  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }
  .breadcrumb-container{
    float: left;
  }
  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }
  .right-menu {
    float: right;
    height: 100%;
    &:focus{
     outline: none;
    }
    .right-menu-item {
      display: inline-block;
      margin: 0 8px;
    }
    .screenfull {
      height: 20px;
    }
    .avatar-container {
      height: 50px;
      margin-right: 30px;
      .avatar-wrapper {
        cursor: pointer;
        margin-top: 5px;
        position: relative;
        .user-avatar {
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }
        .el-icon-caret-bottom {
          display:none;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
