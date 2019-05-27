import request from '@/utils/request'

export const STATUS = {
  PENDING_PAYMENT:'PENDING_PAYMENT',
  SYSTEM_AUTO_CANCEL:'SYSTEM_AUTO_CANCEL',
  ONGOING:'ONGOING',
  COMPLETE:'COMPLETE',
  REFUND_COMPLETE:'REFUND_COMPLETE',
  MERCHANT_ACCEPT:'MERCHANT_ACCEPT',
  MERCHANT_SHIP:'MERCHANT_SHIP',
  MERCHANT_CANCEL:'MERCHANT_CANCEL',
  MERCHANT_REFUNDING:'MERCHANT_REFUNDING',
  CUSTOMER_CANCEL:'CUSTOMER_CANCEL'
}
export function listOrder(query) {
  request.interceptors.response.use(response => {
    if (response.data && response.data.data) {
      let orderItems = response.data.data.items
      if (!orderItems) {
        orderItems = [response.data.data]
      }
    }
    return response
  })
  return request({
    url:'/order/list',
    method:'get',
    params:query
  })
}

export function callDadaRider(data) {
  return request({
    url:'/dada/order',
    method:'post',
    data
  })
}

export function createOrder(data) {
  return request({
    url:'/order/create',
    method:'post',
    data
  })
}
export function readOrder(data) {
  return request({
    url:'/order/read',
    method:'get',
    params:data
  })
}

/**
 * 获取订单详情
 * 包含配送的全部信息
 * @param data
 */
export function getOrderDetail(data) {
  return request({
    url:'/order/detail',
    method:'get',
    params:data
  })
}

export function updateOrder(data) {
  return request({
    url:'/order/update',
    method:'post',
    data
  })
}
/**
 * [updateOrder 更新订单信息的状态吗]
 * @author zhaoguiyang 2018-07-01
 * @param  {[type]} data [description]
 * @return {[type]}      [description]
 */
export function updateOrderCode(data) {
  return request({
    url:'/order/update/status/',
    method:'post',
    data
  })
}

/**
 * [updateOrder 确认退款]
 * @author wz 2018-11-012
 * @param  {[type]} data [description]
 * @return {[type]}      [description]
 */
export function refundOrder(data) {
  return request({
    url:'/order/refundConfirm/',
    method:'post',
    data:{
      orderId:data.id
    }
  })
}

export function deleteOrder(data) {
  return request({
    url:'/order/delete',
    method:'post',
    data
  })
}
export function formatDate(date, fmt) {
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  const o = {
    'M+':date.getMonth() + 1,
    'd+':date.getDate(),
    'h+':date.getHours(),
    'm+':date.getMinutes(),
    's+':date.getSeconds()
  }
  for (const k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      const str = o[k] + ''
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str))
    }
  }
  return fmt
}

function padLeftZero(str) {
  return ('00' + str).substr(str.length)
}
export function formatDate2(date) {
  let fmt = 'year:month:day Thour:minute:second'
  fmt = fmt.replace('year', date.year)
  fmt = fmt.replace('month', date.monthValue)
  fmt = fmt.replace('day', date.dayOfMonth)
  fmt = fmt.replace('hour', date.hour)
  fmt = fmt.replace('minute', date.minute)
  fmt = fmt.replace('second', date.second)
  return fmt
}
