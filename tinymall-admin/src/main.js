import Vue from 'vue'

import 'normalize.css/normalize.css'// A modern alternative to CSS resets

import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'
import VueSocketio from 'vue-socket.io'
import './icons' // icon
import './permission' // permission control
// import './mock' // simulation data

Vue.use(Element, {
  size: 'medium' // set element-ui default size
})

Vue.config.productionTip = false
Vue.use(VueSocketio, 'http://172.16.6.86:7081?clientId=admin-api')
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
