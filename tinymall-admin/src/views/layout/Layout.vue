<template>

  <div class="app-wrapper" :class="{hideSidebar:!sidebar.opened}">
    <sidebar class="sidebar-container"></sidebar>
    <div class="main-container">
      <div id ="nav"></div>
      <tags-view></tags-view>
      <app-main></app-main>
    </div>
  </div>
</template>
<script>
import Vue from 'vue'
import { Navbar, Sidebar, AppMain, TagsView } from './components'
import VueSocketio from 'vue-socket.io'
import io from 'socket.io-client'
import store from '@/store'
export default {
  name:'layout',
  beforeCreate:function() {
    const socketUrl = `${process.env.SOCKET_URL}/?clientId=admin-api-${store.getters.adminId}`
    const sck = io(socketUrl, {
      reconnectionAttempts:Number.MAX_SAFE_INTEGER,
      reconnectionDelay:7000,
      reconnectionDelayMax:7000,
      timeout:7000,
      autoConnect:true
    })
    Vue.use(VueSocketio, sck)
  },
  components:{
    Sidebar,
    AppMain,
    TagsView
  },
  mounted:function() {
    new Vue({
      el:'#nav',
      store,
      template:'<Navbar/>',
      components:{ Navbar }
    })
  },
  computed:{
    sidebar() {
      return this.$store.state.app.sidebar
    }
  }
}

</script>
<style rel="stylesheet/scss" lang="scss" scoped>
@import "src/styles/mixin.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;
}

</style>
