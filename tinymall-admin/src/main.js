import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'
import VueSocketio from 'vue-socket.io'
import io from 'socket.io-client'
import './icons' // icon
import './permission' // permission control
// import './mock' // simulation data

Vue.use(Element, {
  size: 'medium' // set element-ui default size
})
/**
const sck = io('http://39.106.9.50:8777?clientId=admin-api', {
  reconnectionAttempts: Number.MAX_SAFE_INTEGER,
  reconnectionDelay: 500,
  reconnectionDelayMax: 1000,
  timeout: 1000,
  autoConnect: true
})
**/
const sck = io('https://www.bjguangchi.top/socket.io/?clientId=admin-api', {
  reconnectionAttempts: Number.MAX_SAFE_INTEGER,
  reconnectionDelay: 7000,
  reconnectionDelayMax: 7000,
  timeout: 7000,
  autoConnect: true
})
Vue.use(VueSocketio, sck)
Vue.config.productionTip = false
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
